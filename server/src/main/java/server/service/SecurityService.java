package server.service;

/**
 * service to check securitycontext has an authentication
 */
public interface SecurityService {
    /**
     * checks if someone is authenticated
     * @return true if someone is authenticated
     */
    Boolean hasProtectedAccess();
}
