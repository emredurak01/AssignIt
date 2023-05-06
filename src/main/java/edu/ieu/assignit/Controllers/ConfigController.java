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
        String[] list = new String[4];

        runButton.setOnAction(actionEvent -> {
            try {
                list[0] = compilerPath.getText();
                list[1] = assignmentPath.getText();
                list[2] = args.getText();
                list[3] = expected.getText();

                ZipExtractor.extract(list[1]);

                //TODO: Change scene only if assignment path is valid
                Application.changeScene("fxml/results.fxml",
                        600,
                        400, list);
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
