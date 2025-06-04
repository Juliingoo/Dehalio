package controllers;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import database.Sesion;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import model.comercio;
import model.producto;
import model.productoFavorito;

import org.hibernate.Session;
import org.hibernate.Query;

import utilities.Localizacion;
import utilities.LogAdministrador;
import utilities.Paths;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static controllers.NavegacionController.mostrarError;
import static database.Sesion.newSession;
import static utilities.Localizacion.calcularDistancia;
import static utilities.LogAdministrador.*;

public class PRI_INIController implements Initializable{

    Session session = newSession();

    @FXML
    private TextField barraBusqueda;

    @FXML
    private ComboBox<String> filtroComboBox;

    @FXML
    private ScrollPane scrollPaneProductos;

    @FXML
    private FlowPane productosContainer;

    @FXML
    private Slider kmSlider;

    @FXML
    private VBox mapVBox;

    private final MapView mapView = new MapView();

    private final MapLayer marcadorProductoLayer = new MapLayer();

    private final int limiteResultados = 1000;

    double[] coordsUsuario = Localizacion.obtenerCoordenadasUsuario();
    double latUsuario = coordsUsuario[0];
    double lonUsuario = coordsUsuario[1];

    /**
     * Inicializa la pantalla principal configurando los controles de búsqueda y filtros,
     * y cargando los productos y el mapa centrado en la ubicación del usuario.
     *
     * @param location ubicación utilizada para resolver rutas relativas
     * @param resources recursos utilizados para la internacionalización
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(inicioInfoLogConsola() + "Inicializando pantalla principal. Recuperando productos");
        escribirLogInfo("Inicializando pantalla principal. Recuperando productos.");

        filtroComboBox.setOnAction(event -> buscar());
        kmSlider.setOnMouseClicked(event -> buscar());
        barraBusqueda.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                buscar();
            }
        });

        Query<producto> qProducto = session.createQuery("from producto");
        qProducto.setMaxResults(limiteResultados);
        List<producto> listaProducto = qProducto.getResultList();

        cargarProductos(getProductosFiltradosCercania(listaProducto));
        cargarMapa();
    }

    /**
     * Navega a la pantalla de inicio al pulsar el botón correspondiente.
     * Muestra un error si ocurre un problema durante la navegación.
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
     * Navega a la pantalla de tiendas al pulsar el botón correspondiente.
     * Muestra un error si ocurre un problema durante la navegación.
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
     * Muestra un error si ocurre un problema durante la navegación.
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
     * Muestra un error si ocurre un problema durante la navegación.
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
     * Ejecuta la búsqueda de productos según el criterio y filtro seleccionados.
     *
     * @param event el evento de acción que dispara la búsqueda
     */
    @FXML
    public void btnBuscarAction(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton buscar pulsado");
        escribirLogInfo("Boton buscar pulsado");
        buscar();
    }

    /**
     * Filtra la lista de productos recibida mostrando solo aquellos cuyo comercio se encuentra
     * dentro del radio de distancia seleccionado por el usuario.
     *
     * @param listaProductosSinFiltrar lista de productos a filtrar por cercanía
     * @return lista de productos filtrados por distancia
     */
    public List<producto> getProductosFiltradosCercania(List<producto> listaProductosSinFiltrar){
        escribirLogInfo("Filtrando productos por cercanía");
        List<producto> productosFiltrados = new ArrayList<>();
        for (producto p : listaProductosSinFiltrar) {
            comercio c = p.getComercio();
            if (c != null && c.getCoordenadas() != null) {
                try {
                    String[] partes = c.getCoordenadas().split(",");
                    double latComercio = Double.parseDouble(partes[0].trim());
                    double lonComercio = Double.parseDouble(partes[1].trim());

                    double distancia = calcularDistancia(latUsuario, lonUsuario, latComercio, lonComercio);
                    LogAdministrador.escribirLogInfo("Distancia establecida: " + kmSlider.getValue());
                    if (distancia <= kmSlider.getValue()) {
                        productosFiltrados.add(p);
                    }
                } catch (Exception e) {
                    System.out.println("Error al leer coordenadas del comercio: " + e.getMessage());
                    escribirLogError("Error al leer coordenadas del comercio: " + e.getMessage());
                }
            }
        }
        return productosFiltrados;
    }

    /**
     * Realiza la búsqueda de productos aplicando el criterio de texto, el filtro seleccionado
     * y la cercanía geográfica, y actualiza la lista mostrada.
     */
    private void buscar() {
        String filtroSeleccionado = filtroComboBox.getValue();

        String criterioBusqueda = barraBusqueda.getText();

        Query<producto> qProducto = session.createQuery("FROM producto WHERE nombre LIKE :criterio", producto.class);
        qProducto.setMaxResults(limiteResultados);
        qProducto.setParameter("criterio", "%" + criterioBusqueda + "%");

        List<producto> listaProducto = qProducto.getResultList();

        if (filtroSeleccionado == null || filtroSeleccionado.isEmpty()) {
            // Si no hay filtro seleccionado, aplicar solo el filtro de cercanía
            cargarProductos(getProductosFiltradosCercania(listaProducto));
            return;
        }

        switch (filtroSeleccionado) {
            case "Precio Ascendente":
                listaProducto.sort((p1, p2) -> Double.compare(p1.getPrecio(), p2.getPrecio()));
                break;
            case "Precio Descendente":
                listaProducto.sort((p1, p2) -> Double.compare(p2.getPrecio(), p1.getPrecio()));
                break;
            case "Nombre":
                listaProducto.sort((p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()));
                break;
            default:
                cargarProductos(getProductosFiltradosCercania(listaProducto));
                break;
        }
        cargarProductos(getProductosFiltradosCercania(listaProducto));
    }

    /**
     * Carga y muestra las tarjetas de los productos recibidos en la interfaz.
     * Al hacer clic en una tarjeta, centra el mapa en la ubicación del producto y muestra su información.
     *
     * @param listaProductos lista de productos a mostrar
     */
    private void cargarProductos(List<producto> listaProductos) {
        escribirLogInfo("Cargando lista de productos");
        productosContainer.getChildren().clear();

        for (producto producto : listaProductos) {
            // Crear contenedor principal tipo VBox (tarjeta vertical)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TarjetaProducto.fxml"));
            VBox tarjetaProducto = null;
            try {
                tarjetaProducto = loader.load();
            } catch (IOException e) {
                System.out.println(inicioErrorLogConsola() + "Error al cargar la tarjeta de comercio: " + e.getMessage());
                escribirLogError("Error al cargar la tarjeta de comercio: " + e.getMessage());
            }

            // Obtener el controlador de la tarjeta y configurar los datos
            TarjetaProductoController controller = loader.getController();
            controller.setProducto(producto);

            // Evento de clic para mostrar las coordenadas en el mapa
            tarjetaProducto.setOnMouseClicked(event -> {
                String coordenadasProducto = producto.getComercio().getCoordenadas();
                if (!coordenadasProducto.isEmpty() || !coordenadasProducto.isBlank()) {
                    String coordenasProductoSeparadas[] = coordenadasProducto.split(",");
                    double latitud = Double.valueOf(coordenasProductoSeparadas[0]);
                    double longitud = Double.valueOf(coordenasProductoSeparadas[1]);

                    MapPoint puntoProducto = new MapPoint(latitud, longitud);
                    mapView.flyTo(0, puntoProducto, 0.01); // Centrar el mapa en las coordenadas del producto
                    mostrarUbicacion(coordenadasProducto, producto);
                } else {
                    mostrarError("Coordenadas no disponibles", "El producto no tiene coordenadas asignadas.",
                            "No se encuentran las coordenadas del producto");
                    escribirLogError("El producto no tiene coordenadas asignadas " +
                            " Producto: " + producto.getIdProducto() + " | en Tienda:" + producto.getComercio());
                }
            });

            // Añadir tarjeta al contenedor principal
            productosContainer.getChildren().add(tarjetaProducto);
        }
    }

    /**
     * Inicializa y configura el mapa, centrando la vista en la ubicación del usuario.
     * Añade el mapa a la interfaz y ajusta su tamaño y estilo.
     */
    private void cargarMapa() {
        escribirLogInfo("Cargando mapa");
        System.out.println(inicioInfoLogConsola() + "Cargando mapa");

        MapPoint mapPoint = new MapPoint(latUsuario, lonUsuario);

        // El MapView se ajusta al tamaño del VBox
        mapView.prefWidthProperty().bind(mapVBox.widthProperty());
        mapView.prefHeightProperty().bind(mapVBox.heightProperty());
        VBox.setVgrow(mapView, Priority.ALWAYS);

        // Clip para esquinas redondeadas
        Rectangle clip = new Rectangle();
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        clip.widthProperty().bind(mapView.widthProperty());
        clip.heightProperty().bind(mapView.heightProperty());
        mapView.setClip(clip);

        mapView.flyTo(0, mapPoint, 0.1);
        mapView.setZoom(15);

        // Añadir capa de marcador al mapa
        mapView.addLayer(marcadorProductoLayer);

        mapVBox.getChildren().add(mapView);
    }

    /**
     * Muestra la ubicación del producto seleccionado en el mapa y en la interfaz,
     * permitiendo abrir la dirección en Google Maps.
     *
     * @param coordenadas coordenadas del producto en formato "latitud,longitud"
     * @param producto producto cuya ubicación se va a mostrar
     */
    private void mostrarUbicacion(String coordenadas, producto producto) {
        escribirLogInfo("Obteniendo coordenadas del producto: " + producto.getIdProducto());
        System.out.println(inicioInfoLogConsola() + "Obteniendo coordenadas del producto: " + producto.getIdProducto());

        // Si ya existe el HBox de ubicación, lo quitamos para evitar duplicados
        mapVBox.getChildren().removeIf(node -> "ubicacionBox".equals(node.getId()));

        // Texto con la ubicación
        Label ubicacionLabel = new Label("Ubicación del producto: " + producto.getComercio().getDireccion() + ", "
                + producto.getComercio().getMunicipio() + ", " + producto.getComercio().getProvincia());
        ubicacionLabel.setStyle("-fx-font-size: 14px;");

        // Botón para abrir Google Maps
        Button abrirEnMapsBtn = new Button("Ver en Google Maps");
        abrirEnMapsBtn.setStyle("-fx-background-color: #de4733;" +
                "    -fx-text-fill: #FFFFFF;" +
                "    -fx-font-size: 18px;" +
                "    -fx-font-weight: bold;" +
                "    -fx-alignment: center;" +
                "    -fx-background-radius: 15;");

        abrirEnMapsBtn.setOnAction(e -> {
            String url = "https://www.google.com/maps?q=" + coordenadas;
            abrirEnNavegador(url);
        });

        // Contenedor horizontal
        HBox ubicacionBox = new HBox(10, ubicacionLabel, abrirEnMapsBtn);
        ubicacionBox.setId("ubicacionBox");
        ubicacionBox.setAlignment(Pos.CENTER_LEFT);
        ubicacionBox.setPadding(new Insets(10));

        // Añadir debajo del mapa
        mapVBox.getChildren().add(ubicacionBox);
    }

    /**
     * Abre la URL especificada en el navegador web predeterminado del sistema.
     *
     * @param url dirección web a abrir
     */
    private void abrirEnNavegador(String url) {
        System.out.println(inicioInfoLogConsola() + "Abriendo navegador");
        escribirLogInfo("Abriendo nabegador");
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            System.out.println(inicioErrorLogConsola() + "Error al abrir el navegador: " + e.getMessage());
            escribirLogError("Error al abrir el navegador: " + e.getMessage());
        }
    }
}