package client.app;


import client.gui.controller.PrimaryController;
import javafx.application.Platform;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * Client's Main Application
 */
@SpringBootApplication(scanBasePackages = {"client/domain", "client/gui", "client/rest", "client/service"})
public class MainApplication extends Application {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Spring Context
     */
    private ConfigurableApplicationContext springContext;


    /**
     * FXML stage
     */
    private Stage primaryStage;

    /**
     * start method
     * @param primaryStage FXML stage
     * @throws Exception any
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;


        // setup primary window
        primaryStage.setTitle("Dominion");
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(event -> LOG.debug("Application shutdown initiated"));

        //Setup spring beans
        springContext = SpringApplication.run(MainApplication.class);

        // prepare fxml loader to inject controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PrimaryWindow.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        primaryStage.setScene(new Scene(fxmlLoader.load()));

        PrimaryController controller = fxmlLoader.getController();
        controller.setPrimaryWindow(this.primaryStage);

        // show application
        primaryStage.show();
        primaryStage.toFront();
        LOG.debug("Application startup complete");
    }

    /**
     * main method - starts application
     * @param args
     */
    public static void main(String[] args) {
        LOG.debug("Application starting with arguments={}", (Object) args);
        Application.launch(MainApplication.class, args);
    }

    /**
     * stop method
     * @throws Exception any
     */
    @Override
    public void stop() throws Exception {
        springContext.stop();
        super.stop();
        Platform.exit();
        System.exit(0);
    }
}