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
import java.sql.SQLException;

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

    // TODO: move this static method to Application and refactor the places where this method is used.
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
        Config.getInstance().SELECTED_LANGUAGE = Language.GENERIC; // reset the language
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
                createAlert(e.getMessage(), "Error");
            }
        });
        StringBuilder selectedDirectoryPath = new StringBuilder(); // it is indicating the selected directory name
        // I used StringBuilder because it is effectively final so it can be used in lambdas, unlike String
        assignmentChooser.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                assignmentPath.setText(selectedDirectory.getAbsolutePath());
                selectedDirectoryPath.append(selectedDirectory.getName());
                // TODO: import config if there is a .db file inside the assignment folder
            } else {
                System.out.println("selectedDirectory is null");
            }
        });

        saveButton.setOnAction(actionEvent -> {
                // export config to the assignment path only if there is not a .db file inside
                // .assignit is our assignment config file extension. We could also use .db.



            /**
            Config.getInstance().COMPILER_PATH = compilerPath.getText();
            Config.getInstance().ASSIGNMENT_PATH = assignmentPath.getText();
            Config.getInstance().ARGS = args.getText();
            Config.getInstance().EXPECTED = expected.getText();
            try {
                Database.insertConfig(Config.getInstance());

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            createAlert("Configuration saved successfully.", "Success");**/

        try {

                    if (!selectedDirectoryPath.isEmpty()) {
                        Database.getInstance().createAssignmentConfig(selectedDirectoryPath + "/config.assignit");

                         createAlert("Configuration saved successfully.", "Success");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    createAlert(e.getMessage(), "Error");
                }
            });

        ObservableList<String> comboList = FXCollections.observableArrayList();
        comboList.addAll("C Config", "Python Config", "Emacs Lisp Config");
        configComboBox.setItems(comboList);

        configComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (configComboBox.getValue() == null) {

                } else if (configComboBox.getValue().equals("C Config")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.C;
                    assignmentPath.setText("");
                    compilerPath.setText(CCompiler.COMPILER_PATH);
                    args.setText(CCompiler.ARGS);
                    runField.setVisible(true);
                    runField.setManaged(true);
                    runField.setText(CCompiler.RUN_COMMAND);
                    expected.setText("");
                } else if (configComboBox.getValue().equals("Python Config")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.PYTHON;
                    assignmentPath.setText("");
                    compilerPath.setText(PythonCompiler.COMPILER_PATH);
                    runField.setVisible(false);
                    runField.setManaged(false);
                    args.setText(PythonCompiler.ARGS);
                    expected.setText("");
                } else if (configComboBox.getValue().equals("Emacs Lisp Config")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.LISP;
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
