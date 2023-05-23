package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.Application;
import edu.ieu.assignit.Compilers.*;
import edu.ieu.assignit.Compilers.Compiler;
import edu.ieu.assignit.Config;
import edu.ieu.assignit.Result;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;

import javafx.beans.value.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.io.*;
import edu.ieu.assignit.Controllers.*;

import static edu.ieu.assignit.Application.primaryStage;

public class ResultsController implements Initializable {
    @FXML
    private MFXTableView<Submission> table;
    @FXML
    private MFXButton backButton;
    @FXML
    private MFXButton detailsButton;
    private final ObservableList<Submission> submissions = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File f = new File(Config.getInstance().ASSIGNMENT_PATH + "/results.txt");
        if (f.exists()) {
             importResultsFromFile(f);
             setupTable();
            System.out.println("exits");
        } else {
            try {
                File[] submissions = new File(Config.getInstance().ASSIGNMENT_PATH).listFiles();
                for (File file : submissions) {
                    if (!file.isFile()) { // if it is a directory
                        System.out.println(file.getName() + " is working directory for compiling");
                        Compiler compiler;
                        switch (Config.getInstance().SELECTED_LANGUAGE) {
                            case C:
                                compiler = new CCompiler(file);
                                break;
                            case PYTHON:
                                compiler = new PythonCompiler(file);
                                break;
                            case LISP:
                                compiler = new LispCompiler(file);
                                break;
                            case HASKELL:
                                compiler = new HaskellCompiler(file);
                                break;
                            case SCHEME:
                                compiler = new SchemeCompiler(file);
                                break;
                            case JAVA:
                                compiler = new JavaCompiler(file);
                                break;
                            default:
                                compiler = new CCompiler(file);
                                break;
                        }
                        Result result;
                        if (compiler instanceof JavaCompiler ||
                                compiler instanceof HaskellCompiler ||
                                compiler instanceof CCompiler) {
                            compiler.compile(Config.getInstance().COMPILER_PATH, Config.getInstance().ARGS);
                            result = compiler.run(Config.getInstance().RUN_COMMAND);
                        } else {
                            result = compiler.compile(Config.getInstance().COMPILER_PATH, Config.getInstance().ARGS);
                        }
                        System.out.println(Config.getInstance().COMPILER_PATH + " " + Config.getInstance().ARGS);
                        System.out.println("status: " + result.getStatus());
                        System.out.println("output: " + result.getOutput());
                        System.out.println("error: " + result.getError());
                        System.out.println("expected: " + Config.getInstance().EXPECTED);
                        // check results
                        String isError;
                        String resultString;
                        if (result.getOutput() == null) {
                            resultString = "Incorrect";
                        } else if (result.getOutput().trim().equals(Config.getInstance().EXPECTED.trim())) {
                            resultString = "Correct";
                        } else {
                            resultString = "Incorrect";
                        }
                        if (result.getError() == null) {
                            isError = "None";
                        } else {
                            isError = result.getError();
                        }
                        Submission submission = new Submission(file.getName(), result.getOutput(), resultString, result.getStatus(), isError, Config.getInstance().EXPECTED);
                        this.submissions.add(submission);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Application.createAlert(e.getMessage(), "Error");
            }
            exportTableToFile(f);
            setupTable();
        }

        backButton.setOnAction(actionEvent -> {
            try {
                Application.changeScene("fxml/config.fxml",
                        300,
                        560);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void importResultsFromFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            StringBuilder submissionBr = new StringBuilder();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                submissionBr.append(line);
                if (line.endsWith("$")) {
                    String submissionString = submissionBr.toString();
                    String[] values = submissionString.split(",");

                    if (values.length == 6) {
                        String id = values[0];
                        String output = values[1];
                        String result = values[2];
                        String status = values[3];
                        String error = values[4];
                        String expectedValue = values[5].substring(0, values[5].length() - 1);

                        submissions.add(new Submission(id, output, result, Integer.parseInt(status), error, expectedValue));
                    }
                    submissionBr.setLength(0);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Application.createAlert("Error importing results: " + e.getMessage(), "Error");
        }
    }

    // if there is results.txt in assignment folder, the program does not compile!
    private void exportTableToFile(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Submission submission : submissions) {
                writer.println(submission.getId() + "," + submission.getOutput() + "," +
                        submission.getResult() + "," + submission.getStatus() + "," +
                        submission.getError() + "," + submission.getExpectedValue() + "$"); // $ indicates the submission's end position
            }
            // success
        } catch (IOException e) {
            e.printStackTrace();
            // Application.createAlert("Error exporting table: " + e.getMessage(), "Error");
        }
    }

    private void setupTable() {
        MFXTableColumn<Submission> idColumn = new MFXTableColumn<>("ID", true, Comparator.comparing(Submission::getId));
        MFXTableColumn<Submission> outputColumn = new MFXTableColumn<>("Output", true, Comparator.comparing(Submission::getOutput));
        MFXTableColumn<Submission> expectedValueColumn = new MFXTableColumn<>("Expected Value", true, Comparator.comparing(Submission::getExpectedValue));
        MFXTableColumn<Submission> resultColumn = new MFXTableColumn<>("Result", true, Comparator.comparing(Submission::getResult));
        MFXTableColumn<Submission> statusColumn = new MFXTableColumn<>("Status", true, Comparator.comparing(Submission::getStatus));
        MFXTableColumn<Submission> errorColumn = new MFXTableColumn<>("Error", true, Comparator.comparing(Submission::getError));
        idColumn.setRowCellFactory(submission -> new MFXTableRowCell<>(Submission::getId));
        outputColumn.setRowCellFactory(submission -> new MFXTableRowCell<>(Submission::getOutput));
        resultColumn.setRowCellFactory(submission -> new MFXTableRowCell<>(Submission::getResult));
        statusColumn.setRowCellFactory(submission -> new MFXTableRowCell<>(Submission::getStatus));
        errorColumn.setRowCellFactory(submission -> new MFXTableRowCell<>(Submission::getError));
        expectedValueColumn.setRowCellFactory(submission -> new MFXTableRowCell<>(Submission::getExpectedValue));
        table.getTableColumns().addAll(idColumn, outputColumn, resultColumn, statusColumn, errorColumn, expectedValueColumn);
        table.setItems(submissions);

        table.getFilters().addAll(
                new StringFilter<>("ID", Submission::getId),
                new StringFilter<>("Output", Submission::getOutput), //details
                new StringFilter<>("Expected Value", Submission::getExpectedValue),
                new StringFilter<>("Result", Submission::getResult),
                new IntegerFilter<>("Status", Submission::getStatus),
                new StringFilter<>("Error", Submission::getError) //details
        );

        detailsButton.setOnAction(actionEvent -> handleRowSelection());
    }

    private void handleRowSelection() {
        ObservableMap<Integer, Submission> listValues = table.getSelectionModel().getSelection();
        ObservableList<Submission> submissionsList = FXCollections.observableArrayList(listValues.values());

        if (!submissionsList.isEmpty()) {
            Submission selectedSubmission = submissionsList.iterator().next();

            Application.createAlert("Output: \n" + selectedSubmission.getOutput() + "\n" +
                    "Status: " + selectedSubmission.getStatus() + "\n" +
                    "Expected Value: " + selectedSubmission.getExpectedValue() + "\n" +
                    (selectedSubmission.getError().isEmpty() ? "" : "Error: \n" + selectedSubmission.getError()), "Submission Details");

        }
    }
}