package com.birthdaywisher.server.election;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    public Server(int id, int leaderId, int predId, String serverName) {
        this.serverId = id;
        this.leaderId = leaderId;
        this.predId = predId;
        this.isRunning = false;
        this.serverName = serverName;
    }

     //Getters
    public int getServerPort() { return this.serverPort; }
    public int getSuccId() { return this.succId; }
    public String getServerName() { return this.serverName; }
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

    //Setters
    public void setServerPort(int port) { this.serverPort = port; }
    public void setSuccId(int succId) { this.succId = succId; }
    public void setServerName(String name) { this.serverName = name; }
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

    public void initiateElection() {
        setIsRunning(true);
        
        System.out.println("Election initiated by server: " + getServerId());

        election("0" + getServerId());
    }
 
    //call an election and select a new leader
    // "03" election message and 3 is the server id running in the election
    // 0 is election message
    // 1 is leader message
    public void election (String msg) {
        char msgType = msg.charAt(0);
        char msgId = msg.charAt(1);

        if ((int) msgType == 0) {
            System.out.println("Election Message Received: " + msg);
            //If message id is greater than server id, pass message onto successor
            if ((int) msgId > getServerId()) {

                sendMessage(msg);

            //If message id is less than server id, send new message with server id to succesor and set running to true
            } else if ((int) msgId < getServerId() && !getIsRunning()) {

                setIsRunning(true);
                sendMessage("0" + String.valueOf(getServerId()));
                
            //If message id matches server id, new message declaring leader is sent to all servers
            } else if (msgId == getServerId()) {
                
                sendMessage("1" + String.valueOf(msgId));
            }
        //Recieve leader message, update leader id and stop running 
        } else {
            System.out.println("Leader Message Received: " + msg);
            setLeaderId(msgId);
            setIsRunning(false);
 
            //quit the election and not send anything
            //call something to set proxy to send to this server now
//            proxySetPrimary(receivedServer.getServerUrl()); //todo TELL some proxy?
        }
 
    }

    public void sendMessage(String msg) {
        try {
            Socket s = getSuccSocket();
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(msg);
            dout.flush();
            dout.close();

            System.out.println("Sent Message: " + msg);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public String receiveMessage() throws IOException{
        Socket ss = getServerSocket();
        DataInputStream dis = new DataInputStream(ss.getInputStream());
        String  msg = "";

        while(true) {
            if(dis.available() > 0) {
                msg = dis.readUTF();
                System.out.println("Read: " + msg);
                break;
            }
        }

        if(!msg.equals("")) {
            System.out.println("Received msg: " + msg);
        }

        return msg;
    }

}
