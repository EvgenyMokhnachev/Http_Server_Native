package em.server;

import em.server.enums.ContentType;
import em.server.enums.HTTPConnectionType;
import em.server.enums.HttpMethod;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {
    private static final Pattern PATTERN_HEADER = Pattern.compile("^(.*):\\s(.*)\r\n$");
    private static final Pattern PATTERN_METHOD = Pattern.compile("^("+ HttpMethod.rageExpMethods +")\\s(/*|/*.*)\\sHTTP/((\\d\\.\\d)|(\\d))\r\n$");
    private static final Pattern PATTERN_CONTENT_DISPOSITION = Pattern.compile("^Content-Disposition: form-data; name=\"(.*)*\"(; filename=\"(.*)*\")*$");
    private static final Pattern PATTERN_CONTENT_TYPE = Pattern.compile("^Content-Type:\\s(.*)$");
    private static final String END_HEADERS_STRING = "\r\n";
    private static final int END_HEADERS_STRING_LENGTH = END_HEADERS_STRING.length();
    private static final int ZERO = 0;
    private static final int INDEX_NOT_FOUNT = -1;
    private static final int BUFFER_SIZE = 65536;

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

    private byte[] lazyBuffer = new byte[ZERO];
    private byte[] readLineFromInputStream(InputStream inputStream){
        byte[] readBytes = Arrays.copyOf(lazyBuffer, lazyBuffer.length);

        int indexNewLine = new String(readBytes).indexOf(END_HEADERS_STRING);
        if (indexNewLine > INDEX_NOT_FOUNT) {
            byte[] lineBytes = new byte[indexNewLine + END_HEADERS_STRING_LENGTH];
            byte[] newLazyBuffer = new byte[lazyBuffer.length - indexNewLine - END_HEADERS_STRING_LENGTH];
            System.arraycopy(readBytes, ZERO, lineBytes, ZERO, indexNewLine + END_HEADERS_STRING_LENGTH);
            System.arraycopy(readBytes, indexNewLine + END_HEADERS_STRING_LENGTH, newLazyBuffer, ZERO, readBytes.length - indexNewLine - END_HEADERS_STRING_LENGTH);
            lazyBuffer = newLazyBuffer;
            return lineBytes;
        }

        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int readCount = inputStream.read(buffer);
            int oldReadBytesLength = readBytes.length;
            readBytes = Arrays.copyOf(readBytes, oldReadBytesLength + readCount);
            System.arraycopy(buffer, ZERO, readBytes, oldReadBytesLength, readBytes.length - oldReadBytesLength);
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
            String readLine = new String(readLineBytes).replace(END_HEADERS_STRING, emptyStr);

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

            if(PATTERN_CONTENT_DISPOSITION.matcher(readLine).matches()){
                multipartFormData.Content_Disposition = readLine;
                continue;
            }

            if(PATTERN_CONTENT_TYPE.matcher(readLine).matches()){
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

                    Matcher matcherMethod = PATTERN_METHOD.matcher(headerStr);
                    if(matcherMethod.matches()) {
                        initMethodAndPathAndHttpVer(headerStr);
                        break;
                    }

                    Matcher matcherHeader = PATTERN_HEADER.matcher(headerStr);
                    if(matcherHeader.matches()) {
                        initializationHeader(matcherHeader.group(1), matcherHeader.group(2));
                        break;
                    }

                    if(headerStr.equals(END_HEADERS_STRING)) {
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
        Matcher matcher = PATTERN_METHOD.matcher(header);
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
