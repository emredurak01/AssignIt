package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.Application;
import edu.ieu.assignit.CCompiler;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import edu.ieu.assignit.Config;
import edu.ieu.assignit.Result;

import edu.ieu.assignit.Compiler;
import edu.ieu.assignit.PythonCompiler;
import edu.ieu.assignit.LispCompiler;

public class ResultsController implements Initializable {
    @FXML
    private MFXTableView<Submission> table;
    @FXML
    private MFXButton backButton;
    private ObservableList<Submission> people = FXCollections.observableArrayList();
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
                    String resultString;
                    if (result.getOutput() == null){
                        resultString = "Incorrect";
                    }else if(result.getOutput().equals(Config.getInstance().EXPECTED)){
                        resultString = "Correct";

                    }else {
                        resultString = "Incorrect";
                    }
                    Submission submission = new Submission(file.getName(), result.getOutput(), resultString, result.getStatus(), result.getError(),Config.getInstance().EXPECTED);
                    people.add(submission);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ConfigController.createAlert(e.getMessage(), "Error");
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

    private void setupTable() {
        MFXTableColumn<Submission> idColumn = new MFXTableColumn<>("ID", true, Comparator.comparing(Submission::getId));
        MFXTableColumn<Submission> outputColumn = new MFXTableColumn<>("Output", true, Comparator.comparing(Submission::getOutput));
        MFXTableColumn<Submission> expectedValueColumn =  new MFXTableColumn<>("Expected Value",true,Comparator.comparing(Submission::getExpectedValue));
        MFXTableColumn<Submission> resultColumn = new MFXTableColumn<>("Result", true, Comparator.comparing(Submission::getResult));
        MFXTableColumn<Submission> statusColumn =  new MFXTableColumn<>("Status",true, Comparator.comparing(Submission::getStatus));
        MFXTableColumn<Submission> errorColumn = new MFXTableColumn<>("Error",true,Comparator.comparing(Submission::getError));
        idColumn.setRowCellFactory(submission -> new MFXTableRowCell<>(Submission::getId));
        outputColumn.setRowCellFactory(submission -> new MFXTableRowCell<>(Submission::getOutput));
        resultColumn.setRowCellFactory(submission -> new MFXTableRowCell<>(Submission::getResult));
        statusColumn.setRowCellFactory(submission -> new MFXTableRowCell<>(Submission::getStatus));
        errorColumn.setRowCellFactory(submission -> new MFXTableRowCell<>(Submission::getError));
        expectedValueColumn.setRowCellFactory(submission -> new MFXTableRowCell<>(Submission::getExpectedValue));
        table.getTableColumns().addAll(idColumn, outputColumn, resultColumn, statusColumn, errorColumn, expectedValueColumn);
        table.setItems(people);
    }

}
