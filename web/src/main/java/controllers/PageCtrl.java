package controllers;

import em.server.*;
import em.server.annotations.HttpMap;
import em.server.enums.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@HttpMap(method = HttpMethod.GET, path = "/")
public class PageCtrl {

    @HttpMap(method = HttpMethod.GET, path = "main")
    public void mainPage(HttpRequest request, HttpResponse httpResponse){
        Map<String, Object> params = new HashMap<>();
        params.put("title", "main page title");
        params.put("bodyContent", "main page body content");
        httpResponse.setContent(HTMLViewer.parse("webapp/html/page.html", params).getView());
    }

    @HttpMap(method = HttpMethod.GET, path = "")
    public void rootPage(HttpRequest request, HttpResponse httpResponse){
        Map<String, Object> params = new HashMap<>();
        params.put("title", "root page title");
        params.put("bodyContent", "root page body content");
        httpResponse.setContent(HTMLViewer.parse("webapp/html/page.html", params).getView());
    }

}
