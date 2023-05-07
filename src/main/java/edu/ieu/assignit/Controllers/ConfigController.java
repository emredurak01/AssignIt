package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.Application;
import edu.ieu.assignit.ZipExtractor;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import edu.ieu.assignit.Config;
import edu.ieu.assignit.CCompiler;
import static edu.ieu.assignit.Application.primaryStage;

public class ConfigController implements Initializable {

    @FXML
    private MFXTextField assignmentPath;
    @FXML
    private MFXButton assignmentChooser;
    @FXML
    private MFXTextField compilerPath;
    @FXML
    private MFXTextField args;
    @FXML
    private MFXTextField expected;
    @FXML
    private MFXButton saveButton;
    @FXML
    private MFXButton runButton;

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
        compilerPath.setText(CCompiler.COMPILER_PATH);
        assignmentPath.setText("cSampleAssignment");
        args.setText(CCompiler.ARGS);
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
                throw new RuntimeException(e);
            }
        });

        assignmentChooser.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                assignmentPath.setText(selectedDirectory.getAbsolutePath());
            } else {
                System.out.println("selectedDirectory is null");
            }

        });

        saveButton.setOnAction(actionEvent -> createAlert("Configuration saved successfully.", "Success"));

    }
}
