package em.server;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ControllersLoader {

    private static Map<Class, Object> controllersEntices = new HashMap<>();

    public static void setControllerClass(Class controllerClass){
        try {
            controllersEntices.put(controllerClass, controllerClass.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void loadController(HttpRequest request, HttpResponse response) {
        Map<Method, Object> foundMethodsByPath = new HashMap<>();

        for(Map.Entry<Class, Object> controllerEntity : controllersEntices.entrySet()) {
            Class cls = controllerEntity.getKey();
            Object clsInst = controllerEntity.getValue();

            HttpMap classAnnotation = (HttpMap) cls.getAnnotation(HttpMap.class);
            if(classAnnotation == null) {
                continue;
            }

            if((request.getMethod() == classAnnotation.method()) || classAnnotation.method() == HttpMethod.ANY) {
                Method[] methods = cls.getMethods();

                for(Method method : methods){
                    HttpMap methodAnnotation = method.getAnnotation(HttpMap.class);
                    if(methodAnnotation == null) {
                        continue;
                    }

                    if(Objects.equals(classAnnotation.path() + methodAnnotation.path(), request.getPath())) {
                        foundMethodsByPath.put(method, clsInst);
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

                HttpMap methodAnnotation = method.getAnnotation(HttpMap.class);
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
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
