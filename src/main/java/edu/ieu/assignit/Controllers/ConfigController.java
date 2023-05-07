package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.Application;
import edu.ieu.assignit.ZipExtractor;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import edu.ieu.assignit.Config;

import static edu.ieu.assignit.Application.primaryStage;

public class ConfigController implements Initializable {

    @FXML
    private MFXTextField assignmentPath;
    @FXML
    private MFXButton assignmentChooser;
    @FXML
    private MFXTextField compilerPath;
    @FXML
    private MFXTextField args;
    @FXML
    private MFXTextField expected;
    @FXML
    private MFXButton runButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        runButton.setOnAction(actionEvent -> {
            try {
                Config.getInstance().COMPILER_PATH = compilerPath.getText();
                Config.getInstance().ASSIGNMENT_PATH = assignmentPath.getText();
                Config.getInstance().ARGS = args.getText();
                Config.getInstance().EXPECTED = expected.getText();
                ZipExtractor.extract(Config.getInstance().ASSIGNMENT_PATH);

                //TODO: Change scene only if assignment path is valid
                Application.changeScene("fxml/results.fxml",
                        600,
                        400);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        assignmentChooser.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if(selectedDirectory != null) {
                assignmentPath.setText(selectedDirectory.getAbsolutePath());
            } else {
                System.out.println("selectedDirectory is null");
            }

        });


    }
}
