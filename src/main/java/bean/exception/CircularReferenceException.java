package bean.exception;

/**
 * 循环引用.
 *
 * @author skywalker
 */
public class CircularReferenceException extends RuntimeException {

    public CircularReferenceException(String message) {
        super(message);
    }

}
