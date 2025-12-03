package com.example.practica3editordetexto.nui;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;

public class VoskVoiceRecognizer {

    private NuiListener listener;
    private boolean running = false;

    public VoskVoiceRecognizer(NuiListener listener) {
        this.listener = listener;
    }

    public void startListening() {
        // Ejecutamos en un hilo nuevo para no bloquear la interfaz JavaFX
        new Thread(() -> {
            try {
                // 1. ACTIVAR LOGS DE VOSK (IMPORTANTE PARA DEPURAR)
                // Esto hará que Vosk imprima en consola si el modelo carga o falla.
                LibVosk.setLogLevel(LogLevel.INFO);

                System.out.println("DEBUG: Cargando modelo desde la carpeta 'model-es'...");
                // Asegúrate de que la carpeta "model-es" está en la raíz de tu proyecto
                Model model = new Model("model-es");
                System.out.println("DEBUG: Modelo cargado correctamente.");

                // 2. Crear el reconocedor
                Recognizer recognizer = new Recognizer(model, 16000); // 16khz

                // 3. Configurar el micrófono
                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

                // Verificación de seguridad: ¿El PC soporta este micro?
                if (!AudioSystem.isLineSupported(info)) {
                    System.err.println("ERROR: El micrófono no soporta el formato 16kHz mono.");
                    return;
                }

                TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);
                microphone.start();

                System.out.println("NUI: Escuchando comandos de voz... ¡HABLA AHORA!");
                running = true;

                // 4. Bucle de lectura
                byte[] buffer = new byte[4096];
                while (running) {
                    int nbytes = microphone.read(buffer, 0, buffer.length);

                    if (nbytes > 0) {
                        // Si Vosk detecta una frase completa
                        if (recognizer.acceptWaveForm(buffer, nbytes)) {
                            String jsonResult = recognizer.getResult();

                            // IMPRIMIR LO QUE ESCUCHA (Modo Cotilla)
                            // Así sabrás si te entiende o no
                            System.out.println("VOSK ESCUCHÓ: " + jsonResult);

                            procesarTexto(jsonResult);
                        } else {
                            // Resultados parciales (opcional, suele llenar mucho la consola)
                            // System.out.println("Parcial: " + recognizer.getPartialResult());
                        }
                    }
                }
                microphone.close();
                System.out.println("NUI: Micrófono cerrado.");

            } catch (Exception e) {
                System.err.println("ERROR CRÍTICO EN VOSK:");
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        this.running = false;
    }

    // Método para extraer el texto del JSON y mapear a comandos
    private void procesarTexto(String json) {
        // El JSON viene así: { "text" : "abrir documento" }
        String texto = json.toLowerCase();

        NuiCommand comandoDetectado = NuiCommand.DESCONOCIDO;

        // Búsqueda de palabras clave
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

        // Si encontramos un comando válido, avisamos al Listener (el Controller)
        if (comandoDetectado != NuiCommand.DESCONOCIDO) {
            System.out.println(">>> COMANDO VALIDADO: " + comandoDetectado);
            listener.onCommand(comandoDetectado, null);
        }
    }
}