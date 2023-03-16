package com.birthdaywisher.server.election;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ElectionTask extends Thread {
    // private Thread t;
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
            // server.getServerSocket().setSoTimeout(5000);
            server.getSuccSocket().setKeepAlive(true);
            System.out.println("is successor socket alive " + server.getSuccSocket().getKeepAlive());
            while (true) {
                try {
                    String msg = server.receiveMessage();
                    // System.out.println("Received Message: " + msg);
                    if (!msg.equals("")) {
                        if (server.getPredId() == server.getLeaderId()) { // server's predecessor is the leader
                            if (msg.equals("2")) { // heartbeat message

                            } else {
                                server.election(msg);
                            }
                        } else {
                            server.election(msg);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                    // no message received after some time
                    if (server.getPredId() == server.getLeaderId()) {
                        System.out.println("No heartbeat message received, intiating election...");
                        server.initiateElection();
                    }
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
