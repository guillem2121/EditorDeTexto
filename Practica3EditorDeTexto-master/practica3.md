# Práctica 3: Editor de Texto enriquecido con JavaFX

Este proyecto consiste en una aplicación de escritorio desarrollada en Java y JavaFX que funciona como un editor de texto con capacidades de formateo, manipulación de cadenas y gestión de archivos.

## 📋 Descripción General
La aplicación permite al usuario escribir texto, ver estadísticas en tiempo real, aplicar transformaciones rápidas y gestionar archivos en formato Markdown (.md). Se ha puesto énfasis en la limpieza del código y la separación de responsabilidades dentro del controlador.

##  Funcionalidades Principales

### 1. Edición y Estadísticas
- **Contadores en tiempo real:** Muestra el número de caracteres y palabras dinámicamente mediante *Listeners* en el `textProperty`.
- **Deshacer/Rehacer:** Implementación nativa de las funciones *Undo/Redo* de JavaFX.

### 2. Manipulación de Texto
- **Conversión:** A mayúsculas y minúsculas.
- **Inversión:** Función para invertir el texto completo (efecto espejo).
- **Limpieza:** Eliminación de espacios sobrantes (`trim` y espacios dobles) y borrado total.

### 3. Estilos y Formato
- Aplicación de **Negrita** y **Cursiva** inyectando estilos CSS (`-fx-font-weight`, `-fx-font-style`) directamente sobre el componente.
- Botón para **revertir estilos** y volver al formato plano.

### 4. Gestión de Archivos (I/O)
- **Exportar a Markdown:** Guarda el contenido añadiendo la sintaxis básica de Markdown (`**` para negrita, `*` para cursiva) si el estilo está activo.
- **Importar de Markdown:** Lee archivos `.md`, limpia los caracteres de formato para la vista y aplica el estilo visualmente al TextArea.
- **Feedback de Usuario:** Uso de un componente personalizado (`ProgressLabel`) para simular visualmente la carga y guardado de archivos, junto con alertas informativas de éxito o error.

## 🛠 Detalles de Implementación y Refactorización
Para mantener un código limpio (*Clean Code*), se ha refactorizado la lógica del controlador `HelloController`:

### Métodos Helper
- **mostrarSelectorArchivos:** Centraliza la lógica del `FileChooser`.
- **mostrarAlerta:** Unifica la creación de diálogos informativos.
- **ejecutarSimulacionProgreso:** Encapsula la lógica del componente de progreso.
- **generarTextoMarkdown** y **aplicarContenidoImportado:** Separan la lógica de parseo de texto de la lógica de entrada/salida.

##  Requisitos
- **Java JDK** (versión compatible con tu proyecto, ej. 17+).
- **Librerías JavaFX** configuradas correctamente.
