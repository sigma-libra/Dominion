package server.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Spring Application Entry Point
 */
@SpringBootApplication(scanBasePackages = {"server/spring/security/jwt","server/service", "server/spring","server/spring/security","server/spring/rest","server/persistence"})
public class ServerApplication {

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
