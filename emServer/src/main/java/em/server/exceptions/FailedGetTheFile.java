package em.server.exceptions;

public class FailedGetTheFile extends Exception {

    private static final String MESSAGE = "Failed to get the file: ";

    public FailedGetTheFile(String path){
        super(MESSAGE + path);
    }

}
