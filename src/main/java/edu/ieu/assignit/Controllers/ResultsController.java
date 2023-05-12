package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.Application;
import edu.ieu.assignit.Compilers.CCompiler;
import edu.ieu.assignit.Compilers.Compiler;
import edu.ieu.assignit.Compilers.LispCompiler;
import edu.ieu.assignit.Compilers.PythonCompiler;
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
        try {
            File[] submissions = new File(Config.getInstance().ASSIGNMENT_PATH).listFiles();
            for (File file : submissions) {
                if (!file.isFile()) { // if it is directory
                    System.out.println(file.getName() + " is working directory for compiling");
                    Compiler compiler;
                    switch (Config.getInstance().SELECTED_LANGUAGE) {
                        case C -> compiler = new CCompiler(file);
                        case PYTHON -> compiler = new PythonCompiler(file);
                        case LISP -> compiler = new LispCompiler(file);
                        default -> compiler = new CCompiler(file);
                    }
                    Result result = compiler.compile(Config.getInstance().COMPILER_PATH, Config.getInstance().ARGS);
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
                    } else if (result.getOutput().equals(Config.getInstance().EXPECTED)) {
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
        setupTable();

        backButton.setOnAction(actionEvent -> {
            try {
                Application.changeScene("fxml/config.fxml",
                        300,
                        520);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // TODO: fill the in-loop
    private void exportPassedStudents() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showSaveDialog(primaryStage);
        if (selectedFile != null) {
            try (PrintWriter writer = new PrintWriter(selectedFile)) {
                // write the submission IDs with "correct" result
                for (Submission submission : table.getItems()) {
                    if (submission.getResult().equals("Correct")) {
                        for (MFXTableColumn<Submission> column : table.getTableColumns()) {
                        }
                        // writer.println();
                    }
                }

                Application.createAlert("Passed students are exported to the file", "success");
            } catch (IOException ex) {
                Application.createAlert("Error exporting students: " + ex.getMessage(), "error");
            }
        } else {
            Application.createAlert("Selected file is null", "error");
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
                new StringFilter<>("Output", Submission::getOutput),
                new StringFilter<>("Expected Value", Submission::getExpectedValue),
                new StringFilter<>("Result", Submission::getResult),
                new IntegerFilter<>("Status", Submission::getStatus),
                new StringFilter<>("Error", Submission::getError)
        );

        detailsButton.setOnAction(actionEvent -> handleRowSelection());

    }

    private void handleRowSelection() {
        ObservableMap<Integer, Submission> listValues = table.getSelectionModel().getSelection();
        ObservableList<Submission> submissionsList = FXCollections.observableArrayList(listValues.values());

        System.out.println(submissionsList.listIterator().next()); //TODO: Show alert with submission details
    }

}
