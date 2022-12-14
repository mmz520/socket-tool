package org.mian.socketTool.thread;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.mian.socketTool.util.DecoderUtils.bytesToHexString;

public class UdpRunnable implements Runnable{
    private DatagramSocket socket;
    private Boolean hexOut=false;

    private Boolean running=false;

    private Boolean showMsg=true;

    public UdpRunnable(DatagramSocket socket) {
        this.socket = socket;
    }
    public void start(){
        this.running=true;
        Thread thread=new Thread(this);
        thread.start();
        System.out.println(socket.getLocalAddress()+":"+socket.getLocalPort()+"开始监听");
    }
    public void stop(){
        this.running=false;
        this.socket.close();
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
    @Override
    public void run() {
        while (running){
            byte[] buffer=new byte[1024];
            DatagramPacket packet=new DatagramPacket(buffer,buffer.length);
            try {
                socket.receive(packet);
                int length=packet.getLength();
                if(length>0){
                    byte[] res=new byte[packet.getLength()];
                    System.arraycopy(buffer,0,res,0,res.length);
                    String host=socket.getRemoteSocketAddress().toString().replace("/","");
                    if(showMsg){
                        if(hexOut){
                            System.out.println(host+":"+bytesToHexString(res));
                        }else {
                            System.out.println(host+":"+new String(res, StandardCharsets.UTF_8));
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("UDP可能已经断开");
            }
        }
    }
}
