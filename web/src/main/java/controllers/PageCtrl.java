package controllers;

import em.server.HttpMap;
import em.server.HttpMethod;
import em.server.HttpRequest;
import em.server.HttpResponse;

@HttpMap(method = HttpMethod.GET, path = "/")
public class PageCtrl {

    @HttpMap(method = HttpMethod.GET, path = "main")
    public void mainPage(HttpRequest request, HttpResponse httpResponse){
        httpResponse.setContent("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <link rel=\"icon\" href=\"/favicon.png\" type=\"image/png\">\n" +
                "    <title>simple page</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "main page\n" +
                "</body>\n" +
                "</html>");
    }

    @HttpMap(method = HttpMethod.GET, path = "")
    public void rootPage(HttpRequest request, HttpResponse httpResponse){
        httpResponse.setContent("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <link rel=\"icon\" href=\"/favicon.png\" type=\"image/png\">\n" +
                "    <title>simple page</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "root page\n" +
                "</body>\n" +
                "</html>");
    }

}