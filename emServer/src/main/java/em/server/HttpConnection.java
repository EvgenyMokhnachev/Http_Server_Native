package em.server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class HttpConnection implements Runnable {

    private Socket socket;

    private ControllersLoader controllersLoader;

    private InputStream inputStream;
    private OutputStream outputStream;

    private byte[] inputBytes = new byte[0];

    public HttpConnection(Socket socket) throws Throwable {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        this.controllersLoader = new ControllersLoader();
    }

    @Override
    public void run() {
        try {
            HttpRequest httpRequest = new HttpRequest(inputStream);
            HttpResponse httpResponse = new HttpResponse(httpRequest.getHttpVer());

            controllersLoader.loadController(httpRequest, httpResponse);

            if(httpRequest.multipartForm != null) {
                httpRequest.multipartForm.removeTempFiles();
            }

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
