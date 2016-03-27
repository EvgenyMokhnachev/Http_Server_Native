package em.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HTMLViewer {

    private String html;

    private HTMLViewer(String htmlPath, Map<String, Object> params) {
        InputStream resourceAsStream = HTMLViewer.class.getClassLoader().getResourceAsStream(htmlPath);
        byte[] result = null;
        try {
            result = new byte[resourceAsStream.available()];
            int resultArrayIndex = 0;
            while (true) {
                int read = resourceAsStream.read();
                if(read == -1) {
                    break;
                } else {
                    result[resultArrayIndex++] = (byte) read;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        String parsedHtml = new String(result);
        for(Map.Entry<String, Object> paramEntity : params.entrySet()){
            int keyIndex = parsedHtml.indexOf("#"+paramEntity.getKey());
            if(keyIndex > -1){
                parsedHtml = parsedHtml.replace("#" + paramEntity.getKey(), (String) paramEntity.getValue());
            }
        }

        this.html = parsedHtml;
    }

    public String getView(){
        return this.html;
    }

    public static HTMLViewer parse(String htmlPath){
        return new HTMLViewer(htmlPath, null);
    }

    public static HTMLViewer parse(String htmlPath, Map<String, Object> params){
        return new HTMLViewer(htmlPath, params);
    }

}
