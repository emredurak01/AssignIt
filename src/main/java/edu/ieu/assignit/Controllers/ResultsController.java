package edu.ieu.assignit.Controllers;
import edu.ieu.assignit.Application;

// to avoid the class ambiguity

import edu.ieu.assignit.Compilers.Compiler;
import edu.ieu.assignit.Compilers.*;
import edu.ieu.assignit.*;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.*;
import io.github.palexdev.materialfx.filter.*;
import javafx.collections.*;
import javafx.fxml.*;
import java.io.*;
import java.net.*;
import java.util.*;
public class ResultsController implements Initializable {
    private final ObservableList<Submission> submissions = FXCollections.observableArrayList();
    @FXML
    private MFXTableView<Submission> table;
    @FXML
    private MFXButton backButton;
    @FXML
    private MFXButton detailsButton;
    @FXML
    private MFXButton recompileButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        compile();

        backButton.setOnAction(actionEvent -> {
                try {
                    Application.changeScene("fxml/config.fxml",
                                            300,
                                            650);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        recompileButton.setOnAction(actionEvent -> {
                table.getTableColumns().clear();
                submissions.clear();
                File f = new File(Config.getInstance().ASSIGNMENT_PATH + "/results.txt");
                if (f.exists()) {
                    if (f.delete()) {
                        compile();
                    }
                } else {
                    compile();
                }
            });
    }
    private void compile() {
        ZipExtractor zipExtractor = new ZipExtractor();
        zipExtractor.extract(Config.getInstance().ASSIGNMENT_PATH);
        if (!zipExtractor.getZipExists()) {
            // doing nothing is more appropriate than creating alert i guess
            Application.createAlert("Assignment path does not contain any submision zip files. However, if there are extracted files, they will be compiled / interpreted to be shown on the table.", "Warning");
        }
        File f = new File(Config.getInstance().ASSIGNMENT_PATH + "/results.txt");
        if (f.exists()) {
            importResultsFromFile(f);
            setupTable();
        } else {
            try {
                File[] submissions = new File(Config.getInstance().ASSIGNMENT_PATH).listFiles();
                for (File file : submissions) {
                    if (!file.isFile()) { // if it is a directory
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
                            result = compiler.run(Config.getInstance().RUN_COMMAND, Config.getInstance().RUN_ARGS);
                        } else {
                            result = compiler.compile(Config.getInstance().COMPILER_PATH, Config.getInstance().ARGS);
                        }

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
                        exportTableToFile(f);
                        setupTable();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Application.createAlert(e.getMessage(), "Error");
            }
            
        }
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
