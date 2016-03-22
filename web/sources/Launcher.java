import server.HttpServer;

public class Launcher {

    public static void main(String[] args) {
        HttpServer server = new HttpServer(8080);
        server.start();
    }

}
