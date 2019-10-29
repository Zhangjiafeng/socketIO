
import javafx.beans.binding.BooleanExpression;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.concurrent.ThreadPoolExecutor;


public class server implements Runnable {
    public JTextField text=new JTextField(15);
    public JButton btn;
    public boolean suspend=false;
    public static int flag=0;
    public static int[] PORTS={9005,9006,9007};
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
            final ServerSocket server = new ServerSocket(9004);
            server s=new server();
            Thread th = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            System.out.println("开始监听客户端的请求...");
                            Socket socket = server.accept();
                            System.out.println("有客户端连接...");
                            s.AssignServer(socket);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            });
            th.start(); //启动线程运行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

    }

    public void AssignServer(Socket socket) {
        byte[] inputByte = null;
        int length = 0;
        DataInputStream dis = null;
        FileOutputStream fos = null;
        StringBuffer sb=new StringBuffer();
        try {
            try {
                dis = new DataInputStream(socket.getInputStream());
                byte[] input = new byte[1];
                String name="";
                String path="D:\\recordLog.txt";
                File file=new File(path);
                if(!file.exists()){
                    file.createNewFile();
                }
                fos = new FileOutputStream(new File("D:\\recordLog.txt"),true);
                inputByte = new byte[1];
                String str="";
                while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
                    fos.write(inputByte, 0, length);
                    str=str+new String(inputByte);
                }
                String s=str.replaceAll("[?]"," ");
                System.out.println(s);
                FileWriter fw=null;
                fw=new FileWriter(file,true);
                BufferedWriter out=new BufferedWriter(fw);
                out.append(s+"\r\n",0,s.length()+2);
                out.close();
                String[] arr=str.split("[?]");
                System.out.println("完成日志写入");
                System.out.println("正在分配服务器");
                Socket sk=new Socket();
                sk.connect(new InetSocketAddress(arr[1], 9005));
                if(flag>=2){
                    flag=0;
                }else{
                    flag+=1;
                }
                System.out.println("当前分配服务器端口为"+PORTS[flag]);
                OutputStream outStream=sk.getOutputStream();
                outStream.write(arr[1].getBytes());
                outStream.write("?".getBytes());
                outStream.write(arr[2].getBytes());
                sk.close();
                outStream.close();
            } finally {
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

