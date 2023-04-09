package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.Application;
import edu.ieu.assignit.CCompiler;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {
    @FXML
    private MFXButton runButton;
    @FXML
    private MFXTextField path;
    @FXML
    private MFXTextField assignmentPath;
    @FXML
    private MFXTextField args;
    @FXML
    private MFXTextField expected;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] list = new String[4];
        
        runButton.setOnAction(actionEvent -> {
            try {
                list[0] = path.getText();
                list[1] = assignmentPath.getText();
                list[2] = args.getText();
                list[3] = expected.getText();
                Application.changeScene("fxml/results.fxml",
                                        600,
                                        400, list);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
