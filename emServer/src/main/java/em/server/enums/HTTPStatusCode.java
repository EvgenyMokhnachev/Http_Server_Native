package em.server.enums;

public enum HTTPStatusCode {

    OK(200),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405);

    public int code;
    HTTPStatusCode(int code){
        this.code = code;
    }

}
