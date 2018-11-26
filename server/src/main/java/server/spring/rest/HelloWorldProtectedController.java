package server.spring.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;

/**
 * controller for testing purposes
 */
@RestController
public class HelloWorldProtectedController {


    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    /**
     * checks if security context has a valid authentication
     * @param request request
     * @return responseentity.ok
     */
    @RequestMapping(value="/loginIsValid", method = RequestMethod.GET)
    public ResponseEntity<?> loginIsValid(HttpServletRequest request) {
        LOG.info("Authenticated?: " +
            Boolean.toString(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()));
        return ResponseEntity.ok("valid");
    }
}