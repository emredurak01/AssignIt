package edu.ieu.assignit.Controllers;

import edu.ieu.assignit.Application;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private MFXButton createNewButton;
    @FXML
    private MFXButton helpButton;
    @FXML
    private MFXButton exitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createNewButton.setOnAction(actionEvent -> {
            try {
                Application.changeScene("fxml/config.fxml", 300, 560);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        helpButton.setOnAction(actionEvent -> {
            String helpContent = "To use the Integrated Assignment Environment (IAE) software, follow these steps:\n\n" +
                    "1. Creating a Project: In AssignIt, every folder which contains submission files with .zip extension, are considered an assignment project. Thus, having a folder like this is enough to create a project.\n\n" +
                    "2. Configuring the Assignment: Once the project is created, set up the specific steps required for the assignment. This includes defining the compilation process, providing command-line arguments, and specifying the expected output for comparison. You can also import and export configurations for sharing or reusing them in the future.\n\n" +
                    "3. Importing Student Files: Importing Student Files: Obtain the folder containing student submissions (containing ZIP files) and place them in a designated directory. Use the file import feature of the software to automatically process ZIP files. The software will extract the files and create separate directories for each student. When you select an assignment containing zip submissions, the imported config will be filled into text fields, If a config exists for the project.\n\n" +
                    "4. Processing Student Files: Click on the 'Run' button to start processing the student files. The software will compile or interpret the source code based on the project configuration. It will then run the student programs with the provided arguments and compare the output with the expected output.\n\n" +
                    "5. Viewing Results: Once the processing is complete, the software will display the results for each student, indicating whether their submission was successful or not. You can view these results within the software's interface, allowing you to assess the performance of each student's assignment. For outputs containing more than one line, all of them can be displayed by pressing the details button.\n\n" +
                    "6. Saving and Opening Projects: The software allows you to update or save the config of your projects for future reference. Use the 'Config' option in the File menu to save your current project. You can reopen projects later by selecting 'Open Project' and navigating to the saved project / assignment folder .If the Delete Configuration button is used, the config in that assignment project (in the folder) is deleted. If the Save Configuration button is used, the configuration in the assignment project (folder) is saved.\n\n" +
                    "7. Help and Support: If you need assistance or have any questions, refer to the Help menu. It provides access to the user manual, contact information for support, frequently asked questions, keyboard shortcuts, and other resources to aid you in using the software effectively.";


            Application.createAlert(helpContent, "Help");
        });


        exitButton.setOnAction(actionEvent -> Platform.exit());
    }


}
