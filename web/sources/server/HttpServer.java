package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private int serverPort;
    private ServerSocket serverSocket;

    public HttpServer(int port){
        serverPort = port;
    }

    public void start(){
        try {
            serverSocket = new ServerSocket(serverPort);

            while (true){
                Socket socket = serverSocket.accept();
                new Thread(){
                    HttpConnection httpConnection = new HttpConnection(socket);
                };
            }
        } catch (IOException e) {
            System.out.println("Start server on port " + serverPort + " failed!");
            e.printStackTrace();
        }
    }

    public void stop(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Stop server on port " + serverPort + " failed!");
        }
    }

}
