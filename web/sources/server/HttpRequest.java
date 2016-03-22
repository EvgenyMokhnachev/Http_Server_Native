package server;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {

    private HttpMethod method;
    private String path;
    private String httpVer;
    private HttpRequestHeaders headers;

    public HttpRequest(ArrayList<String> stringHeaders){
        String[] requestSplit = new String[stringHeaders.size()];
        for(int stringHeaderIndex = 0; stringHeaderIndex < stringHeaders.size(); stringHeaderIndex++) {
            requestSplit[stringHeaderIndex] = stringHeaders.get(stringHeaderIndex);
        }

        requestSplit = initMethodAndPathAndHttpVer(requestSplit);
        headers = new HttpRequestHeaders(requestSplit);
        int i = 0;
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


        if(method == null) {
            System.out.println("\nRequest method not found");
        }

        if(path == null) {
            System.out.println("Request path not found");
        }

        if(httpVer == null) {
            System.out.println("Request http version not found");
        }

        for(String requestHeader: requestSplit){
            System.out.println(requestHeader);
        }

        System.out.println("\n");

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
}
