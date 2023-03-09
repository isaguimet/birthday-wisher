package com.birthdaywisher.server.election;

public class Election {
    private Server currentServer; 

    Election(Server currentServer) {
        this.currentServer = currentServer;
    }

    // server 1 
    // server 2 
    // server 3 

    // array of server IDs
        // status of servers

    public void initiateElection(Server server) {
        server.setRunning(true);

        System.out.println("Election initiated by server: " + initiatedId);

        //send elction (initiated) to successor(initiated)

        Message msg = new Message(0, server);
        send(msg, server.getSuccIndex());  //todo implement sending
    }

    //call an election and select a new leader
    public void ElectionProcess ( Message msg) {
        // todo check if message came from predecessor
        Server receivedServer = msg.getServer();

        if (msg.getType() == 0) {            
            if (receivedServer.getServerId() > currentServer.getServerId()) {
                send(msg, currentServer.getSuccIndex());
            } else if (receivedServer.getServerId() < currentServer.getServerId() && !currentServer.getRunning()) {
                currentServer.setRunning(true);
                Message msg = new Message(0, currentServer);
                send(msg, server.getSuccIndex());  //todo implement sending
            } else if (receivedServer.getServerId() == currentServer.getServerId()) {
                Message msg = new Message(1, currentServer);    //send leader messsage
                send(msg, server.getSuccIndex());  //todo implement sending
            }
        } else {
            currentServer.setLeader(receivedServer);
            currentServer.setRunning(false);

            //quit the election and not send anything
            //call something to set proxy to send to this server now
            proxySetPrimary(receivedServer.getServerUrl()); //todo impelment this and url
        }

    }

    //todo: assign random ids to servers, and then they add to the server array

}
