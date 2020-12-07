package games;

/**
 * This exception indicates that a player made an erroneous action
 * (e.g. attempted to discard a card it does not have in hand).
 * It is thrown in the DebuggingGame class.
 * 
 * @author Steven Bogaerts
 */
public class BadActionException extends RuntimeException {

    public BadActionException(String message) {
        super(message);
    }
    
}