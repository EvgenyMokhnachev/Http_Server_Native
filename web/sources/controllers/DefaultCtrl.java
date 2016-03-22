package controllers;

import server.ContentType;
import server.HttpRequest;
import server.HttpResponse;
import server.annotations.HttpMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@HttpMap(path = "/")
public class DefaultCtrl {

    @HttpMap(path = "favicon.png")
    public void favicon(HttpRequest httpRequest, HttpResponse response) {
        File file = new File("web/webapp/images/favicon.png");

        Path path = Paths.get(file.getAbsolutePath());
        byte[] faviconBytes = new byte[0];
        try {
            faviconBytes = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.setContentType(ContentType.IMAGE_PNG);
        response.setContent(faviconBytes);
    }

}
