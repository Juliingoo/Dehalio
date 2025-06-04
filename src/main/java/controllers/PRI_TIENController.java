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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.comercio;
import org.hibernate.Query;
import org.hibernate.Session;
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

public class PRI_TIENController implements Initializable{

    Session session = newSession();

    @FXML
    private Button btnInicio;

    @FXML
    private Button btnNegocios;

    @FXML
    private TextField barraBusqueda;

    @FXML
    private ComboBox<String> filtroComboBox;

    @FXML
    private Button btnBuscar;

    @FXML
    private ScrollPane scrollPaneComercios;

    @FXML
    private FlowPane comerciosContainer;

    @FXML
    private Slider kmSlider;

    @FXML
    private VBox mapVBox;

    private final MapView mapView = new MapView();

    private final MapLayer marcadorcomercioLayer = new MapLayer();

    private final int limiteResultados = 1000;

    double[] coordsUsuario = Localizacion.obtenerCoordenadasUsuario();
    double latUsuario = coordsUsuario[0];
    double lonUsuario = coordsUsuario[1];

    /**
     * Inicializa la pantalla de tiendas configurando los controles de búsqueda y filtros,
     * y cargando los comercios y el mapa centrado en la ubicación del usuario.
     *
     * @param location ubicación utilizada para resolver rutas relativas
     * @param resources recursos utilizados para la internacionalización
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(inicioInfoLogConsola() + "Inicializando pantalla principal. Recuperando comercios");
        escribirLogInfo("Inicializando pantalla principal. Recuperando comercios.");

        filtroComboBox.setOnAction(event -> buscar());
        kmSlider.setOnMouseClicked(event -> buscar());
        barraBusqueda.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                buscar();
            }
        });

        Query<comercio> qComercio = session.createQuery("from comercio");
        qComercio.setMaxResults(limiteResultados);
        List<comercio> listaComercios = qComercio.getResultList();
        cargarComercios(getComerciosFiltradosCercania(listaComercios));
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
     * Recarga la pantalla de tiendas al pulsar el botón correspondiente.
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
     * Ejecuta la búsqueda de comercios según el criterio y filtro seleccionados.
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
     * Filtra la lista de comercios recibida mostrando solo aquellos que se encuentran
     * dentro del radio de distancia seleccionado por el usuario.
     *
     * @param listaComerciosSinFiltrar lista de comercios a filtrar por cercanía
     * @return lista de comercios filtrados por distancia
     */
    public List<comercio> getComerciosFiltradosCercania(List<comercio> listaComerciosSinFiltrar){
        escribirLogInfo("Filtrando comercios por cercanía");
        List<comercio> comerciosFiltrados = new ArrayList<>();
        for (comercio comercio : listaComerciosSinFiltrar) {
            if (comercio != null && comercio.getCoordenadas() != null) {
                try {
                    String[] partes = comercio.getCoordenadas().split(",");
                    double latComercio = Double.parseDouble(partes[0].trim());
                    double lonComercio = Double.parseDouble(partes[1].trim());

                    double distancia = calcularDistancia(latUsuario, lonUsuario, latComercio, lonComercio);
                    LogAdministrador.escribirLogInfo("Distancia establecida: " + kmSlider.getValue());
                    if (distancia <= kmSlider.getValue()) { // filtrar a km
                        comerciosFiltrados.add(comercio);
                    }
                } catch (Exception e) {
                    System.out.println("Error al leer coordenadas del comercio: " + e.getMessage());
                    escribirLogError("Error al leer coordenadas del comercio: " + e.getMessage());
                }
            }
        }
        return comerciosFiltrados;
    }

    /**
     * Realiza la búsqueda de comercios aplicando el criterio de texto, el filtro seleccionado
     * y la cercanía geográfica, y actualiza la lista mostrada.
     */
    private void buscar() {
        String filtroSeleccionado = filtroComboBox.getValue();

        String criterioBusqueda = barraBusqueda.getText();

        // Buscar comercios cuyo nombre coincida
        Query<comercio> qComercio = session.createQuery("FROM comercio WHERE nombre LIKE :criterio", comercio.class);
        qComercio.setParameter("criterio", "%" + criterioBusqueda + "%");

        List<comercio> listaComercio = qComercio.getResultList();

        if (filtroSeleccionado == null || filtroSeleccionado.isEmpty()) {
            // Si no hay filtro seleccionado, aplicar solo el filtro de cercanía
            cargarComercios(getComerciosFiltradosCercania(listaComercio));
            return;
        }

        switch (filtroSeleccionado) {
            case "Nombre":
                listaComercio.sort((c1, c2) -> c1.getNombre().compareToIgnoreCase(c2.getNombre()));
                break;
            default:
                cargarComercios(getComerciosFiltradosCercania(listaComercio));
                break;
        }
        cargarComercios(getComerciosFiltradosCercania(listaComercio));
    }

    /**
     * Carga y muestra las tarjetas de los comercios recibidos en la interfaz.
     * Al hacer clic en una tarjeta, centra el mapa en la ubicación del comercio y muestra su información.
     *
     * @param listaComercios lista de comercios a mostrar
     */
    private void cargarComercios(List<comercio> listaComercios) {
        escribirLogInfo("Cargando lista de tiendas");
        comerciosContainer.getChildren().clear();

        for (comercio comercio : listaComercios) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TarjetaComercio.fxml"));
            VBox tarjetaComercio = null;
            try {
                tarjetaComercio = loader.load();
            } catch (IOException e) {
                System.out.println(inicioErrorLogConsola() + "Error al cargar la tarjeta de comercio: " + e.getMessage());
                escribirLogError("Error al cargar la tarjeta de comercio: " + e.getMessage());
            }

            // Obtener el controlador de la tarjeta y configurar los datos
            TarjetaComercioController controller = loader.getController();
            controller.setComercio(comercio);

            // Evento de clic para mostrar las coordenadas en el mapa
            tarjetaComercio.setOnMouseClicked(event -> {
                String coordenadasComercio = comercio.getCoordenadas();
                if (!coordenadasComercio.isEmpty() || !coordenadasComercio.isBlank()) {
                    String coordenasComercioSeparadas[] = coordenadasComercio.split(",");
                    double latitud = Double.valueOf(coordenasComercioSeparadas[0]);
                    double longitud = Double.valueOf(coordenasComercioSeparadas[1]);

                    MapPoint puntoComercio = new MapPoint(latitud, longitud);
                    mapView.flyTo(0, puntoComercio, 0.01); // Centrar el mapa en las coordenadas del comercio
                    mostrarUbicacion(coordenadasComercio, comercio);
                } else {
                    mostrarError("Coordenadas no disponibles", "El comercio no tiene coordenadas asignadas.",
                            "No se encuentran las coordenadas del comercio");
                    escribirLogError("El comercio no tiene coordenadas asignadas " +
                            " Comercio: " + comercio.getIdComercio());
                }
            });

            comerciosContainer.getChildren().add(tarjetaComercio);
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

        Rectangle clip = new Rectangle();
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        clip.widthProperty().bind(mapView.widthProperty());
        clip.heightProperty().bind(mapView.heightProperty());
        mapView.setClip(clip);

        mapView.flyTo(0, mapPoint, 0.1);
        mapView.setZoom(15);

        mapVBox.getChildren().add(mapView);
    }

    /**
     * Muestra la ubicación del comercio seleccionado en el mapa y en la interfaz,
     * permitiendo abrir la dirección en Google Maps.
     *
     * @param coordenadas coordenadas del comercio en formato "latitud,longitud"
     * @param comercio comercio cuya ubicación se va a mostrar
     */
    private void mostrarUbicacion(String coordenadas, comercio comercio) {
        escribirLogInfo("Obteniendo coordenadas del comercio: " + comercio.getIdComercio());
        System.out.println(inicioInfoLogConsola() + "Obteniendo coordenadas del comercio: " + comercio.getIdComercio());

        // Si ya existe el HBox de ubicación, lo quitamos para evitar duplicados
        mapVBox.getChildren().removeIf(node -> "ubicacionBox".equals(node.getId()));

        Label ubicacionLabel = new Label("Ubicación del comercio: " + comercio.getDireccion() + ", "
                + comercio.getMunicipio() + ", " + comercio.getProvincia());
        ubicacionLabel.setStyle("-fx-font-size: 14px;");

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

        HBox ubicacionBox = new HBox(10, ubicacionLabel, abrirEnMapsBtn);
        ubicacionBox.setId("ubicacionBox");
        ubicacionBox.setAlignment(Pos.CENTER_LEFT);
        ubicacionBox.setPadding(new Insets(10));

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