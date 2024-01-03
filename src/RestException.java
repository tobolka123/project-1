public class RestException extends Exception {
    public RestException(Exception e) {
        super(e.getLocalizedMessage());
    }

    public RestException(String msg) {
        super(msg);
    }
}