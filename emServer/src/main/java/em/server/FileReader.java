package em.server;

import em.server.exceptions.FailedGetTheFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileReader {

    private byte[] bytes = new byte[0];

    private static final Map<String, byte[]> cache = new HashMap<>();

    public FileReader(String path) throws FailedGetTheFile {
        if(cache.get(path) != null) {
            bytes = cache.get(path);
        } else {
            InputStream resourceAsStream = null;
            try {
                resourceAsStream = FileReader.class.getResourceAsStream(path);
                if(resourceAsStream == null) {
                    resourceAsStream = new FileInputStream(new File(path));
                }
                byte[] fileBytes = new byte[resourceAsStream.available()];
                int fileBytesCurrentIndex = 0;
                while (true) {
                    int currentByte = resourceAsStream.read();
                    if(currentByte < 0) {
                        break;
                    }

                    fileBytes[fileBytesCurrentIndex++] = (byte) currentByte;
                }
                this.bytes = fileBytes;
                cache.put(path, fileBytes);
            } catch (Exception e) {
                throw new FailedGetTheFile(path);
            } finally {
                if(resourceAsStream != null) {
                    try {
                        resourceAsStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public byte[] getBytes(){
        return this.bytes;
    }

}
