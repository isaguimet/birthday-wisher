package com.birthdaywisher.server;

import com.birthdaywisher.server.election.ElectionTask;
import com.birthdaywisher.server.election.HeartbeatTask;
import com.birthdaywisher.server.election.Server;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        try {
            InetAddress address = InetAddress.getByName("127.0.0.1");


        //server 1
//        Server server = new Server(1, 3, 3, "server1");
//        server.setServerPort(5000);
//        server.setSuccPort(6000);
//        server.setSuccId(2);

        //server 2
        Server server = new Server(2, 3, 1, "server2");
        server.setServerPort(6000);
        server.setSuccPort(7000);
        server.setSuccId(3);

        //server 3
//        Server server = new Server(3, 3, 2, "server3");
//        server.setServerPort(7000);
//        server.setSuccPort(5000);
//        server.setSuccId(1);

        try (ServerSocket serverSocket = new ServerSocket(server.getSuccPort());
             Socket ss = serverSocket.accept();
             Socket s = new Socket(address, server.getSuccPort());)
            {

            server.setSocket(ss);
            System.out.println("Server " + String.valueOf(server.getServerId()) +  " listening on port " + String.valueOf(server.getServerPort()) +  " ... ");
            server.setSuccSocket(s);
            System.out.println("Connected to server " + String.valueOf(server.getSuccId()));
            
            //initialize server information and setup successor and predecessor
            // -------------------------------- BEGIN SERVER 1 SETUP --------------------------------
            //server1 port: 5000
////            Server server = new Server(1, 3, 3, "server1");
////            server.setSuccPort(6000);
//
////            ServerSocket serverSocket = new ServerSocket(5000);
////            Socket ss = serverSocket.accept();
//            server.setSocket(ss);
//            System.out.println("Server 1 listening on port 5000 ... ");
//
////            TimeUnit.SECONDS.sleep(180);
////            Socket s = new Socket(address, server.getSuccPort());   //todo: figure out order
//            server.setSuccSocket(s);
//            System.out.println("Connected to server 2");
            
            // -------------------------------- END SERVER 1 SETUP --------------------------------
            

            // -------------------------------- BEGIN SERVER 2 SETUP --------------------------------
            //server2 port: 6000
////            Server server = new Server(2, 3, 1, "server2");
////            server.setSuccPort(7000);
//            System.out.println("Connected to server 3");
//
////            Socket s = new Socket(address, server.getSuccPort());   //todo: figure out order
//            server.setSuccSocket(s);
//
////            ServerSocket serverSocket = new ServerSocket(6000);
////            Socket ss = serverSocket.accept();
//            server.setSocket(ss);
//            System.out.println("Server 2 listening on port 6000 ... ");
            // -------------------------------- END SERVER 2 SETUP --------------------------------
            

            // -------------------------------- BEGIN SERVER 3 SETUP --------------------------------
//            //server3 port: 7000
////            Server server = new Server(3, 3, 2, "server3");
////            server.setSuccPort(5000);
//            System.out.println("Connected to server 1");
//
////            Socket s = new Socket(address, server.getSuccPort());   //todo: figure out order
//            server.setSuccSocket(s);
//
////            ServerSocket serverSocket = new ServerSocket(7000);
////            Socket ss = serverSocket.accept();
//            server.setSocket(ss);
//            System.out.println("Server 3 listening on port 7000 ... ");
            // -------------------------------- END SERVER 3 SETUP --------------------------------
            

            //Leader functions
            if (server.getServerId()== server.getLeaderId()) {

                Thread heartbeatTaskThread = new HeartbeatTask(server);
                heartbeatTaskThread.start();
                
                //Send heart beat message to successor (server 3 to 1)
                

            //If server 1, listen for heartbeat
            } else {
                //if no heart beat recieved (by 1) and timeout, server 1 start election by opening? socket connetion 
                //(new task start election)?

                Thread electionTask = new ElectionTask(server, server.getServerName());
                electionTask.start();

                electionTask.join();

            }

            //sendMessageToSuccessor(Server server, String msg);


            // InetAddress address = InetAddress.getByName("127.0.0.1");
            // Socket s = new Socket(address, server1.getSuccPort());
            // server1.setSuccSocket(s);
            // DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            // dout.writeUTF("Hello Server 1");
            // dout.flush();
            // dout.close();
            //whatever code here



            // all the codes


//            s.close();
//            ss.close();

            //listening to messages from server 1
            //this is to create the server socket

            // System.out.println("SERVER 3");
            // DataInputStream dis = new DataInputStream(ss.getInputStream());
            // String  str=(String)dis.readUTF();
            // System.out.println("message= "+str);



        } catch (Exception e) {
            System.out.println(e);
        }
        } catch (Exception e) {

        }
        SpringApplication.run(Application.class, args);



    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.serializerByType(ObjectId.class, new ToStringSerializer());
    }
}
