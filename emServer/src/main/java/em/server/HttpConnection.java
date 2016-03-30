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
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            boolean canRead = true;
//            while (canRead) {
//                int bufferSize = inputStream.available() == 0 || inputStream.available() > 65536 ? 65536 : inputStream.available();
//                int[] buffer = new int[bufferSize];
//
//                int bufferWriteIndex = 0;
//                while(bufferWriteIndex < buffer.length){
//                    int readByte = inputStreamReader.read();
//
//                    if(readByte != -1) {
//                        buffer[bufferWriteIndex++] = readByte;
//                        canRead = false;
//                    } else {
//                        break;
//                    }
//                }
//
//                int inputBytesLength = inputBytes.length;
//                inputBytes = Arrays.copyOf(inputBytes, inputBytesLength + bufferWriteIndex);
//                for(int inputBytesIndex = inputBytesLength; inputBytesIndex < inputBytes.length; inputBytesIndex++){
//                    inputBytes[inputBytesIndex] = (byte) buffer[inputBytesIndex - inputBytesLength];
//                }
//            }
//
//            String stringHeaders = new String(inputBytes);
//            String[] splitHeaders = stringHeaders.split("\\r\\n");

//            HttpRequest httpRequest = new HttpRequest(splitHeaders);
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
