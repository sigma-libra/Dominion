package server.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class ServiceException extends Exception {
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * @param message Message
     */
    public ServiceException(String message) {
        super(message);
        LOG.error(message, this);
    }

}
