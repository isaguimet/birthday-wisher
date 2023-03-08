package com.birthdaywisher.server.election;

public class Election {
    private Server currentServer; 

    Election(Server currentServer) {
        this.currentServer = currentServer;
    }

    // server 1 
    // server 2 
    // server 3 

    class Server {
        private int serverId;
        public int serverIndex;
        private int[] serverId;
        private boolean active;
        private int predId;
        private int succId;
        private int predIndex;
        private int succIndex;
        private boolean running;

        Server(int index) {
            this.index = index;
            this.running = false;
            setSuccessor(this.index);
            setPedecessor(this.index);
        }

        private void setSuccessor() {

            int succ = this.index + 1;
            if(succ < serverId.length) {
                this.succId = serverId[succ];
            } else {
                this.succId = 0; //to back to begining
            }

        }

        private void setPedecessor() {
            int pred = this.index - 1;
            if (pred > 0) {
                this.predId = pred;
            } else {
                this.predId = serverId[serverId.length - 1];
            }

        }

        //check if it's server exists
        public int getServerId() {
            return this.serverId;
        }
        public int getSuccId() {
            return this.succId;
        }
        public int getPredId() {
            return this.predId;
        }
        public int getServerIndex() {
            return this.serverIndex;
        }
        public int getSuccIndex() {
            return this.succIndex;
        }
        public int getPredIndex() {
            return this.predIndex;
        }

        public boolean getRunning() {
            return this.running;
        }

        public void setRunning(boolean status) {
            this.running = status;
        }
    }

    class Message {
        private int type; //0 if election, 1 if leader
        private Server s;
        private int currentIndex;

        message(int type, Server s) {
            this.type = type;
            this.s = s;
        }
        
        public Server getServer() {
            return this.s;
        }

        public Server getType(){
            return this.type;
        }

        public int getCurrentIndex(){
            return this.currentIndex;
        }

        public int setCurrentIndex(int index){
            this.currentIndex = index;
        }

    }

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
