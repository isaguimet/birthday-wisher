package com.birthdaywisher.server.election;

import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import java.net.*;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class LeaderElection implements CommandLineRunner {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int port = 9000;
    private boolean isLeader = true;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void run(String... args) {
        printHello();
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            sendMessage("greetings fellow server");
            String greeting = in.readLine();
            System.out.println("received greeting: " + greeting);
        } catch (Exception e) {
            System.out.println("Error" + e);
        }

    }

    public String sendMessage(String msg) throws Exception {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void printHello() {
        System.out.println("Hello Spring!");
    }
}