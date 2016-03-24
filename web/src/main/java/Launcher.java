import em.server.HttpServer;

public class Launcher {

    public static void main(String[] args) {
        Thread.currentThread().setName("Http_Native_Server_web");

        HttpServer server = new HttpServer(8080);
        server.configuration.setConfigurationFilePath("/server_configuration.xml");
        server.start();
    }

}
