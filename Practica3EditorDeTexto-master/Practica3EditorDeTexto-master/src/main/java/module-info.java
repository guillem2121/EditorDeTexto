module com.example.practica3editordetexto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires vosk;

    exports com.example.practica3editordetexto.editor;

    opens com.example.practica3editordetexto.editor.controller to javafx.fxml;
    opens com.example.practica3editordetexto.view to javafx.fxml;
}
