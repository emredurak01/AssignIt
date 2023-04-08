module com.example.assignit {
    requires javafx.controls;
    requires javafx.fxml;
    requires MaterialFX;
            
                            
    opens edu.ieu.assignit to javafx.fxml;
    exports edu.ieu.assignit;
    exports edu.ieu.assignit.Controllers;
    opens edu.ieu.assignit.Controllers to javafx.fxml;
}