package server.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Objects;

/**
 * Securityconfiguration
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * Entry point
     */
    private final AuthEntryPointHandler authEntryPointHandler;

    /**
     * user service
     */
    private final UserDetailsService userDetailsService;

    /**
     * Constructor
     *
     * @param authEntryPointHandler
     * @param userDetailsService
     */
    @Autowired
    public SecurityConfiguration(AuthEntryPointHandler authEntryPointHandler, UserDetailsService userDetailsService) {
        this.authEntryPointHandler = Objects.requireNonNull(authEntryPointHandler);
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
    }


    /**
     * password encoder to decode a the password what's retrieved by db
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * configures auth
     * @param authenticationManagerBuilder
     * @throws Exception
     */
    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
            .userDetailsService(this.userDetailsService)
            .passwordEncoder(passwordEncoder());
    }


    /**
     * filter
     * @return
     * @throws Exception
     */
    @Bean
    public Filter authenticationTokenFilterBean() throws Exception {
        Filter authenticationTokenFilter = new Filter();
        authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationTokenFilter;
    }


    /**
     * allow all GET Requests @/user/*
     * allow /auth
     * @param httpSecurity
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.
            csrf().disable()
            .exceptionHandling().authenticationEntryPoint(authEntryPointHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests().antMatchers("/user/**").permitAll()
                                .antMatchers("/game/**").permitAll()
                                .antMatchers("/stat/**").permitAll()
                                .antMatchers("/auth/**").permitAll().anyRequest().authenticated();
        httpSecurity.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }



}
