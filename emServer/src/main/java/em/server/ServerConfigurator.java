package em.server;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ServerConfigurator {

    public void setConfigurationFilePath(String path){
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
//            f.setValidating(false);
            DocumentBuilder builder = f.newDocumentBuilder();
            Document doc = builder.parse(Thread.currentThread().getClass().getResourceAsStream(path));

            NodeList controllers = doc.getElementsByTagName("controllers");
            for(int controllerTagIndex = 0; controllerTagIndex < controllers.getLength(); controllerTagIndex++){
                Node controllerTag = controllers.item(controllerTagIndex);

                NodeList controllerTagNodes = controllerTag.getChildNodes();
                for(int controllerTagNodeIndex = 0; controllerTagNodeIndex < controllerTagNodes.getLength(); controllerTagNodeIndex++){
                    Node controllerConfigTag = controllerTagNodes.item(controllerTagNodeIndex);
                    if(controllerConfigTag.getNodeName().equals("autosearch")) {
                        String autoSearchPackage = controllerConfigTag.getTextContent().trim();
                        String[] controllerClasses = new String[0];
                        try {
                            controllerClasses = getResourceListing(ServerConfigurator.class, autoSearchPackage+"/");
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        for(String controllerClassName : controllerClasses) {
                            try {
                                Class<?> controllerClass = Class.forName(autoSearchPackage + "." + controllerClassName.replace(".class", ""));
                                ControllersLoader.setControllerClass(controllerClass);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
        /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null) {
        /*
         * In case of a jar file, we can't actually find a directory.
         * Have to assume the same jar as clazz.
         */
            String me = clazz.getName().replace(".", "/")+".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
        /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
            while(entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) { //filter according to the path
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        entry = entry.substring(0, checkSubdir);
                    }
                    if(entry.trim().length() > 2) {
                        result.add(entry);
                    }
                }
            }
            return result.toArray(new String[result.size()]);
        }

        throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
    }

}
