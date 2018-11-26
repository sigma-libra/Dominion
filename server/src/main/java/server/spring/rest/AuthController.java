package server.spring.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import server.service.AuthService;
import shared.dto.AuthRequestDTO;
import shared.dto.AuthResponseDTO;

import java.util.Objects;

/**
 * restcontroller for authentication
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * authentication service
     */
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = Objects.requireNonNull(authService);
    }


    /**
     * authentication rest endpoint
     * @param authenticationRequest http request for auth
     * @return responseentity ok
     * @throws AuthenticationException
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> authenticationRequest(@RequestBody AuthRequestDTO authenticationRequest) throws AuthenticationException {
        AuthResponseDTO authResponseDTO = authService.authenticate(authenticationRequest);

        // Return the token
        return ResponseEntity.ok(authResponseDTO);
    }
}
