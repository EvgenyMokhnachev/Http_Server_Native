package em.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

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

    private String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
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
