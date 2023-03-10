package com.birthdaywisher.server.leader;

import com.birthdaywisher.server.election.ElectionTask;
import com.birthdaywisher.server.election.HeartbeatTask;
import com.birthdaywisher.server.election.Server;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class LeaderElectionService {
    private ServerProperties serverProperties;
    private Server server;

    public LeaderElectionService(ServerProperties serverProperties) {
        System.out.println("Leader Election Service instantiated");
        this.serverProperties = serverProperties;
        if (serverProperties.getPort().intValue() == 8080) {
            this.server = new Server(3, 3, 2, "server3");
            server.setServerPort(7000);
            server.setSuccPort(5000);
            server.setSuccId(1);
        } else if (serverProperties.getPort().intValue() == 8081) {
            this.server = new Server(2, 3, 1, "server2");
            server.setServerPort(6000);
            server.setSuccPort(7000);
            server.setSuccId(3);
        } else {
            this.server = new Server(1, 3, 3, "server1");
            server.setServerPort(5000);
            server.setSuccPort(6000);
            server.setSuccId(2);
        }
    }

    @Async
    public Future<Socket> acceptConnections(ServerSocket serverSocket) throws IOException {
        return CompletableFuture.completedFuture(serverSocket.accept());
    }

    @Async
    public void init() {
        System.out.println("Server " + server.getServerName());
        try {
            InetAddress address = InetAddress.getByName("127.0.0.1");

            System.out.println("waiting to accept connection on " + server.getServerPort());
            ServerSocket serverSocket = new ServerSocket(server.getServerPort());

            TimeUnit.SECONDS.sleep(20);

            Socket s = new Socket(address, server.getSuccPort());
            server.setSuccSocket(s);
            System.out.println("Connected to server " + server.getSuccId());

            Future<Socket> ss = acceptConnections(serverSocket);
            server.setSocket(ss.get());
            System.out.println("Server " + server.getServerId() +  " listening on port " + server.getServerPort() +  " ... ");

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

        } catch (Exception e) {
            System.out.println("Failed to initialize leader: " + e.getMessage());
        }
    }
}
