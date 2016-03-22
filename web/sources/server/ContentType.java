package server;

public enum ContentType {

    TEXT_HTML("text/html"),
    IMAGE_X_ICON("image/x-icon"),
    IMAGE_VDN_MICROSOFT_ICON("image/vnd.microsoft.icon"),
    IMAGE_GIF("image/gif"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_BMP("image/bmp");

    public String type;
    ContentType(String type){
        this.type = type;
    }

}
