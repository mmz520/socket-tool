package org.mian.socketTool.thread;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mian.socketTool.util.DecoderUtils.bytesToHexString;

public class ServerRunnable implements Runnable{

    private Map<String,Socket> socketMap=new ConcurrentHashMap<>();
    private ServerSocket serverSocket;
    private Boolean hexOut=false;

    private Boolean running=false;

    private Boolean showMsg=true;

    public void setShowMsg(Boolean showMsg) {
        this.showMsg = showMsg;
        System.out.println(showMsg?"显示消息":"关闭消息显示");
    }

    public void setHexOut(Boolean hexOut) {
        this.hexOut = hexOut;
        System.out.println(hexOut?"消息修改为16进制显示":"消息修改为正常显示");
    }

    public Map<String, Socket> getSocketMap() {
        return socketMap;
    }

    public ServerRunnable(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void start(){
        this.running=true;
        Thread thread=new Thread(this);
        thread.start();
        System.out.println(serverSocket.getInetAddress().getHostAddress()+":"+serverSocket.getLocalPort()+"开始监听");
    }

    public void stop(){
        this.running=false;
        System.out.println(serverSocket.getInetAddress().getHostAddress()+":"+serverSocket.getLocalPort()+"关闭监听");
    }
    @Override
    public void run() {
        while (running){
            try {
                Socket socket=serverSocket.accept();
                String host=socket.getRemoteSocketAddress().toString().replace("/","");
                System.out.println("客户端"+host+"连接");
                socketMap.put(host,socket);
                Thread listener=new Thread(()->{
                    while (running){
                        try{
                            InputStream stream=socket.getInputStream();
                            int length = stream.available();
                            if(length>0){
                                byte[] bytes=new byte[length];
                                stream.read(bytes);
                                if(showMsg){
                                    if(hexOut){
                                        System.out.println(host+":"+bytesToHexString(bytes));
                                    }else {
                                        System.out.println(host+":"+new String(bytes, StandardCharsets.UTF_8));
                                    }
                                }
                            }
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            System.out.println("客户端可能已经离线");
                        }
                    }
                });
                listener.start();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("客户端可能已经离线");
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(serverSocket.getInetAddress().getHostAddress()+":"+serverSocket.getLocalPort()+"断开");
    }


}
