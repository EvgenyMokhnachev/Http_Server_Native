package em.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

public class HttpConnection {

    private InputStream inputStream;
    private OutputStream outputStream;

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;

    private byte[] inputBytes = new byte[0];

    public HttpConnection(Socket socket) {
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

//            while (!inputStreamReader.ready()) {
//                System.out.println("wait input stream");
//            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (true){
                int bufferSize = inputStream.available() == 0 ? 32768 : inputStream.available();
                inputBytes = new byte[bufferSize];
                int readCount = inputStream.read(inputBytes);
                if (readCount == -1){
                    break;
                }

                if(readCount > 0) {
                    baos.write(inputBytes, 0, readCount);
                }
            }
            baos.flush();
            baos.close();

//            inputStreamReader.close();
//            inputStream.close();

            inputBytes = baos.toByteArray();

//            while (inputStreamReader.ready()) {
//                inputBytes = Arrays.copyOf(inputBytes, inputBytes.length + 1);
//                inputBytes[inputBytes.length - 1] = (byte) inputStreamReader.read();
//            }

            String stringHeaders = new String(inputBytes);
            String[] splitHeaders = stringHeaders.split("\\r\\n");

            httpRequest = new HttpRequest(splitHeaders);
            httpResponse = new HttpResponse(httpRequest.getHttpVer());

            ControllersLoader.loadController(httpRequest, httpResponse);

            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputStream.write(httpResponse.getHeadersBytes());
            outputStream.write(httpResponse.getContentBytes());
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
