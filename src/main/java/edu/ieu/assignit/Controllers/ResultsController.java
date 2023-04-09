package edu.ieu.assignit.Controllers;

import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

public class ResultsController implements Initializable {
    @FXML
    private MFXTableView<Person> table;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();
    }

    private void setupTable() {
        MFXTableColumn<Person> idColumn = new MFXTableColumn<>("ID", true, Comparator.comparing(Person::getId));
        MFXTableColumn<Person> outputColumn = new MFXTableColumn<>("Output", true,
                Comparator.comparing(Person::getOutput));
        MFXTableColumn<Person> resultColumn = new MFXTableColumn<>("Result", true,
                Comparator.comparing(Person::getResult));
        idColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getId));
        outputColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getOutput));
        resultColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getResult));
        table.getTableColumns().addAll(idColumn, outputColumn, resultColumn);

        // Dummy objects
        ObservableList<Person> people = FXCollections.observableArrayList();
        people.add(new Person("2020060201", "1,2,3", "Correct"));
        people.add(new Person("2020060202", "2,1,3", "Incorrect"));
        people.add(new Person("2020060203", "2,1,3", "Incorrect"));
        people.add(new Person("2020060204", "1,2,3", "Correct"));
        people.add(new Person("2020060205", "1,2,3", "Correct"));
        people.add(new Person("2020060206", "1,2,3", "Correct"));
        people.add(new Person("2020060207", "3,2,1", "Incorrect"));
        people.add(new Person("2020060208", "1,2,3", "Correct"));
        people.add(new Person("2020060209", "2,3,1", "Incorrect"));
        people.add(new Person("2020060200", "1,2,3", "Correct"));
        table.setItems(people);
    }
}
