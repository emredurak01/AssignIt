package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.Application;
import edu.ieu.assignit.CCompiler;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
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
import edu.ieu.assignit.CCompiler;
import edu.ieu.assignit.PythonCompiler;
import edu.ieu.assignit.LispCompiler;

public class ResultsController implements Initializable {
    @FXML
    private MFXTableView<Person> table;
    @FXML
    private MFXButton backButton;
    private ObservableList<Person> people = FXCollections.observableArrayList();
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
                    Person person = new Person(file.getName(), result.getOutput(), resultString);
                    people.add(person);
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

    // TODO: Please someone add columns for error and status. In addition, there should be a label indicating the expected value. Thank you very much.
    private void setupTable() {
        MFXTableColumn<Person> idColumn = new MFXTableColumn<>("ID", true, Comparator.comparing(Person::getId));
        MFXTableColumn<Person> outputColumn = new MFXTableColumn<>("Output", true, Comparator.comparing(Person::getOutput));
        MFXTableColumn<Person> resultColumn = new MFXTableColumn<>("Result", true, Comparator.comparing(Person::getResult));
        idColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getId));
        outputColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getOutput));
        resultColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getResult));
        table.getTableColumns().addAll(idColumn, outputColumn, resultColumn);
        table.setItems(people);
    }

}
