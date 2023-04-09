package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.Application;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {
    @FXML
    private MFXButton runButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        runButton.setOnAction(actionEvent -> {
            try {
                Application.changeScene("fxml/results.fxml",
                                        600,
                                        400);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
