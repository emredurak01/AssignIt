package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static edu.ieu.assignit.Application.primaryStage;

public class ConfigController implements Initializable {

    @FXML
    private MFXComboBox<String> configComboBox;
    @FXML
    private MFXTextField assignmentPath;
    @FXML
    private MFXButton assignmentChooser;
    @FXML
    private MFXTextField compilerPath;
    @FXML
    private MFXTextField args;
    @FXML
    private MFXTextField runField;
    @FXML
    private MFXTextField expected;
    @FXML
    private MFXButton saveButton;
    @FXML
    private MFXButton runButton;

    public static void createAlert(String content, String header) {
        MFXGenericDialog dialogContent = MFXGenericDialogBuilder.build().setContentText(content).get();
        MFXStageDialog dialog = MFXGenericDialogBuilder.build(dialogContent).toStageDialogBuilder().initModality(Modality.APPLICATION_MODAL).setDraggable(true).setTitle("Dialog").setScrimPriority(ScrimPriority.WINDOW).setScrimOwner(true).get();

        dialogContent.setMaxSize(600, 600);

        //MFXFontIcon infoIcon = new MFXFontIcon("", 18);
        //dialogContent.setHeaderIcon(infoIcon);

        dialogContent.setHeaderText(header);
        dialog.showDialog();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        runButton.setOnAction(actionEvent -> {
            try {
                Config.getInstance().COMPILER_PATH = compilerPath.getText();
                Config.getInstance().ASSIGNMENT_PATH = assignmentPath.getText();
                Config.getInstance().ARGS = args.getText();
                Config.getInstance().EXPECTED = expected.getText();

                ZipExtractor zipExtractor = new ZipExtractor();
                zipExtractor.extract(Config.getInstance().ASSIGNMENT_PATH);

                if (zipExtractor.getZipExists()) {
                    Application.changeScene("fxml/results.fxml", 600, 420);
                } else {
                    createAlert("Assignment path does not contain any zip files.", "Error");
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        assignmentChooser.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                assignmentPath.setText(selectedDirectory.getAbsolutePath());
            } else {
                System.out.println("selectedDirectory is null");
            }

        });

        saveButton.setOnAction(actionEvent -> createAlert("Configuration saved successfully.", "Success"));

        ObservableList<String> comboList = FXCollections.observableArrayList();
        comboList.addAll("Default C Config", "Default Python Config", "Default Lisp Config");
        configComboBox.setItems(comboList);

        configComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (configComboBox.getValue() == null) {

                } else if (configComboBox.getValue().equals("Default C Config")) {
                    assignmentPath.setText("");
                    compilerPath.setText(CCompiler.COMPILER_PATH);
                    args.setText(CCompiler.ARGS);
                    runField.setVisible(true);
                    runField.setManaged(true);
                    runField.setText(CCompiler.RUN_COMMAND);
                    expected.setText("");
                } else if (configComboBox.getValue().equals("Default Python Config")) {
                    assignmentPath.setText("");
                    compilerPath.setText(PythonCompiler.COMPILER_PATH);
                    runField.setVisible(false);
                    runField.setManaged(false);
                    args.setText(PythonCompiler.ARGS);
                    expected.setText("");
                } else if (configComboBox.getValue().equals("Default Lisp Config")) {
                    assignmentPath.setText("");
                    compilerPath.setText(LispCompiler.COMPILER_PATH);
                    runField.setVisible(false);
                    runField.setManaged(false);
                    args.setText(LispCompiler.ARGS);
                    expected.setText("");
                }
            }
        });


    }
}
