package com.birthdaywisher.server.election;

import java.net.InetAddress;
import java.net.Socket;

public class SendMessageTask extends Thread{
    private Server server;
    private String msg;
    public SendMessageTask(Server server, String msg) {
        this.server = server;
        this.msg = msg;
    }

    @Override
    public void run() {

        try {
            server.setSuccPort(5000);
            InetAddress address = InetAddress.getByName("127.0.0.1");
            Socket s = new Socket(address, server.getSuccPort());
            server.setSuccSocket(s);
            System.out.println("Connected to server 1: " + s.isConnected());
            server.sendMessage(this.msg);

        } catch (Exception e) {
            System.out.println(e);
        }


    }


}
