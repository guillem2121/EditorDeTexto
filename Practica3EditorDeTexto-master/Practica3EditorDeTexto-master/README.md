# 📝 Editor de Texto con Comandos por Voz

Un editor de texto moderno desarrollado en JavaFX que integra reconocimiento de voz mediante **Vosk** para controlar todas las funcionalidades de la aplicación mediante comandos hablados en español.

## 🎯 Características Principales

### Funcionalidades del Editor
- ✍️ Edición de texto con área de trabajo principal
- 📊 Contador de caracteres y palabras en tiempo real
- 💾 Importar y exportar archivos en formato Markdown (.md)
- 🎨 Aplicar estilos de texto (negrita, cursiva)
- 🔄 Deshacer y rehacer cambios
- 📈 Barra de progreso para operaciones de importación/exportación

### Transformaciones de Texto
- 🔠 Convertir a mayúsculas
- 🔡 Convertir a minúsculas
- 🔄 Invertir texto
- 🧹 Limpiar texto completo
- ✂️ Eliminar espacios extras
- 🎨 Revertir estilos aplicados

### 🎤 **Control por Voz (NUEVO)**
El editor ahora incluye reconocimiento de voz en español que permite ejecutar todas las funciones mediante comandos hablados.

## 🗣️ Comandos por Voz Disponibles

| Comando de Voz | Acción | Descripción |
|----------------|--------|-------------|
| **"nuevo"** | Nuevo Documento | Limpia el área de texto para comenzar un nuevo documento |
| **"abrir"** | Abrir Documento | Abre el diálogo para importar un archivo |
| **"guardar"** | Guardar Documento | Abre el diálogo para exportar el documento |
| **"negrita"** | Aplicar/Quitar Negrita | Alterna el estilo de negrita en el texto |
| **"cursiva"** | Aplicar/Quitar Cursiva | Alterna el estilo de cursiva en el texto |
| **"mayúsculas"** | Convertir a Mayúsculas | Convierte todo el texto a mayúsculas |
| **"minúsculas"** | Convertir a Minúsculas | Convierte todo el texto a minúsculas |
| **"invertir"** | Invertir Texto | Invierte el orden de los caracteres |
| **"limpiar"** / **"borrar"** | Limpiar Texto | Borra todo el contenido del editor |
| **"eliminar espacios"** / **"quitar espacios"** | Eliminar Espacios | Elimina espacios duplicados |
| **"revertir"** / **"restaurar estilos"** | Revertir Estilos | Elimina todos los estilos aplicados |
| **"deshacer"** / **"undo"** | Deshacer | Deshace la última acción |
| **"rehacer"** / **"redo"** | Rehacer | Rehace la última acción deshecha |

## 🏗️ Arquitectura del Sistema de Voz

### Componentes Principales

#### 1. **VoskVoiceRecognizer** (`nui/VoskVoiceRecognizer.java`)
Motor de reconocimiento de voz que utiliza la biblioteca Vosk para procesar audio del micrófono.

**Características:**
- Reconocimiento continuo en tiempo real
- Modelo de lenguaje en español (`model-es`)
- Procesamiento en hilo separado para no bloquear la UI
- Frecuencia de muestreo: 16 kHz

**Funcionamiento:**
```java
// Inicialización del reconocedor
Model model = new Model("model-es");
Recognizer recognizer = new Recognizer(model, 16000);

// Captura de audio del micrófono
AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);

// Procesamiento continuo
while (running) {
    int nbytes = microphone.read(buffer, 0, buffer.length);
    if (recognizer.acceptWaveForm(buffer, nbytes)) {
        String jsonResult = recognizer.getResult();
        procesarTexto(jsonResult);
    }
}
```

#### 2. **NuiCommand** (`nui/NuiCommand.java`)
Enumeración que define todos los comandos disponibles en el sistema.

```java
public enum NuiCommand {
    NUEVO_DOCUMENTO,
    ABRIR_DOCUMENTO,
    GUARDAR_DOCUMENTO,
    NEGRITA,
    CURSIVA,
    MAYUSCULAS,
    MINUSCULAS,
    INVERTIR_TEXTO,
    LIMPIAR,
    ELIMINAR_ESPACIOS,
    REVERTIR_ESTILOS,
    DESHACER,
    REHACER,
    DESCONOCIDO
}
```

#### 3. **NuiListener** (`nui/NuiListener.java`)
Interfaz que permite la comunicación entre el reconocedor de voz y el controlador.

```java
public interface NuiListener {
    void onCommand(NuiCommand command, String payload);
}
```

#### 4. **HelloController** (`editor/controller/HelloController.java`)
Controlador principal que implementa `NuiListener` y ejecuta las acciones correspondientes.

**Integración:**
```java
// Implementación de la interfaz
public class HelloController implements NuiListener {
    
    private VoskVoiceRecognizer vozRecorder;
    
    @FXML
    public void initialize() {
        // Inicializar reconocimiento de voz
        vozRecorder = new VoskVoiceRecognizer(this);
        vozRecorder.startListening();
    }
    
    @Override
    public void onCommand(NuiCommand command, String payload) {
        Platform.runLater(() -> {
            switch (command) {
                case MAYUSCULAS:
                    convertirMayusculas(null);
                    break;
                case NEGRITA:
                    aplicarNegrita(null);
                    break;
                // ... más casos
            }
        });
    }
}
```

## 🛠️ Tecnologías Utilizadas

- **JavaFX 21.0.2** - Framework de interfaz gráfica
- **Vosk 0.3.38** - Motor de reconocimiento de voz offline
- **JNA 5.13.0** - Acceso a funciones nativas del sistema
- **Maven** - Gestión de dependencias y construcción del proyecto
- **Java 21** - Lenguaje de programación (LTS)

## 📋 Requisitos Previos

- **Java Development Kit (JDK) 21** o superior
- **Maven 3.6+**
- **Modelo de lenguaje Vosk en español** (`model-es`)
- Micrófono funcional

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/guillem2121/EditorDeTexto.git
cd EditorDeTexto
```

### 2. Descargar el modelo de voz en español
Descarga el modelo desde [Vosk Models](https://alphacephei.com/vosk/models) y extráelo en la raíz del proyecto:

```bash
# Descargar modelo pequeño en español (recomendado)
wget https://alphacephei.com/vosk/models/vosk-model-small-es-0.42.zip
unzip vosk-model-small-es-0.42.zip
mv vosk-model-small-es-0.42 model-es
```

La estructura debe quedar así:
```
Practica3EditorDeTexto/
├── model-es/           # Modelo de Vosk
│   ├── am/
│   ├── conf/
│   └── graph/
├── src/
├── pom.xml
└── README.md
```

### 3. Compilar el proyecto
```bash
mvn clean compile
```

### 4. Ejecutar la aplicación
```bash
mvn javafx:run
```

## 📦 Dependencias del Proyecto

```xml
<!-- Vosk: Motor de reconocimiento de voz -->
<dependency>
    <groupId>com.alphacephei</groupId>
    <artifactId>vosk</artifactId>
    <version>0.3.38</version>
</dependency>

<!-- JNA: Acceso a funciones nativas -->
<dependency>
    <groupId>net.java.dev.jna</groupId>
    <artifactId>jna</artifactId>
    <version>5.13.0</version>
</dependency>

<!-- JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.2</version>
</dependency>
```

## 🎮 Uso de la Aplicación

### Modo Manual (Interfaz Gráfica)
1. Escribe o pega texto en el área principal
2. Usa los botones de la barra de herramientas para aplicar transformaciones
3. Guarda tu trabajo usando el botón "Exportar"

### Modo por Voz
1. Asegúrate de que tu micrófono esté activo
2. Habla claramente en español
3. Di uno de los comandos disponibles (ej: "mayúsculas", "negrita", "guardar")
4. La aplicación ejecutará la acción automáticamente

**Ejemplo de uso:**
```
Usuario: "mayúsculas"
→ El texto se convierte a mayúsculas

Usuario: "negrita"
→ Se aplica formato de negrita

Usuario: "guardar"
→ Se abre el diálogo para guardar el archivo
```

## 🔧 Solución de Problemas

### El reconocimiento de voz no funciona
- Verifica que el directorio `model-es` existe en la raíz del proyecto
- Comprueba que tu micrófono está conectado y configurado como dispositivo predeterminado
- Revisa la consola para ver mensajes de error de Vosk

### Los comandos no se reconocen
- Habla claramente y a un volumen moderado
- Asegúrate de usar las palabras clave exactas (ver tabla de comandos)
- Verifica que el modelo de lenguaje es el correcto para español

### Error al compilar
```bash
# Limpiar y recompilar
mvn clean install
```

## 📝 Estructura del Proyecto

```
src/main/java/com/example/practica3editordetexto/
├── editor/
│   ├── HelloApplication.java          # Clase principal de la aplicación
│   └── controller/
│       └── HelloController.java       # Controlador con lógica de negocio
├── nui/                               # Paquete de reconocimiento de voz
│   ├── VoskVoiceRecognizer.java      # Motor de reconocimiento
│   ├── NuiCommand.java               # Enumeración de comandos
│   └── NuiListener.java              # Interfaz de escucha
└── view/
    └── ProgressLabel.java            # Componente de progreso personalizado
```

## 🆕 Cambios Recientes (v2.0)

### ✨ Nuevas Características
- ✅ Sistema completo de reconocimiento de voz con Vosk
- ✅ 13 comandos de voz en español
- ✅ Arquitectura NUI (Natural User Interface) modular
- ✅ Procesamiento de audio en tiempo real
- ✅ Integración transparente con funcionalidades existentes

### 🔄 Mejoras
- Refactorización del controlador para soportar comandos por voz
- Uso de `Platform.runLater()` para operaciones thread-safe
- Mejor manejo de errores en reconocimiento de voz

## 👥 Contribuciones

Las contribuciones son bienvenidas. Para cambios importantes:
1. Haz fork del repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

## 🙏 Agradecimientos

- [Vosk Speech Recognition Toolkit](https://alphacephei.com/vosk/) - Por el excelente motor de reconocimiento de voz offline
- [OpenJFX](https://openjfx.io/) - Por el framework JavaFX
- Comunidad de desarrolladores Java

## 📧 Contacto

Para preguntas o sugerencias, abre un issue en el repositorio.

---

**Desarrollado con ❤️ usando JavaFX y Vosk**
