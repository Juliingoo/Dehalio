# Dehalio. Aplicación de escritorio de comparación de precios de productos por ubicación.
### Trabajo de fin de grado.
![Logo_Dehalio_wide](https://github.com/user-attachments/assets/de0176e1-686d-495c-bdd9-5867ebdcded4)
📍 Busca en tu zona productos al mejor precio.

# Dehalio V1.0

## 🧩 Requisitos previos

Antes de ejecutar el programa, asegúrate de que el entorno cumple con los siguientes requisitos:

- El programa **no funcionará** si no encuentra una base de datos, ya que su primera gestión es conectarse con ella.
- Se requiere una instancia de **MySQL local**. La base de datos no está hosteada en la nube.
- Es necesario tener **MySQL instalado y corriendo** en el equipo donde se pruebe.
- El usuario de MySQL debe tener las siguientes credenciales:
  - **Usuario:** `root`
  - **Contraseña:** `root`
- Dentro del archivo `.zip` del proyecto, en la carpeta `BBDD/`, se incluye un script SQL para generar la base de datos con datos de ejemplo.

---

## 🖥️ Ejecución del archivo `Dehalio_V1.0.exe`

1. Verifica que el servicio de MySQL esté activo y configurado según los requisitos mencionados.
2. Ejecuta el archivo `Dehalio_V1.0.exe`.

> ⚠️ **Nota:** Si no hay una base de datos disponible o conectada, el programa no se abrirá.

---

## 🧑‍💻 Ejecución desde IntelliJ IDEA

1. Asegúrate de tener MySQL en ejecución y de haber creado la base de datos de ejemplo con el script incluido en `BBDD/`.
2. Abre el proyecto en IntelliJ IDEA.
3. Ejecuta la clase principal:


4. Configura las VM Options en la configuración de ejecución con los siguientes valores:

```bash
--module-path "javafx-sdk-24\lib"
--add-modules javafx.swing,javafx.graphics,javafx.fxml,javafx.media,javafx.web
--add-reads javafx.graphics=ALL-UNNAMED
--add-opens javafx.controls/com.sun.javafx.charts=ALL-UNNAMED
--add-opens javafx.graphics/com.sun.javafx.iio=ALL-UNNAMED
--add-opens javafx.graphics/com.sun.javafx.iio.common=ALL-UNNAMED
--add-opens javafx.graphics/com.sun.javafx.css=ALL-UNNAMED
--add-opens javafx.base/com.sun.javafx.runtime=ALL-UNNAMED

```
5. Argumentos para ejecutar el programa

```bash
-Dprism.order=sw
```
