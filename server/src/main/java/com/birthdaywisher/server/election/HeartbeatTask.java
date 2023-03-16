package com.birthdaywisher.server.election;

public class HeartbeatTask extends Thread {
    // private Thread t;
    private String threadName;
    private Server server;

    public HeartbeatTask(Server server) {
        this.server = server;
        threadName = "HeartbeatTask";
    }

    @Override
    public void run() {
        System.out.println("Starting HeartbeatTask Thread");
        while (true) {
            if (server.getServerId() == server.getLeaderId()) {
                try {
                    System.out.println(
                            "Sending Heartbeat Msg to Successor Port: " + String.valueOf(server.getSuccPort()));
                    server.sendMessage("2");
                    Thread.sleep(5000);
                } catch (Exception e) {
                    System.out.println("heartbeat error:" + e);
                    // server.initiateElection();
                }
            } else {
                break;
            }

        }
        System.out.println("Exiting HeartbeatTask Thread");
    }

    // public void start() {
    // System.out.println("Starting HeartbeatTask Thread");
    //
    // if (t == null) {
    // t = new Thread(this, threadName);
    // t.start();
    // }
    //
    // }

}
