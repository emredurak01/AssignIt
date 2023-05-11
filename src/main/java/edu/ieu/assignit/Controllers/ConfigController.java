package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.*;
import edu.ieu.assignit.Compilers.CCompiler;
import edu.ieu.assignit.Compilers.LispCompiler;
import edu.ieu.assignit.Compilers.PythonCompiler;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<String> comboList = FXCollections.observableArrayList();

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
                    Application.createAlert("Assignment path does not contain any zip files.", "Error");
                }

            } catch (IOException e) {
                Application.createAlert(e.getMessage(), "Error");
            }
        });

        StringBuilder selectedDirectoryPath = new StringBuilder(); // it is indicating the selected directory name
        // I used StringBuilder because it is effectively final so it can be used in lambdas, unlike String
        assignmentChooser.setOnAction(actionEvent -> {
            try {
                importConfig(selectedDirectoryPath, comboList);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        saveButton.setOnAction(actionEvent -> exportConfig(selectedDirectoryPath));

        comboList.addAll("C Config", "Python Config", "Emacs Lisp Config");
        configComboBox.setItems(comboList);
        configComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (configComboBox.getValue() == null) {

                } else if (configComboBox.getValue().equals("C Config")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.C;
                    fillTextFields(CCompiler.COMPILER_PATH, CCompiler.ARGS, true, CCompiler.RUN_COMMAND);
                } else if (configComboBox.getValue().equals("Python Config")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.PYTHON;
                    fillTextFields(PythonCompiler.COMPILER_PATH, PythonCompiler.ARGS, false, "");
                } else if (configComboBox.getValue().equals("Emacs Lisp Config")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.LISP;
                    fillTextFields(LispCompiler.COMPILER_PATH, LispCompiler.ARGS, false, "");
                }
            }
        });

    }

    private void fillTextFields(String compilerPathParam, String argsParam, boolean runFieldBool, String runFieldParam) {
        assignmentPath.setText("");
        compilerPath.setText(compilerPathParam);
        args.setText(argsParam);
        runField.setVisible(runFieldBool);
        runField.setManaged(runFieldBool);
        runField.setText(runFieldParam);
        expected.setText("");
    }

    private void importConfig(StringBuilder selectedDirectoryPath, ObservableList<String> comboList) throws SQLException {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            String directoryPath = selectedDirectory.getAbsolutePath();
            assignmentPath.setText(directoryPath);
            assignmentPath.appendText("");
            assignmentPath.end();
            selectedDirectoryPath.setLength(0);
            selectedDirectoryPath.append(selectedDirectory.getName());

            File directory = new File(directoryPath);
            File file = new File(directory, "config.assignit");

            if (file.exists()) {
                System.out.println("File exists.");
                comboList.add("Custom Config");

                Config config = Database.getInstance().getConfig();
                if (config.SELECTED_LANGUAGE.toString().equals("C")){
                    fillTextFields(config.COMPILER_PATH,config.ARGS,true,config.RUN_COMMAND);

                }
                else {
                    fillTextFields(config.COMPILER_PATH,config.ARGS,false,"");

                }

            } else {
                System.out.println("File does not exist.");
            }
        } else {
            System.out.println("selectedDirectory is null");
        }

    }

    private void exportConfig(StringBuilder selectedDirectoryPath) {
        // export config to the assignment path only if there is not a .db file inside
        // .assignit is our assignment config file extension. We could also use .db.
        try {
            if (!selectedDirectoryPath.isEmpty()) {
                Database.getInstance().createAssignmentConfig(selectedDirectoryPath + "/config.assignit");
                Database.getInstance().addConfig(compilerPath.getText(), args.getText(), expected.getText(), runField.getText(), Config.getInstance().SELECTED_LANGUAGE.toString());
                Database.getInstance().disconnect();

                Application.createAlert("Configuration saved successfully.", "Success");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Application.createAlert(e.getMessage(), "Error");
        }
    }
}
