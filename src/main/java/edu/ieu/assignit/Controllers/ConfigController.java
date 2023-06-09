package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.*;

// to avoid the class ambiguity

import edu.ieu.assignit.Compilers.*;
import io.github.palexdev.materialfx.controls.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.stage.*;;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

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
    private MFXButton deleteButton;
    @FXML
    private MFXButton runButton;
    @FXML
    private MFXButton runChooser;
    @FXML
    private MFXButton expectedChooser;
    @FXML
    private MFXTextField runFieldArgs;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> comboList = FXCollections.observableArrayList();
        comboList.addAll("Generic", "C", "Python", "Emacs Lisp", "Scheme", "Java", "Haskell");
        configComboBox.setItems(comboList);
        configComboBox.getSelectionModel().selectFirst();
        if (Config.getInstance().COMPILER_PATH != null) {
            compilerPath.setText(Config.getInstance().COMPILER_PATH);
            assignmentPath.setText(Config.getInstance().ASSIGNMENT_PATH);
            args.setText(Config.getInstance().ARGS);
            runField.setText(Config.getInstance().RUN_COMMAND);
            expected.setText(Config.getInstance().EXPECTED);
            Language SELECTED_LANGUAGE = Config.getInstance().SELECTED_LANGUAGE;
            if (SELECTED_LANGUAGE == null) {
            } else if (SELECTED_LANGUAGE.toString().equals("C")) {
                configComboBox.getSelectionModel().selectIndex(1);
            } else if (SELECTED_LANGUAGE.toString().equals("JAVA")) {
                configComboBox.getSelectionModel().selectIndex(5);
            } else if (SELECTED_LANGUAGE.toString().equals("HASKELL")) {
                configComboBox.getSelectionModel().selectIndex(6);
            } else if (SELECTED_LANGUAGE.toString().equals("PYTHON")) {
                configComboBox.getSelectionModel().selectIndex(2);
            } else if (SELECTED_LANGUAGE.toString().equals("LISP")) {
                configComboBox.getSelectionModel().selectIndex(3);
            } else if (SELECTED_LANGUAGE.toString().equals("SCHEME")) {
                configComboBox.getSelectionModel().selectIndex(4);
            }
        }
        runButton.setOnAction(actionEvent -> {
                try {
                    // the null check may be not necessary for all fields, but we are guaranteeing the correct behaviour
                    if (compilerPath.getText() == null ||
                        args.getText() == null ||
                        assignmentPath.getText() == null ||
                        expected.getText() == null ||
                        (runField.isVisible() && runField.getText() == null) ||
                        compilerPath.getText().isBlank() ||
                        assignmentPath.getText().isBlank() ||
                        args.getText().isBlank() ||
                        (runField.getText().isBlank() && runField.isVisible()) ||
                        expected.getText().isBlank()) {
                        throw new Exception("All fields must be filled");
                    }
                    Config.getInstance().COMPILER_PATH = compilerPath.getText();
                    Config.getInstance().ASSIGNMENT_PATH = assignmentPath.getText();
                    Config.getInstance().ARGS = args.getText();
                    Config.getInstance().RUN_COMMAND = runField.getText();
                    Config.getInstance().EXPECTED = expected.getText();
                    Config.getInstance().RUN_ARGS = runFieldArgs.getText();
                    Application.changeScene("fxml/results.fxml", 600, 420);
                } catch (Exception e) {
                    e.printStackTrace();
                    Application.createAlert(e.getMessage(), "Error");
                }
            });

        StringBuilder selectedDirectoryPath = new StringBuilder(); // it is indicating the selected directory name
        // I used StringBuilder because it is effectively final so it can be used in lambdas, unlike String
        assignmentChooser.setOnAction(actionEvent -> {
                try {
                    importConfig(selectedDirectoryPath, comboList);
                    if (assignmentPath != null) {
                        Config.getInstance().ASSIGNMENT_PATH = assignmentPath.getText();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        runChooser.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    String content = Files.readString(selectedFile.toPath());
                    runFieldArgs.setText(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        expectedChooser.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                try {
                    String content = Files.readString(selectedFile.toPath());
                    expected.setText(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        saveButton.setOnAction(actionEvent -> exportConfig(selectedDirectoryPath));
        configComboBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    String comboBoxValue = configComboBox.getValue();
                    String directoryString = Config.getInstance().ASSIGNMENT_PATH;
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

        deleteButton.setOnAction(actionEvent -> {
                String filePath = selectedDirectoryPath + File.separator + "config.assignit";
                File file = new File(filePath);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        Application.createAlert("Configuration deleted successfully.", "Success");
                    } else {
                        Application.createAlert("Failed to delete the configuration.", "Error");
                    }
                } else {
                    Application.createAlert("Configuration could not be found.", "Error");
                }
            });
    }
    private void fillTextFields(String assignmentPathParam, String compilerPathParam, String argsParam, boolean runFieldBool, String runFieldParam) {
        assignmentPath.setText(Objects.requireNonNullElse(assignmentPathParam, ""));
        compilerPath.setText(compilerPathParam);
        args.setText(argsParam);
        runField.setVisible(runFieldBool);
        runField.setManaged(runFieldBool);

        runChooser.getParent().setVisible(runFieldBool);
        runChooser.getParent().setManaged(runFieldBool);
        runField.setText(runFieldParam);
        expected.setText(Config.getInstance().EXPECTED);
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
                Database.getInstance().connect(selectedDirectoryPath + "/config.assignit");
                Config config = Database.getInstance().getConfig();
                Language SELECTED_LANGUAGE = config.SELECTED_LANGUAGE;
                config.ASSIGNMENT_PATH = selectedDirectoryPath.toString();
                String COMPILER_PATH = config.COMPILER_PATH;
                String ARGS = config.ARGS;
                String RUN_COMMAND = config.RUN_COMMAND;

                Database.getInstance().disconnect();
                switch (SELECTED_LANGUAGE.toString()) {
                case "C":
                    configComboBox.getSelectionModel().selectIndex(1);
                    break;
                case "JAVA":
                    configComboBox.getSelectionModel().selectIndex(5);
                    break;
                case "HASKELL":
                    configComboBox.getSelectionModel().selectIndex(6);
                    break;
                case "PYTHON":
                    configComboBox.getSelectionModel().selectIndex(2);
                    break;
                case "LISP":
                    configComboBox.getSelectionModel().selectIndex(3);
                    break;
                case "SCHEME":
                    configComboBox.getSelectionModel().selectIndex(4);
                    break;
                default:
                    configComboBox.getSelectionModel().selectIndex(0);
                }

                config.SELECTED_LANGUAGE = SELECTED_LANGUAGE;
                config.COMPILER_PATH = COMPILER_PATH;
                config.ARGS = ARGS;
                config.RUN_COMMAND = RUN_COMMAND;
                final Set<String> LANGUAGE_WITH_RUN = new HashSet<>(Arrays.asList("C", "JAVA", "HASKELL"));
                if (LANGUAGE_WITH_RUN.contains(config.SELECTED_LANGUAGE.toString())) {
                    fillTextFields(selectedDirectoryPath.toString(), COMPILER_PATH, ARGS, true, RUN_COMMAND);
                } else {
                    fillTextFields(selectedDirectoryPath.toString(), COMPILER_PATH, ARGS, false, "");
                }

            }
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
