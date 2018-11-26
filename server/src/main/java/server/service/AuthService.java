package server.service;

import org.springframework.security.core.AuthenticationException;
import server.service.exception.InvalidTokenException;
import shared.dto.AuthRequestDTO;
import shared.dto.AuthResponseDTO;
import shared.dto.UserDTO;

/**
 * service to authenticate against the server
 */
public interface AuthService {

    /**
     * authenticates against the server
     * @param authenticationRequest http request to authenticate with
     * @return http response
     * @throws AuthenticationException if something goes wrong
     */
    AuthResponseDTO authenticate(AuthRequestDTO authenticationRequest) throws AuthenticationException;

    UserDTO getUserFromToken(String token) throws InvalidTokenException;
}
