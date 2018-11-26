package server.service.impl;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import server.service.SecurityService;

@Service("securityService")
public class SecurityServiceImpl implements SecurityService {
    @Override
    public Boolean hasProtectedAccess() {
        return (SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }
}
