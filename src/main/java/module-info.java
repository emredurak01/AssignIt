module com.example.assignit {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.example.assignit to javafx.fxml;
    exports com.example.assignit;
}