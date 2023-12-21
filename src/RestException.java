public class RestException extends Throwable {
    public RestException(Exception e) {
        super(e.getLocalizedMessage());
    }

    public RestException(String msg) {
        super(msg);
    }
}