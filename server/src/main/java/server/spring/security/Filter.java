package server.spring.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import server.service.UserService;
import server.spring.security.jwt.JwtUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * Filter to authenticate with a username & Password combination
 */
public class Filter extends UsernamePasswordAuthenticationFilter {

    /**
     * token utilities
     */
    private JwtUtil jwtUtil;

    /**
     * token header
     */
    @Value("authorization")
    private String tokenHeader;

    /**
     * a user service
     */
    private UserService userDetails;

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Setter for spring to set this field
     *
     * @param userDetails
     */
    @Autowired
    public void setUserDetails(UserService userDetails) {
        this.userDetails = Objects.requireNonNull(userDetails);
    }

    /**
     * Setter for spring to set this field
     * @param jwtUtil
     */
    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = Objects.requireNonNull(jwtUtil);
    }


    /**
     * method checks if user is already authenticated with a token - if not, it uses username and password
     * @param servletRequest request
     * @param servletResponse response
     * @param filterChain servlet filterchain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String authToken = httpRequest.getHeader(tokenHeader);
        LOG.info("doFilter:" + tokenHeader);

        LOG.info("doFilter"+ authToken);
        String username = null;
        LOG.info ("authtoken == null: " + (authToken == null));
        if(authToken != null)
            username = jwtUtil.getUsernameFromToken(authToken.substring(7));
        LOG.info("Username: "+ username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetails.loadUserByUsername(username);
            if (this.jwtUtil.validateToken(authToken.substring(7), userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
