package com.birthdaywisher.server.election;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ElectionTask extends Thread{
//    private Thread t;
    private String threadName;
    private Server server;

    public ElectionTask(Server server, String threadName) {
        this.server = server;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        System.out.println("Starting ElectionTask Thread: " + threadName);
        try {
            server.getServerSocket().setSoTimeout(180000);
            while (true) {
                try {
                    String msg = server.receiveMessage();
                    System.out.println("Receved Message: " + msg);
                    if (server.getPredId() == server.getLeaderId()) {   //server's predecessor is the leader
                        if (msg.equals("2")) {  //heartbeat message

                        } else {
                            server.election(msg);
                        }
                    } else {
                        server.election(msg);
                    }


                } catch (SocketTimeoutException ste) {
                    //no message received after some time
                    if (server.getPredId() == server.getLeaderId()) {
                        System.out.println("No heartbeat message received, intiating election...");
                        server.initiateElection();
                    }
                } catch (IOException e)  {
                    System.out.println("Error: " + e);
                }
            }
        } catch (SocketException e) {
            System.out.println("Error: " + e);
        }

        System.out.println("Exiting ElectionTask Thread: " + threadName);

    }

//    public void start() {
//
//
//        if (t == null) {
//            t = new Thread(this, threadName);
//            t.start();
//        }
//    }


}
