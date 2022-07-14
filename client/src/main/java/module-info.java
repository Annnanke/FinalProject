module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    opens org.example to javafx.fxml;
    exports org.example;
    exports org.example.Requesters;
    exports org.example.Objects;
    opens org.example.Requesters to javafx.fxml;
}