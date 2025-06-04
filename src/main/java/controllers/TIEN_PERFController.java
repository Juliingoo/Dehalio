package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.*;
import org.hibernate.Query;
import org.hibernate.Session;
import utilities.LogAdministrador;
import utilities.Paths;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static controllers.NavegacionController.mostrarError;
import static controllers.NavegacionController.mostrarInformacion;
import static database.Sesion.newSession;
import static database.Sesion.usuario;
import static utilities.LogAdministrador.*;
import static utilities.LogAdministrador.escribirLogError;

public class TIEN_PERFController implements Initializable {

    @FXML
    private TextField barraBusqueda;

    @FXML
    private ComboBox<String> filtroComboBox;

    @FXML
    private ScrollPane scrollPaneProductos;

    @FXML
    private FlowPane productosContainer;

    @FXML
    private ImageView bannerTienda;

    @FXML
    private ImageView imagenProducto;

    @FXML
    private Text nombreProducto;

    @FXML
    private Text tipoProducto;

    @FXML
    private Text categoriaProducto;

    @FXML
    private Text nombreTienda;

    @FXML
    private Text direccionTienda;

    @FXML
    private ComboBox<String> valoracionComboBox;

    @FXML
    private Text mediaValoracionesText;

    @FXML
    private Button aceptarValoracionBtn;

    private double averageRating = 0.0;

    private final int limiteResultados = 1000;

    private comercio comercioActual;

    Session session = newSession();

    /**
     * Inicializa la pantalla de perfil de tienda, configurando los listeners de búsqueda y carga inicial de productos.
     *
     * @param location ubicación utilizada para resolver rutas relativas
     * @param resources recursos utilizados para la internacionalización
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LogAdministrador.escribirLogInfo("Inicializando pantalla de perfil de tienda.");

        filtroComboBox.setOnAction(event -> buscar());
        barraBusqueda.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                buscar();
            }
        });



        cargarProductos();
    }

    /**
     * Navega a la pantalla de inicio al pulsar el botón correspondiente.
     *
     * @param event el evento de acción que dispara la navegación
     */
    public void btnInicioAction(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton inicio pulsado");
        escribirLogInfo("Boton inicio pulsado");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            NavegacionController.navegar(stage, Paths.PRI_INI);
        } catch (IOException e) {
            System.out.println("Error al navegar a inicio: " + e.getMessage());
            escribirLogError("Error al navegar a inicio: " + e.getMessage());
        }
    }

    /**
     * Navega a la pantalla de listado de tiendas al pulsar el botón correspondiente.
     *
     * @param event el evento de acción que dispara la navegación
     */
    public void btnTiendasAction(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton tiendas pulsado");
        escribirLogInfo("Boton tiendas pulsado");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            NavegacionController.navegar(stage, Paths.PRI_TIEN);
        } catch (IOException e) {
            System.out.println("Error al navegar a tiendas: " + e.getMessage());
            escribirLogError("Error al navegar a tiendas: " + e.getMessage());
        }
    }

    /**
     * Navega a la pantalla de favoritos al pulsar el botón correspondiente.
     *
     * @param event el evento de acción que dispara la navegación
     */
    public void btnFavoritosAction(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton favoritos pulsado");
        escribirLogInfo("Boton favoritos pulsado");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            NavegacionController.navegar(stage, Paths.PRI_FAV);
        } catch (IOException e) {
            System.out.println("Error al navegar a favoritos: " + e.getMessage());
            escribirLogError("Error al navegar a favoritos: " + e.getMessage());
        }
    }

    /**
     * Navega a la pantalla de ajustes al pulsar el botón correspondiente.
     *
     * @param event el evento de acción que dispara la navegación
     */
    public void btnAjustesAction(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton ajustes pulsado");
        escribirLogInfo("Boton ajustes pulsado");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            NavegacionController.navegar(stage, Paths.PRI_AJU);
        } catch (IOException e) {
            System.out.println("Error al navegar a ajustes: " + e.getMessage());
            escribirLogError("Error al navegar a ajustes: " + e.getMessage());
        }
    }

    /**
     * Ejecuta la búsqueda de productos según el criterio introducido y el filtro seleccionado.
     *
     * @param event el evento de acción que dispara la búsqueda
     */
    @FXML
    public void btnBuscarAction(ActionEvent event) {
        buscar();
    }

    /**
     * Configura la vista con los datos del comercio recibido y carga sus productos.
     *
     * @param comercio comercio cuyos datos y productos se mostrarán
     */
    public void setComercio(comercio comercio) {
        this.comercioActual = comercio;

        // Configurar el nombre y la dirección de la tienda
        nombreTienda.setText(comercio.getNombre());
        direccionTienda.setText(comercio.getDireccion());

        // Configurar el banner de la tienda
        if (bannerTienda != null) {
            if (comercio.getImagen() != null && comercio.getImagen().length > 0) {
                bannerTienda.setImage(new Image(new ByteArrayInputStream(comercio.getImagen())));
            } else {
                bannerTienda.setImage(new Image(getClass().getResourceAsStream("/images/Fondo_carritos.png")));
            }
        } else {
            LogAdministrador.escribirLogError("El componente bannerTienda no está inicializado.");
        }

        // Cargar los productos de la tienda seleccionada
        cargarProductos();
    }

    /**
     * Carga y muestra todas las tarjetas de productos del comercio actual en la vista.
     * Muestra mensajes de error si ocurre algún problema al cargar los productos.
     */
    private void cargarProductos() {
        // Simulación de consulta de productos
        List<producto> listaProductos = obtenerProductosDeTienda();

        productosContainer.getChildren().clear();

        for (producto producto : listaProductos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/TarjetaProducto.fxml"));
                Node tarjetaProducto = loader.load();

                TarjetaProductoController controller = loader.getController();
                controller.setProducto(producto);

                tarjetaProducto.setOnMouseClicked(event -> mostrarProducto(producto));

                productosContainer.getChildren().add(tarjetaProducto);
            } catch (IOException e) {
                LogAdministrador.escribirLogError("Error al cargar la tarjeta del producto: " + e.getMessage());
                mostrarError("Error al cargar producto", "No se pudo cargar la tarjeta del producto.", e.getMessage());
            }
        }
    }

    /**
     * Filtra y ordena los productos del comercio según el texto de búsqueda y el filtro seleccionado.
     * Actualiza la vista con los productos resultantes.
     */
    private void buscar() {
        String filtroSeleccionado = filtroComboBox.getValue();
        String criterioBusqueda = barraBusqueda.getText();

        List<producto> listaProductos = obtenerProductosDeTienda();
        List<producto> productosFiltrados = new ArrayList<>();

        for (producto producto : listaProductos) {
            if (producto.getNombre().toLowerCase().contains(criterioBusqueda.toLowerCase())) {
                productosFiltrados.add(producto);
            }
        }

        if (filtroSeleccionado != null) {
            switch (filtroSeleccionado) {
                case "Precio Ascendente":
                    productosFiltrados.sort((p1, p2) -> Double.compare(p1.getPrecio(), p2.getPrecio()));
                    break;
                case "Precio Descendente":
                    productosFiltrados.sort((p1, p2) -> Double.compare(p2.getPrecio(), p1.getPrecio()));
                    break;
                case "Nombre":
                    productosFiltrados.sort((p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()));
                    break;
            }
        }

        cargarProductosFiltrados(productosFiltrados);
    }

    /**
     * Muestra en la vista las tarjetas de los productos filtrados recibidos.
     * Muestra mensajes de error si ocurre algún problema al cargar las tarjetas.
     *
     * @param productosFiltrados lista de productos a mostrar
     */
    private void cargarProductosFiltrados(List<producto> productosFiltrados) {
        productosContainer.getChildren().clear();

        for (producto producto : productosFiltrados) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/TarjetaProducto.fxml"));
                Node tarjetaProducto = loader.load();

                TarjetaProductoController controller = loader.getController();
                controller.setProducto(producto);

                tarjetaProducto.setOnMouseClicked(event -> mostrarProducto(producto));

                productosContainer.getChildren().add(tarjetaProducto);
            } catch (IOException e) {
                LogAdministrador.escribirLogError("Error al cargar la tarjeta del producto: " + e.getMessage());
                mostrarError("Error al cargar producto", "No se pudo cargar la tarjeta del producto.", e.getMessage());
            }
        }
    }

    /**
     * Muestra los detalles del producto seleccionado en la sección de información de producto.
     *
     * @param producto producto cuyos detalles se mostrarán
     */
    private void mostrarProducto(producto producto) {
        if (producto.getImagen() != null && producto.getImagen().length > 0) {
            imagenProducto.setImage(new Image(new ByteArrayInputStream(producto.getImagen())));
        } else {
            imagenProducto.setImage(new Image(getClass().getResourceAsStream("/iconos/productoSinImagen.png")));
        }
        nombreProducto.setText(producto.getNombre());
        tipoProducto.setText(producto.getTipo().getNombre());
        categoriaProducto.setText(producto.getTipo().getCategoria().getNombre());
    }

    /**
     * Recupera la lista de productos asociados al comercio actual desde la base de datos.
     *
     * @return lista de productos del comercio actual
     */
    private List<producto> obtenerProductosDeTienda() {
        if (comercioActual == null) {
            return new ArrayList<>();
        }

        return newSession().createQuery("FROM producto WHERE comercio.id = :idTienda", producto.class)
                .setParameter("idTienda", comercioActual.getIdComercio())
                .setMaxResults(limiteResultados)
                .getResultList();
    }

    /**
     * Calcula la media de valoraciones del comercio actual consultando la base de datos.
     *
     * @return media de valoraciones, o 0.0 si no hay valoraciones
     */
    private double calcularMediaValoraciones(){

        Query<Double> qPromedio = session.createQuery(
                        "SELECT AVG(numeroValoracion) FROM valoracion WHERE comercio = :idComercio",
                        Double.class).setParameter("idComercio", comercioActual);
        List<Double> listaPromedio = qPromedio.getResultList();

        double promedio = (listaPromedio != null && !listaPromedio.isEmpty() && listaPromedio.get(0) != null)
                ? listaPromedio.get(0)
                : 0.0;

        return promedio;
    }

    /**
     * Registra o actualiza la valoración del usuario para el comercio actual.
     * Muestra mensajes informativos o de error según el resultado.
     */
    @FXML
    private void confirmarValoracion() {
        String seleccion = valoracionComboBox.getValue();
        if (seleccion != null) {
            int puntuacion = seleccion.length();

            try {
                Query<valoracion> qValoracion = session.createQuery("FROM valoracion WHERE usuario = :idUsuario AND comercio = :idComercio");
                qValoracion.setParameter("idUsuario", usuario);
                qValoracion.setParameter("idComercio", comercioActual);

                List<valoracion> valoracionUsuarioLista = qValoracion.getResultList();


                session.beginTransaction();

                if (!valoracionUsuarioLista.isEmpty()) {
                    // Update existing rating
                    valoracion valoracionExistente = valoracionUsuarioLista.getFirst();
                    valoracionExistente.setNumeroValoracion(puntuacion);
                    session.update(valoracionExistente);
                    mostrarInformacion("Valoración actualizada", "Tu valoración ha sido actualizada.", "Tenías una valoración existente y se ha actualizado");
                } else {
                    // Insert new rating
                    model.valoracion nuevaValoracion = new model.valoracion();
                    nuevaValoracion.setUsuario(usuario);
                    nuevaValoracion.setComercio(comercioActual);
                    nuevaValoracion.setNumeroValoracion(puntuacion);
                    session.save(nuevaValoracion);
                    mostrarInformacion("Valoración registrada", "Tu valoración ha sido registrada.", "");
                }

                session.getTransaction().commit();

                // Recalcular media valoraciones
                mediaValoracionesText.setText("Media valoraciones: " + calcularMediaValoraciones());

            } catch (Exception e) {
                session.getTransaction().rollback();
                mostrarError("Error al valorar", "No se pudo registrar la valoración.", e.getMessage());
            }
        } else {
            mostrarError("Valoración no seleccionada", "Por favor, selecciona una valoración antes de confirmar.", "");
        }
    }



}