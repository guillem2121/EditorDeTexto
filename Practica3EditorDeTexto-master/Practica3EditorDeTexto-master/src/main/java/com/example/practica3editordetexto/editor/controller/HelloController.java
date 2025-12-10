package com.example.practica3editordetexto.editor.controller;

import com.example.practica3editordetexto.nui.NuiCommand;
import com.example.practica3editordetexto.nui.NuiListener;
import com.example.practica3editordetexto.nui.VoskVoiceRecognizer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.example.practica3editordetexto.view.ProgressLabel;

import java.io.*;
import java.nio.file.Files;

public class HelloController implements NuiListener {

    // --- Conexiones con el FXML ---
    @FXML
    private TextArea areaTextoPrincipal;

    @FXML
    private Label labelCaracteres;

    @FXML
    private Label labelPalabras;

    @FXML
    private ProgressLabel componenteProgreso;
    private static final long SIZE_THRESHOLD_BYTES = 1024;

    private VoskVoiceRecognizer vozRecorder;

    // --- Lógica de la Aplicación ---

    @FXML
    public void initialize() {

        componenteProgreso.setState(ProgressLabel.State.IDLE);
        componenteProgreso.setProgress(0.0);

        // El único listener que necesitamos es para actualizar los contadores.
        areaTextoPrincipal.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarContadores(newValue);
        });

        // Inicializamos los contadores.
        actualizarContadores(areaTextoPrincipal.getText());

        // Inicializar reconocimiento de voz
        vozRecorder = new VoskVoiceRecognizer(this);
        vozRecorder.startListening();
    }

    /**
     * Actualiza los contadores de caracteres y palabras.
     */
    private void actualizarContadores(String texto) {
        if (texto == null)
            texto = "";

        int caracteres = texto.length();
        int palabras = 0;
        String textoTrim = texto.trim();
        if (!textoTrim.isEmpty()) {
            palabras = textoTrim.split("\\s+").length;
        }

        labelCaracteres.setText(String.valueOf(caracteres));
        labelPalabras.setText(String.valueOf(palabras));
    }

    // --- Métodos de Modificación de Texto ---

    @FXML
    public void convertirMayusculas(ActionEvent event) {
        areaTextoPrincipal.setText(areaTextoPrincipal.getText().toUpperCase());
    }

    @FXML
    public void convertirMinusculas(ActionEvent event) {
        areaTextoPrincipal.setText(areaTextoPrincipal.getText().toLowerCase());
    }

    @FXML
    public void invertirTexto(ActionEvent event) {
        String textoInvertido = new StringBuilder(areaTextoPrincipal.getText()).reverse().toString();
        areaTextoPrincipal.setText(textoInvertido);
    }

    @FXML
    public void limpiarTexto(ActionEvent event) {
        areaTextoPrincipal.clear();
    }

    @FXML
    public void eliminarEspacios(ActionEvent event) {
        String texto = areaTextoPrincipal.getText();
        areaTextoPrincipal.setText(texto.trim().replaceAll("\\s+", " "));
    }

    // --- Métodos de Estilo ---

    @FXML
    public void aplicarNegrita(ActionEvent event) {
        if (areaTextoPrincipal.getStyle().contains("bold")) {
            areaTextoPrincipal.setStyle(areaTextoPrincipal.getStyle().replace("-fx-font-weight: bold;", ""));
        } else {
            areaTextoPrincipal.setStyle(areaTextoPrincipal.getStyle() + "-fx-font-weight: bold;");
        }
    }

    @FXML
    public void aplicarCursiva(ActionEvent event) {
        if (areaTextoPrincipal.getStyle().contains("italic")) {
            areaTextoPrincipal.setStyle(areaTextoPrincipal.getStyle().replace("-fx-font-style: italic;", ""));
        } else {
            areaTextoPrincipal.setStyle(areaTextoPrincipal.getStyle() + "-fx-font-style: italic;");
        }
    }

    // --- Métodos Undo y Redo (Ahora funcionarán correctamente) ---

    @FXML
    public void undoMetodo(ActionEvent event) {
        areaTextoPrincipal.undo();
    }

    @FXML
    public void redoMetodo(ActionEvent event) {
        areaTextoPrincipal.redo();
    }

    @FXML
    public void exportMethod(ActionEvent event) {
        File archivo = mostrarSelectorArchivos("Exportar a Markdown", true);

        if (archivo != null) {
            try (FileWriter writer = new FileWriter(archivo)) {
                // 1. Preparar y escribir contenido
                String contenidoMarkdown = generarTextoMarkdown();
                writer.write(contenidoMarkdown);

                // 2. Simular proceso
                ejecutarSimulacionProgreso();

                // 3. Notificar éxito
                mostrarAlerta("Éxito", "Archivo exportado correctamente.");
                resetearProgreso();

            } catch (IOException e) {
                System.out.println("Error al escribir el archivo");
                e.printStackTrace();
                mostrarAlerta("Error", "No se ha podido exportar el archivo.");
            }
        }
    }

    @FXML
    public void importMethod(ActionEvent event) {
        File archivo = mostrarSelectorArchivos("Abrir archivo", false);

        if (archivo == null)
            return;

        try {
            // 1. Simular proceso
            ejecutarSimulacionProgreso();

            // 2. Leer archivo
            String contenido = Files.readString(archivo.toPath());

            // 3. Procesar contenido y aplicar estilos en la UI
            aplicarContenidoImportado(contenido);

            // 4. Notificar éxito
            mostrarAlerta("Éxito", "Archivo importado correctamente.");
            resetearProgreso();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se ha podido importar el archivo.");
        }
    }

    // ---------------- METODOS AUXILIARES (HELPERS) ----------------

    private File mostrarSelectorArchivos(String titulo, boolean esGuardar) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(titulo);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo Markdown (*.md)", "*.md"));

        Stage stage = (Stage) areaTextoPrincipal.getScene().getWindow();

        if (esGuardar) {
            return fileChooser.showSaveDialog(stage);
        } else {
            return fileChooser.showOpenDialog(stage);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void ejecutarSimulacionProgreso() {
        componenteProgreso.setState(ProgressLabel.State.WORKING);
        componenteProgreso.setProgress(0.0);
        try {
            componenteProgreso.setProgress(0.1);
            componenteProgreso.setProgress(1.0);
            componenteProgreso.setState(ProgressLabel.State.FINISHED);
        } catch (Exception e) {
            componenteProgreso.setState(ProgressLabel.State.ERROR);
            componenteProgreso.setText("Error: " + e.getMessage());
        }
    }

    private void resetearProgreso() {
        componenteProgreso.setProgress(0.0);
        componenteProgreso.setState(ProgressLabel.State.IDLE);
    }

    private String generarTextoMarkdown() {
        String texto = areaTextoPrincipal.getText();
        String estiloActual = areaTextoPrincipal.getStyle();
        boolean esNegrita = estiloActual.contains("bold");
        boolean esCursiva = estiloActual.contains("italic");

        StringBuilder md = new StringBuilder();
        if (esNegrita)
            md.append("**");
        if (esCursiva)
            md.append("*");
        md.append(texto);
        if (esCursiva)
            md.append("*");
        if (esNegrita)
            md.append("**");

        return md.toString();
    }

    private void aplicarContenidoImportado(String contenido) {
        // Detectar estilos Markdown
        boolean negrita = contenido.startsWith("**") && contenido.endsWith("**");
        boolean cursiva = contenido.startsWith("*") && contenido.endsWith("*");

        // Limpiar marcas Markdown
        String textoLimpio = contenido
                .replace("**", "")
                .replace("*", "");

        areaTextoPrincipal.setText(textoLimpio);

        // Aplicar estilo al TextArea completo
        StringBuilder style = new StringBuilder();
        if (negrita)
            style.append("-fx-font-weight: bold;");
        if (cursiva)
            style.append("-fx-font-style: italic;");

        areaTextoPrincipal.setStyle(style.toString());
    }

    // Devolver el estilo original
    @FXML
    public void revertStyles(ActionEvent event) {
        areaTextoPrincipal.setStyle("");

    }

    @Override
    public void onCommand(NuiCommand command, String payload) {
        Platform.runLater(() -> {
            switch (command) {
                case NUEVO_DOCUMENTO:
                    limpiarTexto(null);
                    break;
                case ABRIR_DOCUMENTO:
                    importMethod(null);
                    break;
                case GUARDAR_DOCUMENTO:
                    exportMethod(null);
                    break;
                case NEGRITA:
                    aplicarNegrita(null);
                    break;
                case CURSIVA:
                    aplicarCursiva(null);
                    break;
                default:
                    System.out.println("Comando desconocido: " + command);
            }
        });
    }

}