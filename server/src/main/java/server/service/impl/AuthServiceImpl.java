package server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import server.service.AuthService;
import server.service.UserService;
import server.service.exception.InvalidTokenException;
import server.service.exception.ServiceException;
import server.spring.security.jwt.JwtUtil;
import shared.dto.AuthRequestDTO;
import shared.dto.AuthResponseDTO;
import shared.dto.UserDTO;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static AuthServiceImpl instance = null;

    public AuthServiceImpl getInstance(){
        if (instance == null)
            instance = new AuthServiceImpl();

        return instance;
    }

    private Map<String,UserDTO> userTokenMap = new HashMap<>();

    private UserService userService;

    private JwtUtil jwtUtil;

    private AuthenticationManager authenticationManager;


    private AuthServiceImpl(){}

    /**
     * Setter (for spring)
     *
     * @param userService
     */
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = Objects.requireNonNull(userService);
    }

    /**
     * Setter (for spring)
     *
     * @param jwtUtil
     */
    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = Objects.requireNonNull(jwtUtil);
    }

    /**
     * Setter (for spring)
     *
     * @param authenticationManager
     */
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = Objects.requireNonNull(authenticationManager);
    }

    public AuthResponseDTO authenticate(AuthRequestDTO authenticationRequest) throws AuthenticationException {
        // Perform the authentication
        Authentication authentication = this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(),
                authenticationRequest.getPassword()
            )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-authentication so we can generate token
        UserDetails userDetails = this.userService.loadUserByUsername(authenticationRequest.getUsername());
        String token = this.jwtUtil.genToken(userDetails);
        LOG.debug("Token for user: " + userDetails.getUsername() +": "+ token);

        // save the token
        UserDTO user;
        try {
            user = this.userService.getUserByUsername(authenticationRequest.getUsername());
        } catch (ServiceException e){
            throw new UsernameNotFoundException(e.getMessage());
        }
        userTokenMap.put(token, user);

        // Return the token
        return new AuthResponseDTO(token);
    }

    public UserDTO getUserFromToken(String token) throws InvalidTokenException {
        UserDTO user = userTokenMap.get(token);
        if (user == null)
            throw new InvalidTokenException();

        return user;
    }
}
