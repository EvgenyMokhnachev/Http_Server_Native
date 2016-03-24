package em.server;

public class HttpResponse {

    private String http_ver;
    private HTTPStatusCode status_code;
    private static final String server = "JMHTTPNative";
    private ContentType content_type;
    private int content_length;
    private String connection;
    private byte[] content;

    public HttpResponse(String http_ver){
        this.http_ver = http_ver;
        this.status_code = HTTPStatusCode.OK;
        this.content_type = ContentType.TEXT_HTML;
        this.content = new byte[0];
        this.content_length = this.content.length;
        this.connection = "close";
    }

    public void setContent(String content){
        this.content = content.getBytes();
        this.content_length = this.content.length;
    }

    public void setContent(byte[] content){
        this.content = content;
        this.content_length = this.content.length;
    }

    public void setStatusCode(HTTPStatusCode statusCode) {
        this.status_code = statusCode;
    }

    public void setContentType(ContentType contentType) {
        this.content_type = contentType;
    }

    public byte[] getHeadersBytes(){
        StringBuffer result = new StringBuffer();

        result.append("HTTP/").append(this.http_ver).append(" ").append(this.status_code.code);
        result.append("\r\n");
        result.append("Server: " + server);
        result.append("\r\n");
        result.append("Content-Type: ").append(this.content_type.type);
        result.append("\r\n");
        result.append("Content-Length: ").append(this.content_length);
        result.append("\r\n");
        result.append("Connection: ").append(this.connection);
        result.append("\r\n");
        result.append("\r\n");

        return new String(result).getBytes();
    }

    public byte[] getContentBytes(){
        return this.content;
    }

}