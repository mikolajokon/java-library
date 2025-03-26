package java_library.library.exceptions;

/**
 * Wyjątek rzucany, gdy przedmiot jest przetrzymany.
 */
public class OverdueException extends Exception {
    public OverdueException(String message) {
        super(message);
    }
}