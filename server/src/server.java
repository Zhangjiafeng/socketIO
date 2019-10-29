
import javafx.beans.binding.BooleanExpression;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.concurrent.ThreadPoolExecutor;


public class server implements Runnable {
    public JTextField text=new JTextField(15);
    public JButton btn;
    public boolean suspend=false;
    public static int PORT=9005;
    public synchronized void toSuspend(){
        suspend=true;
    }
    public synchronized void toResume(){
        notify();
        suspend=false;
    }
    public void createServerGUI() throws UnknownHostException{
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame=new JFrame("文件传输");
        frame.setSize(400,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel=new JPanel();
        JLabel label=new JLabel("已接收");
        panel.add(label);
        panel.add(text);
        JPanel panel2=new JPanel();
        JLabel label2=new JLabel("IP地址");
        panel2.add(label2);
        JTextField ipText=new JTextField(15);
        panel2.add(ipText);
        InetAddress addr=InetAddress.getLocalHost();
        ipText.setText(addr.getHostAddress());
        frame.add(panel, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.CENTER);
        btn=new JButton("中断");
        JPanel panel3=new JPanel();
        panel3.add(btn);
        frame.add(panel3,BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }
    public static void main(String[] args) throws UnknownHostException {

        try {
            final ServerSocket server = new ServerSocket(PORT);
            server s=new server();
            s.createServerGUI();
            final Thread[] th = {new Thread(new Runnable() {
                public void run() {

                    while (true) {
                        try {
                            s.btn.setEnabled(false);
                            System.out.println("开始监听分配服务器的请求...");
                            Socket socket = server.accept();
                            System.out.println("有任务分配过来...");
//                            s.btn.setEnabled(true);
                            s.receiveFile(socket);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            })};
            s.btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try{
                        s.btn.setEnabled(false);
                        th[0].stop();
                    }catch(Exception e){
                        System.out.println(e);
                    }finally {
                        th[0] =new Thread(new Runnable() {
                            public void run() {
                                while (true) {
                                    try {
                                        s.btn.setEnabled(false);
                                        System.out.println("开始监听分配服务器的请求...");
                                        Socket socket = server.accept();
                                        System.out.println("有任务分配过来...");
//                                        s.btn.setEnabled(true);
                                        s.receiveFile(socket);
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }
                                }
                            }
                        });
                        th[0].start();
                    }

                }
            });
            th[0].start(); //启动线程运行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

    }

    public void receiveFile(Socket socket) {
        byte[] inputByte = null;
        int length = 0;
        DataInputStream dis = null;
        FileOutputStream fos = null;
        StringBuffer sb=new StringBuffer();
        String str="";
        try {
            try {
                dis = new DataInputStream(socket.getInputStream());
                byte[] input = new byte[1];

                InetAddress addr=InetAddress.getLocalHost();
                String ip=addr.getHostAddress();
                byte[] ips=new byte[ip.length()];
                dis.read(ips,0,ip.length());
                String s=new String(ips);
                System.out.println("ip "+s+" "+ip);
                if(s.equals(ip)){
                    str+=new String(ips);
                    while(dis.read(input,0,1)>0){
                        str+=new String(input);
                    }
                    System.out.println(str);
                    String[] arr=str.split("[?]");
                    Socket sk=new Socket();
                    sk.connect(new InetSocketAddress(arr[0], Integer.parseInt(arr[1])));
                    OutputStream os=sk.getOutputStream();

                    os.write(ip.getBytes());
                    os.write("?".getBytes());
                    os.write(String.valueOf(PORT).getBytes());
                    System.out.println("正在去连接客户端...");
                }else{
                    System.out.println("客户端连接成功");
                    try {
                        try {
                            dis = new DataInputStream(socket.getInputStream());
                            String name="";
                            if(str.indexOf("?")!=-1){
                               name=str.substring(0,str.indexOf("?"));
                            }else{
                                name=str;
                                while(dis.read(input,0,1)>0){
                                    System.out.println(new String(input));
                                    if(new String(input).equals("?"))
                                        break;
                                    name+=new String(input);
                                }
                                System.out.println("name="+name);
                                fos = new FileOutputStream(new File("D:\\test\\1.txt"));
                                inputByte = new byte[1024];
                                System.out.println("开始接收数据...");
                                int total=0;
                                while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
                                    text.setText(total+"byte");
                                    total+=length;
                                    System.out.println(length);
                                    fos.write(inputByte, 0, length);
                                    fos.flush();
                                }
                                System.out.println("完成接收");
                            }


                        } finally {
                            if (fos != null)
                                fos.close();
                            if (dis != null)
                                dis.close();
                            if (socket != null)
                                socket.close();
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }


            } finally {
                if (fos != null)
                    fos.close();
                if (dis != null)
                    dis.close();
                if (socket != null)
                    socket.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}

