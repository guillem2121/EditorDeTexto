package com.example.practica3editordetexto.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

public class ProgressLabel extends VBox {
    public enum State { IDLE, WORKING, FINISHED, ERROR }
    private final ProgressBar progressBar;
    private final Label label;
    private State state;
    public ProgressLabel() {
        this.progressBar = new ProgressBar(0.0);
        this.label = new Label("Listo");
        this.state = State.IDLE;
        this.setSpacing(4);
        this.progressBar.setPrefWidth(200);
        this.progressBar.setMaxWidth(Double.MAX_VALUE);
        this.getChildren().addAll(progressBar, label);
    }
    public void setProgress(double value) { progressBar.setProgress(value); }
    public double getProgress() { return progressBar.getProgress(); }
    public DoubleProperty progressProperty() { return
            progressBar.progressProperty(); }
    public void setText(String text) { label.setText(text); }
    public String getText() { return label.getText(); }
    public StringProperty textProperty() { return label.textProperty(); }
    public void setState(State newState) {
        this.state = newState;
        switch (newState) {
            case IDLE -> {
                setText("Esperando...");
                setStyle("-fx-background-color: transparent;");
            }

            case WORKING -> {
                setText("Procesando...");
                setStyle("-fx-background-color: #e0f7fa;");
            }
            case FINISHED -> {
                setText("Completado con éxito");
                setStyle("-fx-border-color: green; -fx-border-width: 1px;");
            }
            case ERROR -> {
                setText("Error en la operación");
                setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
        }
    }
    public State getState() { return state; }
}

