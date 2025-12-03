module com.example.practica3editordetexto {
    requires javafx.controls;
    requires javafx.fxml;
    requires vosk;
    requires java.desktop;

    exports com.example.practica3editordetexto.editor;
    opens com.example.practica3editordetexto.editor to javafx.fxml;
    exports com.example.practica3editordetexto.editor.controller;
    opens com.example.practica3editordetexto.editor.controller to javafx.fxml;
    exports com.example.practica3editordetexto.view;
    opens com.example.practica3editordetexto.view to javafx.fxml;
}
