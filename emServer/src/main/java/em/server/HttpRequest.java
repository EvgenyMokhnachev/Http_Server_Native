package em.server;

import em.server.enums.ContentType;
import em.server.enums.HTTPConnectionType;
import em.server.enums.HttpMethod;
import em.server.exceptions.InvalidHttpProtocol;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {
    private static final Pattern patternHeader = Pattern.compile("^(.*):\\s(.*)\r\n$");
    private static final Pattern patternMethod = Pattern.compile("^("+ HttpMethod.rageExpMethods +")\\s(/*|/*.*)\\sHTTP/((\\d\\.\\d)|(\\d))\r\n$");
    private static final Pattern patternContentDisposition = Pattern.compile("^Content-Disposition: form-data; name=\"(.*)*\"(; filename=\"(.*)*\")*$");
    private static final Pattern patternContentType = Pattern.compile("^Content-Type:\\s(.*)$");
//    private static final Pattern patternInitMethodAndPathAndHTTPVer = Pattern.compile("^("+ HttpMethod.rageExpMethods +")\\s(/*|/*.*)\\sHTTP/(\\d*.\\d|\\d*)\r\n$");
    private static final String endHeadersString = "\r\n";
    private static final String endHeadersStringR = "\r";
    private static final String endHeadersStringN = "\n";

    private HttpMethod method;
    private String path;
    private String httpVer;
    private String host;
    private HTTPConnectionType connection;
    private String cache_control;
    private String accept;
    private String upgrade_insecure_requests;
    private String user_agent;
    private String accept_encoding;
    private String accept_language;
    private String pragma;
    private String proxy_connection;
    private String accept_charset;
    private String referer;
    private String origin;
    private Integer content_length;
    private ContentType content_type;
    private String boundary;

    public HttpRequest(InputStream inputStream) {
        initializationHeaders(inputStream);

        if(this.content_type == ContentType.MULTIPART_FORM_DATA){
            initializationMultipartFormData(inputStream);
        }
    }

    private byte[] lazyBuffer = new byte[0];
    private byte[] readLineFromInputStream(InputStream inputStream){
        byte[] readBytes = Arrays.copyOf(lazyBuffer, lazyBuffer.length);

        int indexNewLine = new String(readBytes).indexOf(endHeadersString);
        if (indexNewLine > -1) {
            byte[] lineBytes = new byte[indexNewLine + 2];
            byte[] newLazyBuffer = new byte[lazyBuffer.length - indexNewLine - 2];
            System.arraycopy(readBytes, 0, lineBytes, 0, indexNewLine + 2);
            System.arraycopy(readBytes, indexNewLine + 2, newLazyBuffer, 0, readBytes.length - indexNewLine - 2);
            lazyBuffer = newLazyBuffer;
            return lineBytes;
        }

        try {
            byte[] buffer = new byte[8192];
            int readCount = inputStream.read(buffer);
            int oldReadBytesLength = readBytes.length;
            readBytes = Arrays.copyOf(readBytes, oldReadBytesLength + readCount);
            for(int readBytesIndex = oldReadBytesLength; readBytesIndex < readBytes.length; readBytesIndex++){
                readBytes[readBytesIndex] = buffer[readBytesIndex - oldReadBytesLength];
            }

            lazyBuffer = readBytes;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readLineFromInputStream(inputStream);
    }

    private void initializationMultipartFormData(InputStream inputStream){
        List<MultipartFormData> multipartFormDataList = new ArrayList<>();
        MultipartFormData multipartFormData = null;
        boolean readContent = false;
        final String boundary = "--" + this.boundary;
        final String endBoundary = boundary + "--";
        final String emptyStr = "";
        while (true) {
            byte[] readLineBytes = readLineFromInputStream(inputStream);
            String readLine = new String(readLineBytes).replace(endHeadersString, emptyStr);

            if(readLine.contains(boundary)){
                readContent = false;
                if(multipartFormData != null) {
                    multipartFormData.closeStream();
                    multipartFormDataList.add(multipartFormData);
                }

                if(readLine.equals(endBoundary)) break;

                multipartFormData = new MultipartFormData();

                continue;
            }

            if(readContent){
                multipartFormData.setContent(readLineBytes);
                continue;
            }

            if(patternContentDisposition.matcher(readLine).matches()){
                multipartFormData.Content_Disposition = readLine;
                continue;
            }

            if(patternContentType.matcher(readLine).matches()){
                multipartFormData.Content_Type = readLine;
                continue;
            }

            if(readLine.equals(emptyStr)){
                readContent = true;
            }
        }
    }

    private void initializationHeaders(InputStream inputStream){
        try {
            boolean readHeaders = true;
            while (readHeaders) {
                byte[] headerBytes = new byte[0];
                while (true) {
                    int read = inputStream.read();

                    if(read == -1) break;

                    headerBytes = Arrays.copyOf(headerBytes, headerBytes.length + 1);
                    headerBytes[headerBytes.length - 1] = (byte) read;

                    String headerStr = new String(headerBytes);

                    Matcher matcherMethod = patternMethod.matcher(headerStr);
                    if(matcherMethod.matches()) {
                        initMethodAndPathAndHttpVer(headerStr);
                        break;
                    }

                    Matcher matcherHeader = patternHeader.matcher(headerStr);
                    if(matcherHeader.matches()) {
                        initializationHeader(matcherHeader.group(1), matcherHeader.group(2));
                        break;
                    }

                    if(headerStr.equals(endHeadersString)) {
                        readHeaders = false;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializationHeader(String headerName, String headerData){
        String name = headerName.toLowerCase().trim();
        String data = headerData.trim();

        switch (name){
            case ("host"): host = data; break;
            case ("connection"): connection = HTTPConnectionType.valueOfString(data); break;
            case ("cache-control"): cache_control = data; break;
            case ("accept"): accept = data; break;
            case ("upgrade-insecure-requests"): upgrade_insecure_requests = data; break;
            case ("user-agent"): user_agent = data; break;
            case ("accept-encoding"): accept_encoding = data; break;
            case ("accept-language"): accept_language = data; break;
            case ("proxy-connection"): proxy_connection = data; break;
            case ("pragma"): pragma = data; break;
            case ("accept-charset"): accept_charset = data; break;
            case ("referer"): referer = data; break;
            case ("origin"): origin = data; break;
            case ("content-length"): content_length = Integer.valueOf(data); break;
            case ("content-type"): {
                content_type = ContentType.valueOfString(data);
                if(content_type == ContentType.MULTIPART_FORM_DATA){
                    boundary = data.substring(data.lastIndexOf("boundary=") + "boundary=".length(), data.length());
                }
            } break;
            default: {
                System.out.println("Unknown header:");
                System.out.println(name);
                System.out.println("header data:");
                System.out.println(data);
            }
        }
    }

    private void initMethodAndPathAndHttpVer(String header){
//        Matcher matcher = patternInitMethodAndPathAndHTTPVer.matcher(header);
        Matcher matcher = patternMethod.matcher(header);
        if(matcher.matches()){
            method = HttpMethod.valueOf(matcher.group(1));
            path = matcher.group(2);
            httpVer = matcher.group(3);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVer() {
        return httpVer;
    }

}
