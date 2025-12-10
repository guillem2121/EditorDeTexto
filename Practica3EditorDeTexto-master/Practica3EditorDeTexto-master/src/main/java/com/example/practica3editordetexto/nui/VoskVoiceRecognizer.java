package com.example.practica3editordetexto.nui;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.io.IOException;

public class VoskVoiceRecognizer {

    private NuiListener listener;
    private boolean running = false;
    private Recognizer recognizer; // Added member variable

    public VoskVoiceRecognizer(NuiListener listener) {
        this.listener = listener;
        LibVosk.setLogLevel(LogLevel.WARNINGS); // Set log level
    }

    public void startListening() {
        new Thread(() -> {
            try {
                Model model = new Model("model-es");
                // 2. Crear el reconocedor
                recognizer = new Recognizer(model, 16000); // Initialized recognizer

                // 3. Configurar el micrófono
                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);
                microphone.start();

                running = true;

                // 4. Bucle de lectura
                byte[] buffer = new byte[4096];
                while (running) {
                    int nbytes = microphone.read(buffer, 0, buffer.length);

                    if (nbytes > 0) {
                        if (recognizer.acceptWaveForm(buffer, nbytes)) {
                            String jsonResult = recognizer.getResult();
                            procesarTexto(jsonResult);
                        } else {
                            // recognizer.getPartialResult(); // Optional
                        }
                    }
                }
                microphone.close();
                recognizer.close(); // Clean up
                model.close(); // Clean up

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        this.running = false;
    }

    private void procesarTexto(String json) {
        // El JSON viene así: { "text" : "abrir documento" }
        String texto = json.toLowerCase();

        NuiCommand comandoDetectado = NuiCommand.DESCONOCIDO;

        if (texto.contains("nuevo")) {
            comandoDetectado = NuiCommand.NUEVO_DOCUMENTO;
        } else if (texto.contains("abrir")) {
            comandoDetectado = NuiCommand.ABRIR_DOCUMENTO;
        } else if (texto.contains("guardar")) {
            comandoDetectado = NuiCommand.GUARDAR_DOCUMENTO;
        } else if (texto.contains("negrita")) {
            comandoDetectado = NuiCommand.NEGRITA;
        } else if (texto.contains("cursiva")) {
            comandoDetectado = NuiCommand.CURSIVA;
        }

        if (comandoDetectado != NuiCommand.DESCONOCIDO) {
            listener.onCommand(comandoDetectado, null);
        }
    }
}