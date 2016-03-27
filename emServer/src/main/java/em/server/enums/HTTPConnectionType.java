package em.server.enums;

public enum HTTPConnectionType {

    Upgrade("Upgrade"),
    Keep_alive("keep-alive"),
    Close("close");

    public String type;

    HTTPConnectionType(String type){
        this.type = type;
    }

    public static HTTPConnectionType valueOfString(String data){
        HTTPConnectionType[] values = HTTPConnectionType.values();
        for(HTTPConnectionType connectionType : values){
            if(connectionType.type.equals(data)){
                return connectionType;
            }
        }
        return HTTPConnectionType.Close;
    }

}
