import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Date;


public class client {
    private static final int PORT=9001;
    private static final int ASSIGN_PORT=9004;
    public static void createAndShowGUI() throws UnknownHostException {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame=new JFrame("文件传输");
        frame.setSize(400,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel1=new JPanel();
        JLabel label=new JLabel("发送的文件");
        panel1.add(label);
        JTextField text=new JTextField(20);
        text.setBounds(10,40,120,25);
        JPanel panel2=new JPanel();
        JFileChooser filechooser=new JFileChooser("D:\\");
        filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        panel2.add(filechooser);
        frame.add(panel2,BorderLayout.CENTER);
        JPanel panel3=new JPanel();
        JLabel labels=new JLabel("文件大小");
        panel3.add(labels);
        JTextField size=new JTextField(10);
        panel3.add(size);
        JLabel label2=new JLabel("IP地址");
        panel3.add(label2);
        JTextField ipText=new JTextField(20);
        panel3.add(ipText);
        JButton btn=new JButton("发送");
        panel3.add(btn);
        frame.add(panel3, BorderLayout.SOUTH);
        InetAddress addr=InetAddress.getLocalHost();
        ipText.setText(addr.getHostAddress());
        frame.pack();
        frame.setVisible(true);
        filechooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println(filechooser.getSelectedFile().length());
                size.setText(filechooser.getSelectedFile().length()/1024+"KB");
            }
        });
        btn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println(text.getText());
                InputStream in = null;
                Socket socket = null;
                try {
                    File file = new File(filechooser.getSelectedFile().getPath());
                    System.out.println(file.length());
                    size.setText(file.length()/1024+"KB");
                    if(file.exists() && file.isFile()) {
                        in = new FileInputStream(file);
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(ipText.getText(), ASSIGN_PORT));
                        OutputStream out = socket.getOutputStream();
                        out.write(file.getName().getBytes());
                        out.write("?".getBytes());
                        out.write(ipText.getText().getBytes());
                        out.write("?".getBytes());
                        out.write(String.valueOf(PORT).getBytes());
                        out.write("?".getBytes());
                        Date date = new Date();
                        out.write(String.valueOf(date.getTime()).getBytes());
                        out.write("?".getBytes());
                        byte[] data=new byte[8];
                        in.read(data);
                        out.write(data,0,8);
                        out.write("?".getBytes());
                        out.write(String.valueOf(file.length()).getBytes());
                        System.out.println("请等待分配文件服务器...");
                        ServerSocket server=new ServerSocket(PORT);
                        Socket sk=server.accept();
                        System.out.println("服务器分配完成...");
                        DataInputStream dis = null;
                        FileOutputStream fos = null;
                        dis=new DataInputStream(sk.getInputStream());
                        byte[] input=new byte[1];
                        String str="";
                        while(dis.read(input,0,1)>0){
                            str+=new String(input);
                        }
                        System.out.println(str);
                        String[] arr=str.split("[?]");
                        sk.connect(new InetSocketAddress(arr[0],Integer.parseInt(arr[1])));
                        OutputStream os=sk.getOutputStream();
                        byte[] datas = new byte[1024];
                        int i = 0;
                        while((i = in.read(data)) != -1) {
                            os.write(data, 0, i);
                        }
                        System.out.println("传输文件完成");
                    }else {
                        System.out.println("文件不存在或者一个文件~~");
                    }
                } catch (Exception exp) {
                   System.out.println(exp);
                }finally {
                    try {
                        in.close();
                    } catch (IOException exp) {
                        exp.printStackTrace();
                    }finally {
                        // 强制将输入流置为空
                        in = null;
                    }
                    try {
                        socket.close();
                    } catch (IOException exp) {
                        exp.printStackTrace();
                    }finally {
                        // 强制释放socket
                        socket = null;
                    }

                }

            }
        });
    }

    public static void main(String[] args) throws IOException {
        createAndShowGUI();
    }
}
