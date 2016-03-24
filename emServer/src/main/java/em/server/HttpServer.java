package em.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public ServerConfigurator configuration;

    private int serverPort;
    private ServerSocket serverSocket;

    public HttpServer(int port){
        this.configuration = new ServerConfigurator();
        this.serverPort = port;
    }

    public void start(){
        try {
            serverSocket = new ServerSocket(serverPort);

            while (true){
                final Socket socket = serverSocket.accept();
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
