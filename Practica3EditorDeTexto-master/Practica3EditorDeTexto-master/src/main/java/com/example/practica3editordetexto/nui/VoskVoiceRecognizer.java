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

    public VoskVoiceRecognizer(NuiListener listener) {
        this.listener = listener;
    }

    public void startListening() {
        // Ejecutamos en un hilo nuevo para no bloquear JavaFX
        new Thread(() -> {
            try {
                // 1. Cargar el modelo (Asegúrate que la ruta sea correcta)
                // LibVosk.setLogLevel(LogLevel.INFO); // Descomentar para ver logs de Vosk
                Model model = new Model("model-es");

                // 2. Crear el reconocedor
                Recognizer recognizer = new Recognizer(model, 16000); // 16khz es estándar

                // 3. Configurar el micrófono
                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);

                microphone.open(format);
                microphone.start();

                System.out.println("NUI: Escuchando comandos de voz...");
                running = true;

                // 4. Bucle de lectura
                byte[] buffer = new byte[4096];
                while (running) {
                    int nbytes = microphone.read(buffer, 0, buffer.length);

                    if (nbytes > 0) {
                        // Si Vosk detecta una frase completa (Result)
                        if (recognizer.acceptWaveForm(buffer, nbytes)) {
                            String jsonResult = recognizer.getResult();
                            procesarTexto(jsonResult);
                        } else {
                            recognizer.getPartialResult();
                        }

                    }
                }
                microphone.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        this.running = false;
    }

    // Método simple para extraer el texto del JSON y mapear a comandos
    private void procesarTexto(String json) {
        // El JSON viene así: { "text" : "abrir documento" }
        // Haremos una limpieza básica
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

        // Si encontramos un comando válido, avisamos al Listener (al Controller)
        if (comandoDetectado != NuiCommand.DESCONOCIDO) {
            System.out.println("Comando voz detectado: " + comandoDetectado);
            listener.onCommand(comandoDetectado, null);
        }
    }
}
