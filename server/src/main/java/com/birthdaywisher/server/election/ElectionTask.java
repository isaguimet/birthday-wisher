package com.birthdaywisher.server.election;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ElectionTask extends Thread {
    // private Thread t;
    private String threadName;
    private Server server;

    private final long heartbeatTimeoutInterval = 5050;

    public ElectionTask(Server server, String threadName) {
        this.server = server;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        System.out.println("Starting ElectionTask Thread: " + threadName);
        long lastRead = System.currentTimeMillis();
        try {
            // server.getServerSocket().setSoTimeout(5000);
            server.getSuccSocket().setKeepAlive(true);
            while (true) {
                try {
//                    System.out.println("is successor socket alive " + server.getSuccSocket().getKeepAlive());
                    String msg = server.receiveMessage();
                    // System.out.println("Received Message: " + msg);
                    if (!msg.equals("")) {
                        if (msg.equals("2")) {
                            lastRead = System.currentTimeMillis();
                        } else {
                            System.out.println("send election message");
                            server.election(msg);
                        }
//                        if (server.getPredId() == server.getLeaderId()) { // server's predecessor is the leader
//                            if (!msg.equals("2")) { // heartbeat message
//                                server.election(msg);
//                            }
//                        } else {
//                            server.election(msg);
//                        }
                    }
                    if (server.getPredId() == server.getLeaderId() && !server.getElectionStarted()) {
//                        System.out.println("time since last heartbeat: " + (System.currentTimeMillis() - lastRead));
                        if ((System.currentTimeMillis() - lastRead) > heartbeatTimeoutInterval) {
                            System.out.println("Out of heartbeat interval");
                            server.initiateElection();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("ElectionTask Error: " + e);
                }
            }
        } catch (SocketException e) {
            System.out.println("SocketExceptionError: " + e);
        }

        System.out.println("Exiting ElectionTask Thread: " + threadName);

    }

    // public void start() {
    //
    //
    // if (t == null) {
    // t = new Thread(this, threadName);
    // t.start();
    // }
    // }

}
