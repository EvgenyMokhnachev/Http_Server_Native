import em.server.HttpServer;

public class Launcher {

    public static void main(String[] args) {
        Thread.currentThread().setName("Http_Native_Server_web");

        System.out.println(Thread.currentThread().getClass().getResource("/server_configuration.xml"));
//        HttpServer server = new HttpServer(8080);
//        server.start();
    }

}
