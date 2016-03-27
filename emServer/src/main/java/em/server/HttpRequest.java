package em.server;

import em.server.enums.ContentType;
import em.server.enums.HTTPConnectionType;
import em.server.enums.HttpMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {

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
    private Map<String, String> params = new HashMap<>();

    public HttpRequest(ArrayList<String> stringHeaders){
        String[] requestSplit = new String[stringHeaders.size()];
        for(int stringHeaderIndex = 0; stringHeaderIndex < stringHeaders.size(); stringHeaderIndex++) {
            requestSplit[stringHeaderIndex] = stringHeaders.get(stringHeaderIndex);
        }

        requestSplit = initMethodAndPathAndHttpVer(requestSplit);
        initHeaders(requestSplit);
    }

    public HttpRequest(String[] requestSplit){
        requestSplit = initMethodAndPathAndHttpVer(requestSplit);
        initHeaders(requestSplit);
    }

    private void initHeaders(String[] requestSplit){
        for(String requestHeader : requestSplit) {
            Pattern patternHeader = Pattern.compile("^(.*):\\s(.*)$");
            Pattern patternParams = Pattern.compile("^((\\S*)=(\\S*)&?)+$");

            Matcher matcherHeader = patternHeader.matcher(requestHeader);
            Matcher matcherParams = patternParams.matcher(requestHeader);

            if(matcherHeader.matches()){
                initializationHeader(matcherHeader.group(1), matcherHeader.group(2));
            }

            if(matcherParams.matches()){
                initializationParams(requestHeader);
            }
        }
    }

    private void initializationParams(String headerParams){
        String[] paramsSplit = headerParams.split("&");
        for(String param : paramsSplit){
            String[] paramSplit = param.split("=");
            try {
                String decodeParamName = URLDecoder.decode(paramSplit[0], "UTF-8");
                String decodeParamValue = URLDecoder.decode(paramSplit[1], "UTF-8");
                params.put(decodeParamName, decodeParamValue);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private void initializationHeader(String headerName, String headerData){
        String name = headerName.toLowerCase().trim();
        String data = headerData.trim();

        switch (name){
            case ("host"): {
                host = data;
            } break;
            case ("connection"): {
                connection = HTTPConnectionType.valueOfString(data);
            } break;
            case ("cache-control"): {
                cache_control = data;
            } break;
            case ("accept"): {
                accept = data;
            } break;
            case ("upgrade-insecure-requests"): {
                upgrade_insecure_requests = data;
            } break;
            case ("user-agent"): {
                user_agent = data;
            } break;
            case ("accept-encoding"): {
                accept_encoding = data;
            } break;
            case ("accept-language"): {
                accept_language = data;
            } break;
            case ("proxy-connection"): {
                proxy_connection = data;
            } break;
            case ("pragma"): {
                pragma = data;
            } break;
            case ("accept-charset"): {
                accept_charset = data;
            } break;
            case ("referer"): {
                referer = data;
            } break;
            case ("origin"): {
                origin = data;
            } break;
            case ("content-length"): {
                content_length = Integer.valueOf(data);
            } break;
            case ("content-type"): {
                content_type = ContentType.valueOfString(data);
            } break;
            default: {
                System.out.println("Unknown header:");
                System.out.println(name);
                System.out.println("header data:");
                System.out.println(data);
            }
        }
    }

    private String[] initMethodAndPathAndHttpVer(String[] requestSplit){
        for(String requestStingItem : requestSplit){
            Pattern pattern = Pattern.compile("^("+ HttpMethod.rageExpMethods +")\\s(/*|/*.*)\\sHTTP/(\\d*.\\d|\\d*)$");
            Matcher matcher = pattern.matcher(requestStingItem);
            if(matcher.matches()){
                method = HttpMethod.valueOf(matcher.group(1));
                path = matcher.group(2);
                httpVer = matcher.group(3);

                String[] newRequestSplit = new String[requestSplit.length - 1];
                for(int indexRequestSplit = 0, indexNewRequestSplit = 0; indexRequestSplit < requestSplit.length; indexRequestSplit++){
                    if(!requestSplit[indexRequestSplit].equals(requestStingItem)){
                        newRequestSplit[indexNewRequestSplit++] = requestSplit[indexRequestSplit];
                    }
                }

                return newRequestSplit;
            }
        }


//        if(method == null) {
//            method = HttpMethod.ANY;
//        }
//
//        if(path == null) {
//            path = "";
//        }
//
//        if(httpVer == null) {
//            System.out.println("Request http version not found");
//        }
//
//        for(String requestHeader: requestSplit){
//            System.out.println(requestHeader);
//        }
//
//        System.out.println("\n");

        return requestSplit;
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

    public HTTPConnectionType getConnection() {
        return connection;
    }

}
