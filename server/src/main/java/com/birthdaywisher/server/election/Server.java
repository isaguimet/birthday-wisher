package com.birthdaywisher.server.election;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Server {

    private int serverId;
    private String serverName;
    private int predId;
    private int succId;
    private Socket socket;
    private Socket succSocket;
    private int succPort;
    private int serverPort;
    private int leaderId;
    private boolean isRunning;
    private DataInputStream inputStream;
    private boolean electionStarted;
    public Server(int id, int leaderId, int predId, String serverName) {
        this.serverId = id;
        this.leaderId = leaderId;
        this.predId = predId;
        this.isRunning = false;
        this.serverName = serverName;
        this.electionStarted = false;
    }

    // Getters
    public boolean getElectionStarted() { return this.electionStarted; };
    public int getServerPort() {
        return this.serverPort;
    }

    public int getSuccId() {
        return this.succId;
    }

    public String getServerName() {
        return this.serverName;
    }

    public Socket getServerSocket() {
        return this.socket;
    }

    public int getServerId() {
        return this.serverId;
    }

    public Socket getSuccSocket() {
        return this.succSocket;
    }

    public int getSuccPort() {
        return this.succPort;
    }

    public int getPredId() {
        return this.predId;
    }

    public boolean getIsRunning() {
        return this.isRunning;
    }

    public int getLeaderId() {
        return this.leaderId;
    }

    // Setters
    public void setElectionStarted(boolean status) { this.electionStarted = status;}
    public void setServerPort(int port) {
        this.serverPort = port;
    }

    public void setSuccId(int succId) {
        this.succId = succId;
    }

    public void setServerName(String name) {
        this.serverName = name;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setSuccSocket(Socket socket) {
        this.succSocket = socket;
    }

    public void setSuccPort(int port) {
        this.succPort = port;
    }

    public void setPedecessor(int id) {
        this.predId = id;
    }

    public void setLeaderId(int id) {
        this.leaderId = id;
    }

    public void setIsRunning(boolean status) {
        this.isRunning = status;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void initiateElection() {
        setIsRunning(true);
        setElectionStarted(true);

        System.out.println("Election initiated by server: " + getServerId());
        sendMessage("0" + getServerId());
//        election("0" + getServerId());
    }

    // call an election and select a new leader
    // "03" election message and 3 is the server id running in the election
    // 0 is election message
    // 1 is leader message
    public void election(String msg) {
        System.out.println("election message: " + msg);
        int isLeaderMessage = Character.getNumericValue(msg.charAt(0));
        int serverId = Character.getNumericValue(msg.charAt(1));

        if (isLeaderMessage == 0) {
//            System.out.println("Election Message Received: " + msg);
            // If message id is greater than server id, pass message onto successor
            if (serverId > getServerId()) {

                sendMessage(msg);

                // If message id is less than server id, send new message with server id to
                // succesor and set running to true
            } else if (serverId < getServerId() && !getIsRunning()) {
                System.out.println("current server id better than election id");
                setIsRunning(true);


                if (getServerId() == 2) {
                    setSuccPort(5000);
                    try {
                        InetAddress address = InetAddress.getByName("127.0.0.1");
                        Socket s = new Socket(address, getSuccPort());
                        setSuccSocket(s);
                        System.out.println("Connected to server 1");
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                }
                sendMessage("0" + String.valueOf(getServerId()));

                // If message id matches server id, new message declaring leader is sent to all
                // servers
            } else if (serverId == getServerId()) {

                sendMessage("1" + String.valueOf(serverId));
                setLeaderId(serverId);
                setElectionStarted(false);
                setIsRunning(false);
            }
            // Recieve leader message, update leader id and stop running
        } else {
            System.out.println("Leader Message Received: " + msg);
            setLeaderId(serverId);
            setIsRunning(false);
            setElectionStarted(false);

            System.out.println("Leader ID" + getLeaderId());
            System.out.println("Server ID" + getServerId());
            if (getLeaderId() == getServerId()) {
                Thread heartbeatTaskThread = new HeartbeatTask(this);
                heartbeatTaskThread.start();
            }

            // quit the election and not send anything
            // call something to set proxy to send to this server now
            // proxySetPrimary(receivedServer.getServerUrl()); //todo TELL some proxy?
        }

    }

    public void sendMessage(String msg) {
        try {
            Socket s = getSuccSocket();
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(msg);
            dout.flush();
            // dout.close();

            System.out.println("Sent Message: " + msg);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public String receiveMessage() throws IOException {
        Socket ss = getServerSocket();
        DataInputStream dis = new DataInputStream(ss.getInputStream());
        String msg = "";
        while (dis.available() > 0) {
            msg = (String) dis.readUTF();
            System.out.println("Read: " + msg);
        }
        if (!msg.equals("")) {
            System.out.println("Received Message: " + msg);
        }
        return msg;
    }

}
