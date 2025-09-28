module co.unicauca {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.sql;

    opens co.unicauca.presentation to javafx.fxml; // ← Esta línea
    exports co.unicauca.presentation; // ← Y esta
}
