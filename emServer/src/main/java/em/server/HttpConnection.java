package em.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class HttpConnection {

    private InputStream inputStream;
    private OutputStream outputStream;

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;

    public HttpConnection(Socket socket){
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ArrayList<String> stringHeaders = new ArrayList<String>();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while(true) {
                String s = bufferedReader.readLine();
                if(s == null || s.trim().length() == 0) {
                    break;
                } else {
                    stringHeaders.add(s);
                }
            }

            httpRequest = new HttpRequest(stringHeaders);
            httpResponse = new HttpResponse(httpRequest.getHttpVer());

            ControllersLoader.loadController(httpRequest, httpResponse);

            outputStream.write(httpResponse.getHeadersBytes());
            outputStream.write(httpResponse.getContentBytes());
            outputStream.flush();
        } catch (IOException e) {
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
