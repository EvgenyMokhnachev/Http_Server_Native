package em.server;

import java.io.*;
import java.util.UUID;

public class MultipartFormData {

    public String Content_Disposition;
    public String Content_Type;

    private final File file;
    private FileOutputStream fileOutputStream;

    public MultipartFormData(){
        this.file = new File("C:\\Users\\johnm\\Desktop\\test\\" + UUID.randomUUID());
        try {
            this.fileOutputStream = new FileOutputStream(this.file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setContent(byte[] content){
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileOutputStream.write(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeStream(){
        try {
            fileOutputStream.close();
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
