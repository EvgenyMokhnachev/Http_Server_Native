package controllers;

import em.server.HttpRequest;
import em.server.HttpResponse;
import em.server.annotations.HttpMap;

@HttpMap(path = "/")
public class UploadFile {

    @HttpMap(path = "uploadFile.*")
    public void uploadFile(HttpRequest request, HttpResponse response) {
        int i = 0;
    }

}
