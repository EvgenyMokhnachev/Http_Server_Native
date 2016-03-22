package server;

import controllers.DefaultCtrl;
import controllers.PageCtrl;
import server.annotations.HttpController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ControllersLoader {

    private static Class[] controllersClasses = new Class[]{
            PageCtrl.class,
            DefaultCtrl.class
    };

    private static Object[] loadedControllers = new Object[controllersClasses.length];

    static {
        int i = 0;
        for(Class cls : controllersClasses){
            try {
                loadedControllers[i++] = Class.forName(cls.getName()).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadController(HttpRequest request, HttpResponse response) {
        Map<Method, Object> foundMethodsByPath = new HashMap<>();

        for(int indexController = 0; indexController < controllersClasses.length; indexController++){
            Class cls = controllersClasses[indexController];
            HttpController classAnnotation = (HttpController) cls.getAnnotation(HttpController.class);
            if(classAnnotation == null) {
                continue;
            }

            if((request.getMethod() == classAnnotation.method()) || classAnnotation.method() == HttpMethod.ANY) {
                Method[] methods = cls.getMethods();

                for(Method method : methods){
                    HttpController methodAnnotation = method.getAnnotation(HttpController.class);
                    if(methodAnnotation == null) {
                        continue;
                    }

                    if(Objects.equals(classAnnotation.path() + methodAnnotation.path(), request.getPath())) {
                        foundMethodsByPath.put(method, loadedControllers[indexController]);
                    }
                }
            }
        }

        Map<Method, Object> foundMethodsByMethod = new HashMap<>();

        if(foundMethodsByPath.isEmpty()) {
            response.setStatusCode(HTTPStatusCode.NOT_FOUND);
        } else {
            for(Map.Entry<Method, Object> entry : foundMethodsByPath.entrySet()) {
                Method method = entry.getKey();
                Object ctrlInstance = entry.getValue();

                HttpController methodAnnotation = method.getAnnotation(HttpController.class);
                if((methodAnnotation.method() == request.getMethod()) || methodAnnotation.method() == HttpMethod.ANY) {
                    foundMethodsByMethod.put(method, ctrlInstance);
                }
            }
        }

        if(foundMethodsByMethod.isEmpty() && !foundMethodsByPath.isEmpty()) {
            response.setStatusCode(HTTPStatusCode.METHOD_NOT_ALLOWED);
        } else {
            for(Map.Entry<Method, Object> entry : foundMethodsByMethod.entrySet()) {
                Method method = entry.getKey();
                Object ctrlInstance = entry.getValue();

                try {
                    method.invoke(ctrlInstance, request, response);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
