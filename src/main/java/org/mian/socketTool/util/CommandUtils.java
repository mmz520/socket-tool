package org.mian.socketTool.util;

import org.mian.socketTool.thread.ClientRunnable;
import org.mian.socketTool.thread.ServerRunnable;
import org.mian.socketTool.thread.UdpRunnable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class CommandUtils {
    private final static String tipTcpServer ="TCP服务命令列表（按ENTER确认）\n" +
            "1、send [message] [host]     发送消息\n" +
            "2、sendHex [message] [host]  发送16进制消息\n" +
            "3、msg hex                   16进制展示消息\n" +
            "4、msg normal                正常展示消息\n" +
            "5、clients                   查看客户端列表\n" +
            "6、display msg               关闭接收消息显示\n" +
            "7、play msg                  开启接收消息显示\n" +
            "8、help                      查看命令列表\n" +
            "9、over                      关闭TCP服务";

    private final static String tipTcpClient ="TCP客户端命令列表（按ENTER确认）\n" +
            "1、send [message]            发送消息\n" +
            "2、sendHex [message]         发送16进制消息\n" +
            "3、msg hex                   16进制展示消息\n" +
            "4、msg normal                正常展示消息\n" +
            "5、display msg               关闭接收消息显示\n" +
            "6、play msg                  开启接收消息显示\n" +
            "7、help                      查看命令列表\n" +
            "8、over                      关闭TCP客户端";

    private final static String tipUdp ="UDP命令列表（按ENTER确认）\n" +
            "1、send [message] [host]     发送消息\n" +
            "2、sendHex [message] [host]  发送16进制消息\n" +
            "3、msg hex                   16进制展示消息\n" +
            "4、msg normal                正常展示消息\n" +
            "5、display msg               关闭接收消息显示\n" +
            "6、play msg                  开启接收消息显示\n" +
            "7、help                      查看命令列表\n" +
            "8、over                      关闭TCP客户端";
    public static void chooseServer(Scanner scanner){
        System.out.println("选择命令（按ENTER确认）\n" +
                "1、TCP服务\n" +
                "2、TCP客户端\n" +
                "3、UDP\n" +
                "4、退出");
        String cmd=scanner.nextLine();
        if(!StringUtils.isEmptyOrBlank(cmd)&&(cmd.equals("1")||cmd.equals("2")||cmd.equals("3")||cmd.equals("4"))){
            switch (cmd){
                case "1":
                    inputPort(scanner,true);
                    break;
                case "2":
                    inputHost(scanner);
                    break;
                case "3":
                    inputPort(scanner,false);
                    break;
                default:
                    chooseServer(scanner);
                    break;
            }
        }
        else {
            chooseServer(scanner);
        }
    }
    public static void inputHost(Scanner scanner){
        System.out.println("请输入TCP服务IP");
        String remoteIp=scanner.nextLine();
        System.out.println("请输入TCP服务端口");
        String port=scanner.nextLine();
        if(StringUtils.isEmptyOrBlank(remoteIp)||!StringUtils.isNumeric(port)||port.contains(".")){
            inputHost(scanner);
        }else {
            try {
                Socket socket=new Socket(remoteIp,Integer.parseInt(port));
                System.out.println(socket.getLocalAddress()+":"+socket.getLocalPort()+" TCP客户端创建成功");
                ClientRunnable runnable=new ClientRunnable(socket);
                runnable.start();
                tcpClientCmd(scanner,socket,runnable,true);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                inputHost(scanner);
            }
        }
    }
    public static void inputPort(Scanner scanner,boolean tcp){
        System.out.println("请输入端口号：");
        String cmd=scanner.nextLine();
        if(StringUtils.isNumeric(cmd)&&!cmd.contains(".")){
            if(tcp){
                try {
                    ServerSocket serverSocket=new ServerSocket(Integer.parseInt(cmd));
                    String host=serverSocket.getInetAddress().getHostAddress()+":"+serverSocket.getLocalPort();
                    System.out.println(host+" TCP服务创建成功");
                    ServerRunnable runnable=new ServerRunnable(serverSocket);
                    runnable.start();
                    tcpServerCmd(scanner,serverSocket,runnable,true);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    inputPort(scanner,true);
                }
            }
            else {
                try {
                    DatagramSocket socket=new DatagramSocket(Integer.parseInt(cmd));
                    System.out.println(socket.getLocalAddress()+":"+socket.getLocalPort()+" UDP创建成功");
                    UdpRunnable runnable=new UdpRunnable(socket);
                    runnable.start();
                    udpCmd(scanner,socket,runnable,true);
                } catch (SocketException e) {
                    System.out.println(e.getMessage());
                    inputPort(scanner,false);
                }
            }
        }
        else {
            inputPort(scanner,tcp);
        }
    }
    public static void udpCmd(Scanner scanner,DatagramSocket socket,UdpRunnable runnable,boolean showCmd){
        if(showCmd){
            System.out.println(tipUdp);
        }
        boolean over=false;
        String cmd=scanner.nextLine();
        if(StringUtils.isEmptyOrBlank(cmd)) return;
        if(cmd.trim().startsWith("send ")){
            sendCmd(cmd,socket,false);
        }
        if(cmd.trim().startsWith("sendHex ")){
            sendCmd(cmd,socket,true);
        }
        if(cmd.trim().equals("msg hex")){
            runnable.setHexOut(true);
        }
        if(cmd.trim().equals("msg normal")){
            runnable.setHexOut(false);
        }

        if(cmd.trim().equals("display msg")){
            runnable.setShowMsg(false);
        }
        if(cmd.trim().equals("play msg")){
            runnable.setShowMsg(true);
        }
        if(cmd.trim().equals("help")){
            System.out.println(tipUdp);
        }
        if(cmd.trim().equals("over")){
            over=true;
            runnable.stop();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            chooseServer(scanner);
        }
        System.out.println("请输入UDP命令:");
        if(!over)
            udpCmd(scanner,socket,runnable,false);
    }
    public static void tcpClientCmd(Scanner scanner, Socket socket, ClientRunnable runnable, boolean showCmd){
        if(showCmd){
            System.out.println(tipTcpClient);
        }
        boolean over=false;
        String cmd=scanner.nextLine();
        if(StringUtils.isEmptyOrBlank(cmd)) return;
        if(cmd.trim().startsWith("send ")){
            sendCmd(cmd,socket,false);
        }
        if(cmd.trim().startsWith("sendHex ")){
            sendCmd(cmd,socket,true);
        }
        if(cmd.trim().equals("msg hex")){
            runnable.setHexOut(true);
        }
        if(cmd.trim().equals("msg normal")){
            runnable.setHexOut(false);
        }

        if(cmd.trim().equals("display msg")){
            runnable.setShowMsg(false);
        }
        if(cmd.trim().equals("play msg")){
            runnable.setShowMsg(true);
        }
        if(cmd.trim().equals("help")){
            System.out.println(tipTcpClient);
        }
        if(cmd.trim().equals("over")){
            over=true;
            runnable.stop();
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            chooseServer(scanner);
        }
        System.out.println("请输入TCP客户端命令:");
        if(!over)
            tcpClientCmd(scanner,socket,runnable,false);
    }
    public static void tcpServerCmd(Scanner scanner, ServerSocket serverSocket, ServerRunnable runnable, boolean showCmd){
        if(showCmd){
            System.out.println(tipTcpServer);
        }
        boolean over=false;
        String cmd=scanner.nextLine();
        if(StringUtils.isEmptyOrBlank(cmd)) return;
        if(cmd.trim().startsWith("send ")){
            sendCmd(cmd,runnable,false);
        }
        if(cmd.trim().startsWith("sendHex ")){
            sendCmd(cmd,runnable,true);
        }
        if(cmd.trim().equals("msg hex")){
            runnable.setHexOut(true);
        }
        if(cmd.trim().equals("msg normal")){
            runnable.setHexOut(false);
        }
        if(cmd.trim().equals("clients")){
            StringBuilder builder=new StringBuilder();
            for (Map.Entry<String, Socket> entry : runnable.getSocketMap().entrySet()) {
                builder.append(entry.getKey());
                builder.append("  ");
                builder.append(entry.getValue().isConnected()?"在线\r\n":"离线\r\n");
            }
            System.out.println(StringUtils.isEmptyOrBlank(builder.toString())?"无客户端连接":builder);
        }
        if(cmd.trim().equals("display msg")){
            runnable.setShowMsg(false);
        }
        if(cmd.trim().equals("play msg")){
            runnable.setShowMsg(true);
        }
        if(cmd.trim().equals("help")){
            System.out.println(tipTcpServer);
        }
        if(cmd.trim().equals("over")){
            over=true;
            runnable.stop();
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            chooseServer(scanner);
        }
        System.out.println("请输入TCP服务命令:");
        if(!over)
            tcpServerCmd(scanner,serverSocket,runnable,false);
    }

    private static void sendCmd(String cmd, ServerRunnable runnable, boolean hex){
        String[] array=cmd.trim().split(" ");
        if(array.length==3){
            String msg=array[1];
            String host=array[2].trim();
            Socket socket=runnable.getSocketMap().get(host);
            if(socket==null){
                System.out.println("客户端不存在");
            }
            else {
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(hex?DecoderUtils.hexStringToBytes(msg):msg.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    System.out.println("发送成功！");
                } catch (IOException e) {
                    System.out.println("发送失败");
                }
            }
        }
        else {
            System.out.println("命令有误");
        }
    }

    private static void sendCmd(String cmd, Socket socket, boolean hex){
        String[] array=cmd.trim().split(" ");
        if(array.length==2){
            String msg=array[1];
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(hex?DecoderUtils.hexStringToBytes(msg):msg.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                System.out.println("发送成功！");
            } catch (IOException e) {
                System.out.println("发送失败");
            }
        }
        else {
            System.out.println("命令有误");
        }
    }

    private static void sendCmd(String cmd, DatagramSocket socket, boolean hex){
        String[] array=cmd.trim().split(" ");
        if(array.length==3){
            String msg=array[1];
            String host=array[2].trim();
            String[] split = host.split(":");
            if(split.length==2&&StringUtils.isNumeric(split[1])&&!split[1].contains(".")){
                byte[] data=hex?DecoderUtils.hexStringToBytes(msg):msg.getBytes(StandardCharsets.UTF_8);
                try {
                    DatagramPacket packet=new DatagramPacket(data,data.length,InetAddress.getByName(split[0]),Integer.parseInt(split[1]));
                    socket.send(packet);
                    System.out.println("发送成功");
                } catch (UnknownHostException e) {
                    System.out.println("发送失败");
                } catch (IOException e) {
                    System.out.println("发送失败");
                }
            }
            else {
                System.out.println("命令有误");
            }
        }
        else {
            System.out.println("命令有误");
        }
    }
}
