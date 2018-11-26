package server.spring.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * AUthentication entry point 401
 */
@Component
public class AuthEntryPointHandler implements AuthenticationEntryPoint {

    /**
     * Commences an authentication scheme
     * @param request that resulted in an AuthenticationException
     * @param response so that the user agent can begin authentication
     * @param authException that caused the invocation
     * @throws IOException
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied!");
    }

}
