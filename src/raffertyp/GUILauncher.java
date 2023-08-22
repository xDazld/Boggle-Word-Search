package raffertyp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Provides the entry point for launching a JavaFX application with a graphical user interface.
 *
 * @author Patrick Rafferty
 */
public class GUILauncher extends Application {
    /**
     * The main method is the entry point of the application. It is responsible for launching the
     * application by calling the launch method.
     *
     * @param args the command line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        ((Stage) FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("GUI.fxml")))).show();
    }
}
