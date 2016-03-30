package em.server;

import em.server.enums.ContentType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class MultipartForm {

    private static final File tempMultipart;

    static {
        tempMultipart = new File("tempMultipart");
        if(!tempMultipart.exists()) {
            tempMultipart.mkdir();
        }
    }

    private FormDataText[] formDataTexts;

    private FormDataFile[] formDataFiles;

    public MultipartForm(){
        this.formDataTexts = new FormDataText[0];
        this.formDataFiles = new FormDataFile[0];
    }

    public FormDataText createFormDataText(String name){
        return new FormDataText(name);
    }

    public FormDataFile createFormDataFile(String name, String fileName, ContentType contentType){
        return new FormDataFile(name, fileName, contentType);
    }

    public abstract class FormData {
        private String name;

        private FormData(String name){
            this.name = name;
        }

        public abstract void appendData(byte[] bytesData);

        public abstract void endWrite();
    }

    private class FormDataText extends FormData {
        private String data = "";

        private FormDataText(String name) {
            super(name);

            formDataTexts = Arrays.copyOf(formDataTexts, formDataTexts.length + 1);
            formDataTexts[formDataTexts.length - 1] = this;
        }

        @Override
        public void appendData(byte[] bytesData){
            data = data + new String(bytesData).replace("\r\n", "");
        }

        @Override
        public void endWrite() {

        }
    }

    private class FormDataFile extends FormData {
        private String fileName;
        private ContentType contentType;
        private File file;

        private FileOutputStream fileOutputStream;

        private FormDataFile(String name, String fileName, ContentType contentType) {
            super(name);
            this.fileName = fileName;
            this.contentType = contentType;
            this.file = new File("tempMultipart/" + UUID.randomUUID() + fileName);

            try {
                this.fileOutputStream = new FileOutputStream(this.file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            formDataFiles = Arrays.copyOf(formDataFiles, formDataFiles.length + 1);
            formDataFiles[formDataFiles.length - 1] = this;
        }

        @Override
        public void appendData(byte[] bytesData){
            try {
                this.fileOutputStream.write(bytesData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void endWrite() {
            try {
                this.fileOutputStream.flush();
                this.fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeTempFiles(){
        for(FormDataFile formDataFile : formDataFiles){
            if(!formDataFile.file.delete()) formDataFile.file.deleteOnExit();
        }
    }

}
