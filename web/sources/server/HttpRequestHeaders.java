package server;

public class HttpRequestHeaders {
    private String host;
    private String connection;
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

    public HttpRequestHeaders(String[] requestSplit){
        for(String requestHeader : requestSplit) {
            int splitIndex = requestHeader.indexOf(":");
            if(splitIndex > -1) {
                String headerName = requestHeader.substring(0, splitIndex);
                String headerData = requestHeader.substring(splitIndex + 1, requestHeader.length());
                initialization(headerName, headerData);
            }
        }
    }

    private void initialization(String headerName, String headerData){
        String name = headerName.toLowerCase().trim();
        String data = headerData.trim();

        switch (name){
            case ("host"): host = data; break;
            case ("connection"): connection = data; break;
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
            default: {
                System.out.println("Unknown header:");
                System.out.println(name);
                System.out.println("header data:");
                System.out.println(data);
            }
        }
    }
}
