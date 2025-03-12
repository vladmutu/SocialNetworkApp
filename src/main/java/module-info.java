module org.example.socialnetwork {
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens org.example.socialnetwork to javafx.fxml;
    exports org.example.socialnetwork;
    exports org.example.socialnetwork.Controllers;
    exports org.example.socialnetwork.Domain;
    opens org.example.socialnetwork.Controllers to javafx.fxml;
}