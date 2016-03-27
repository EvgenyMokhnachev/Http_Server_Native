package em.server;

import em.server.exceptions.FailedGetTheFile;

import java.io.*;

public class FileReader {

    private byte[] bytes = new byte[0];

    public FileReader(String path) throws FailedGetTheFile {
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

    public byte[] getBytes(){
        return this.bytes;
    }

}
