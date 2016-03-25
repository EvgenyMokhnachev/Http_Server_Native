package controllers;

import em.server.*;

@HttpMap(method = HttpMethod.GET, path = "/")
public class PageCtrl {

    @HttpMap(method = HttpMethod.GET, path = "main")
    public void mainPage(HttpRequest request, HttpResponse httpResponse){
        httpResponse.setContent(HTMLViewer.parse("webapp/html/main.html").getView());
    }

    @HttpMap(method = HttpMethod.GET, path = "")
    public void rootPage(HttpRequest request, HttpResponse httpResponse){
        httpResponse.setContent(HTMLViewer.parse("webapp/html/root.html").getView());
    }

}
