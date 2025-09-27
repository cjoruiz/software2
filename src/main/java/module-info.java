module co.unicauca {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.sql;
    requires java.base;

    opens co.unicauca.presentation to javafx.fxml;
    opens co.unicauca.solid.domain to javafx.base; 
    exports co.unicauca.presentation; // ← Y esta
}
