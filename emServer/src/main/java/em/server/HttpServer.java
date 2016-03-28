package em.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public final ServerConfigurator configuration;
    private int serverPort;

    public HttpServer(int port){
        this.configuration = new ServerConfigurator();
        this.serverPort = port;
    }

    public void start(){
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);

            System.out.println("Start server on port " + serverPort + "!");

            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                try {
                    new Thread(new HttpConnection(socket)).start();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Start server on port " + serverPort + " failed!");
            e.printStackTrace();
        }
    }

}
