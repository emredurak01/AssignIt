/**
 *
 */
module edu.ieu.assignit {
    requires javafx.controls;
    requires javafx.fxml;
    requires MaterialFX;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    opens edu.ieu.assignit to javafx.fxml;
    exports edu.ieu.assignit;
    exports edu.ieu.assignit.Controllers;
    opens edu.ieu.assignit.Controllers to javafx.fxml;
    exports edu.ieu.assignit.Compilers;
    opens edu.ieu.assignit.Compilers to javafx.fxml;
}