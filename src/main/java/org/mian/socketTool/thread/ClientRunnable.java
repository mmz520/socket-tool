package org.mian.socketTool.thread;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.mian.socketTool.util.DecoderUtils.bytesToHexString;

public class ClientRunnable implements Runnable{
    private Socket socket;
    private Boolean hexOut=false;

    private Boolean running=false;

    private Boolean showMsg=true;

    public void start(){
        this.running=true;
        Thread thread=new Thread(this);
        thread.start();
        System.out.println(socket.getLocalAddress()+":"+socket.getLocalPort()+"开始监听");
    }
    public void stop(){
        this.running=false;
        try {
            this.socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(socket.getLocalAddress()+":"+socket.getLocalPort()+"关闭");
    }

    public void setShowMsg(Boolean showMsg) {
        this.showMsg = showMsg;
        System.out.println(showMsg?"显示消息":"关闭消息显示");
    }

    public void setHexOut(Boolean hexOut) {
        this.hexOut = hexOut;
        System.out.println(hexOut?"消息修改为16进制显示":"消息修改为正常显示");
    }
    public ClientRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (running){
            try {
                InputStream stream = socket.getInputStream();
                int length = stream.available();
                String host=socket.getRemoteSocketAddress().toString().replace("/","");
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
                System.out.println("服务器可能关闭了");
            }
        }
    }
}
