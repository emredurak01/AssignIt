package edu.ieu.assignit;

import edu.ieu.assignit.Controllers.MainController;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {
    public static Stage primaryStage;

    public static void changeScene(String fxml, int width, int height) throws IOException {
        Parent newScene = FXMLLoader.load(Objects.requireNonNull(Application.class.getResource(fxml)));
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.getScene().setRoot(newScene);
    }

    public static void createAlert(String content, String header) {
        MFXGenericDialog dialogContent = MFXGenericDialogBuilder.build().setContentText(content).get();
        MFXStageDialog dialog = MFXGenericDialogBuilder.build(dialogContent).toStageDialogBuilder().initModality(Modality.APPLICATION_MODAL).setDraggable(true).setTitle("Dialog").setScrimPriority(ScrimPriority.WINDOW).setScrimOwner(true).get();

        dialogContent.setMaxSize(1000, 600);


        dialogContent.setHeaderText(header);
        dialog.showDialog();
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("fxml/main.fxml"));
        loader.setControllerFactory(c -> new MainController());
        Parent root = loader.load();
        Scene scene = new Scene(root);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);

       primaryStage.setTitle("AssignIt");
        primaryStage.setWidth(250);
        primaryStage.setHeight(250);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
