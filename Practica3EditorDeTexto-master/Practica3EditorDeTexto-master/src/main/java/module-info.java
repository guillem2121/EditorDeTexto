module com.example.practica3editordetexto {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.practica3editordetexto to javafx.fxml;
    exports com.example.practica3editordetexto;
}
