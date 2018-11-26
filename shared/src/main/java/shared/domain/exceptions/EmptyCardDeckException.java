package shared.domain.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * exception to be thrown if carddeck is empty
 */
public class EmptyCardDeckException extends Exception {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public EmptyCardDeckException(String s) {
        super(s);
    }
}
