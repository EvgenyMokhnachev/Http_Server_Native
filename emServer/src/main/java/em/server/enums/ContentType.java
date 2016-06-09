package em.server.enums;

public enum ContentType {

    TEXT_HTML("text/html"),
    TEXT_PLAIN("text/plain"),
    TEXT_JAVASCRIPT("text/javascript"),
    TEXT_CSS("text/css"),
    IMAGE_X_ICON("image/x-icon"),
    IMAGE_VDN_MICROSOFT_ICON("image/vnd.microsoft.icon"),
    IMAGE_GIF("image/gif"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_JPG("image/jpg"),
    IMAGE_PNG("image/png"),
    IMAGE_BMP("image/bmp"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APPLICATION_X_ZIP_COMPRESSED("application/x-zip-compressed"),
    APPLICATION_OCTEC_STREAM("application/octet-stream"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    APPLICATION_X_FONT_WOFF("application/x-font-woff"),
    FONT_OPENTYPE("font/opentype");


    public String type;

    ContentType(String type){
        this.type = type;
    }

    public static ContentType valueOfString(String data){
        ContentType[] values = ContentType.values();
        for(ContentType connectionType : values){
            if(data.contains(connectionType.type)){
                return connectionType;
            }
        }
        return ContentType.TEXT_HTML;
    }

}
