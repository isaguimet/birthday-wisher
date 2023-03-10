package com.birthdaywisher.server;

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

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        try {
            //--------------------------------- Common variabels ------------------------------------
            InetAddress address = InetAddress.getByName("127.0.0.1");
            
            //initialize server information and setup successor and predecessor
            // -------------------------------- BEGIN SERVER 1 SETUP --------------------------------
            //server1 port: 5000
            Server server = new Server(1, 3);
            server.setSuccPort(5000);

            Socket s = new Socket(address, server.getSuccPort());
            server.setSuccSocket(s);
            
            // -------------------------------- END SERVER 1 SETUP --------------------------------
            

            // -------------------------------- BEGIN SERVER 2 SETUP --------------------------------
            //server2 port: 6000
            Server server = new Server(2, 3);
            server.setSuccPort(6000);

            Socket s = new Socket(address, server.getSuccPort());
            server.setSuccSocket(s);
            // -------------------------------- END SERVER 2 SETUP --------------------------------
            

            // -------------------------------- BEGIN SERVER 3 SETUP --------------------------------
            //server3 port: 7000
            Server server = new Server(3, 3);
            server.setSuccPort(7000);

            Socket s = new Socket(address, server.getSuccPort());
            server.setSuccSocket(s);
            
            // -------------------------------- END SERVER 3 SETUP --------------------------------
            //Leader functions
            if (server.getServerId()== server.getLeaderId()) {
                
                //Send heart beat message to successor (server 3 to 1)
                //open sockets 
            } else {

                //if no heart beat recieved (by 3) and timeout, server 1 start election

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

            s.close();

            // all the codes




            //listening to messages from server 1
            //this is to create the server socket
            ServerSocket serverSocket = new ServerSocket(5000);
            Socket ss = serverSocket.accept();
            System.out.println("SERVER 3");
            DataInputStream dis = new DataInputStream(ss.getInputStream());
            String  str=(String)dis.readUTF();
            System.out.println("message= "+str);
            ss.close();


        } catch (Exception e) {
            System.out.println(e);
        }


        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.serializerByType(ObjectId.class, new ToStringSerializer());
    }
}
