package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.Application;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private MFXButton createNewButton;
    @FXML
    private MFXButton exitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createNewButton.setOnAction(actionEvent -> {
                try {
                    Application.changeScene("fxml/config.fxml",
                                            275,
                                            500,
                                            null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        exitButton.setOnAction(actionEvent -> Platform.exit());
    }
}
