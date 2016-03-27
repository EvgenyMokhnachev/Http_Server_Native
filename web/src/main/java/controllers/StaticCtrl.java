package controllers;

import em.server.*;
import em.server.annotations.HttpMap;
import em.server.enums.ContentType;
import em.server.enums.HTTPStatusCode;
import em.server.enums.HttpMethod;
import em.server.exceptions.FailedGetTheFile;

@HttpMap(path = "/", method = HttpMethod.GET)
public class StaticCtrl {

    private static final String WEB_APP_PATH = "/webapp";

    @HttpMap(path = ".*.js")
    public void javascript(HttpRequest request, HttpResponse response){
        try {
            FileReader fileReader = new FileReader(WEB_APP_PATH + request.getPath());
            response.setContent(fileReader.getBytes());
        } catch (FailedGetTheFile failedGetTheFile) {
            response.setStatusCode(HTTPStatusCode.NOT_FOUND);
        }
        response.setContentType(ContentType.TEXT_JAVASCRIPT);
    }

    @HttpMap(path = ".*.png")
    public void imagePng(HttpRequest request, HttpResponse response){
        try {
            FileReader fileReader = new FileReader(WEB_APP_PATH + request.getPath());
            response.setContent(fileReader.getBytes());
        } catch (FailedGetTheFile failedGetTheFile) {
            response.setStatusCode(HTTPStatusCode.NOT_FOUND);
        }
        response.setContentType(ContentType.IMAGE_PNG);
    }

}
