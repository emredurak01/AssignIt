package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.*;
import edu.ieu.assignit.Compilers.*;
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
import java.util.Objects;
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
        comboList.addAll("Generic", "C", "Python", "Emacs Lisp", "Scheme", "Java", "Haskell");
        configComboBox.setItems(comboList);
        configComboBox.getSelectionModel().selectFirst();

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
        configComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String comboBoxValue = configComboBox.getValue();
                String directoryString = selectedDirectoryPath.toString();
                if (comboBoxValue == null) {
                } else if (comboBoxValue.equals("Generic")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.GENERIC;
                    fillTextFields(directoryString, "", "", true, "");
                } else if (comboBoxValue.equals("C")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.C;
                    fillTextFields(directoryString, CCompiler.COMPILER_PATH, CCompiler.ARGS, true, CCompiler.RUN_COMMAND);
                } else if (comboBoxValue.equals("Python")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.PYTHON;
                    fillTextFields(directoryString, PythonCompiler.COMPILER_PATH, PythonCompiler.ARGS, false, "");
                } else if (comboBoxValue.equals("Emacs Lisp")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.LISP;
                    fillTextFields(directoryString, LispCompiler.COMPILER_PATH, LispCompiler.ARGS, false, "");
                } else if (comboBoxValue.equals("Scheme")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.SCHEME;
                    fillTextFields(directoryString, SchemeCompiler.COMPILER_PATH, SchemeCompiler.ARGS, false, "");
                } else if (comboBoxValue.equals("Java")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.JAVA;
                    fillTextFields(directoryString, JavaCompiler.COMPILER_PATH, JavaCompiler.ARGS, true, JavaCompiler.RUN_COMMAND);
                } else if (comboBoxValue.equals("Haskell")) {
                    Config.getInstance().SELECTED_LANGUAGE = Language.HASKELL;
                    fillTextFields(directoryString, HaskellCompiler.COMPILER_PATH, HaskellCompiler.ARGS, true, HaskellCompiler.RUN_COMMAND);
                }
            }
        });

    }

    private void fillTextFields(String assignmentPathParam, String compilerPathParam, String argsParam, boolean runFieldBool, String runFieldParam) {
        assignmentPath.setText(Objects.requireNonNullElse(assignmentPathParam, ""));
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
            String directoryPath = selectedDirectory.getPath();
            assignmentPath.setText(directoryPath);
            assignmentPath.appendText("");
            assignmentPath.end();
            selectedDirectoryPath.setLength(0);
            selectedDirectoryPath.append(selectedDirectory.getPath());

            File directory = new File(directoryPath);
            File file = new File(directory, "config.assignit");

            if (file.exists()) {
                System.out.println("File exists.");

                // TODO: use switch and get rid of duplicate code
                Database.getInstance().connect(selectedDirectoryPath + "/config.assignit");
                Config config = Database.getInstance().getConfig();
                Config.getInstance().COMPILER_PATH = config.COMPILER_PATH;
                Config.getInstance().ASSIGNMENT_PATH = config.ASSIGNMENT_PATH;
                Config.getInstance().ARGS = config.ARGS;
                Config.getInstance().RUN_COMMAND = config.RUN_COMMAND;
                Config.getInstance().EXPECTED = config.EXPECTED;
                Database.getInstance().disconnect();
                if (config.SELECTED_LANGUAGE.toString().equals("C")) {
                    configComboBox.getSelectionModel().selectIndex(1);
                    fillTextFields(selectedDirectoryPath.toString(), config.COMPILER_PATH, config.ARGS, true, config.RUN_COMMAND);
                } else if (config.SELECTED_LANGUAGE.toString().equals("JAVA")) {
                    configComboBox.getSelectionModel().selectIndex(5);
                    fillTextFields(selectedDirectoryPath.toString(), config.COMPILER_PATH, config.ARGS, true, config.RUN_COMMAND);
                } else if (config.SELECTED_LANGUAGE.toString().equals("HASKELL")) {
                    configComboBox.getSelectionModel().selectIndex(6);
                    fillTextFields(selectedDirectoryPath.toString(), config.COMPILER_PATH, config.ARGS, true, config.RUN_COMMAND);
                } else if (config.SELECTED_LANGUAGE.toString().equals("PYTHON")) {
                    configComboBox.getSelectionModel().selectIndex(2);
                    fillTextFields(selectedDirectoryPath.toString(), config.COMPILER_PATH, config.ARGS, false, "");
                } else if (config.SELECTED_LANGUAGE.toString().equals("LISP")) {
                    configComboBox.getSelectionModel().selectIndex(3);
                    fillTextFields(selectedDirectoryPath.toString(), config.COMPILER_PATH, config.ARGS, false, "");
                } else if (config.SELECTED_LANGUAGE.toString().equals("SCHEME")) {
                    configComboBox.getSelectionModel().selectIndex(4);
                    fillTextFields(selectedDirectoryPath.toString(), config.COMPILER_PATH, config.ARGS, false, "");
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
                Database.getInstance().connect(selectedDirectoryPath + "/config.assignit");
                Database.getInstance().createAssignmentConfig();
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
