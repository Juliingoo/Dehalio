package controllers;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import database.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import model.comercio;
import model.productoFavorito;
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
    private Button btnBuscar;

    @FXML
    private ScrollPane scrollPaneComercios;

    @FXML
    private FlowPane comerciosContainer;

    @FXML
    private Slider kmSlider;

    @FXML
    private VBox mapVBox;

    MapView mapView = new MapView();

    private MapLayer marcadorcomercioLayer = new MapLayer();

    double[] coordsUsuario = Localizacion.obtenerCoordenadasUsuario();
    double latUsuario = coordsUsuario[0];
    double lonUsuario = coordsUsuario[1];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(inicioInfoLogConsola() + "Inicializando pantalla principal. Recuperando comercios");
        escribirLogInfo("Inicializando pantalla principal. Recuperando comercios.");
        Query<comercio> qComercio = session.createQuery("from comercio");
        List<comercio> listaComercios = qComercio.getResultList();
        cargarComercios(getComerciosFiltradosCercania(listaComercios));
        cargarMapa();
    }

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

    public void btnFavoritosAction(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton favoritos pulsado");
        escribirLogInfo("Boton favoritos pulsado");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            NavegacionController.navegar(stage, Paths.PRI_TIEN);
        } catch (IOException e) {
            System.out.println("Error al navegar a favoritos: " + e.getMessage());
            escribirLogError("Error al navegar a favoritos: " + e.getMessage());
        }
    }

    public void btnAjustesAction(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton ajustes pulsado");
        escribirLogInfo("Boton ajustes pulsado");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            NavegacionController.navegar(stage, Paths.PRI_TIEN);
        } catch (IOException e) {
            System.out.println("Error al navegar a ajustes: " + e.getMessage());
            escribirLogError("Error al navegar a ajustes: " + e.getMessage());
        }
    }

    @FXML
    public void btnBuscarAction(ActionEvent event) {
        System.out.println(inicioInfoLogConsola() + "Boton buscar pulsado");
        escribirLogInfo("Boton buscar pulsado");

        String criterioBusqueda = barraBusqueda.getText();

        // Buscar comercios cuyo nombre coincida
        Query<comercio> qComercio = session.createQuery("FROM comercio WHERE nombre LIKE :criterio", comercio.class);
        qComercio.setParameter("criterio", "%" + criterioBusqueda + "%");

        cargarComercios(getComerciosFiltradosCercania(qComercio.getResultList()));
    }

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

    private void cargarComercios(List<comercio> listaComercios) {
        escribirLogInfo("Cargando lista de tiendas");
        comerciosContainer.getChildren().clear();

        for (comercio comercio : listaComercios) {
            // Crear contenedor principal tipo VBox (tarjeta vertical)
            VBox tarjetaComercio = new VBox();
            tarjetaComercio.setSpacing(8);
            tarjetaComercio.setPadding(new Insets(10));
            tarjetaComercio.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; " +
                    "-fx-border-radius: 10; -fx-border-color: #ddd; " +
                    "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 4, 0, 0, 2);");
            tarjetaComercio.setPrefWidth(300);
            tarjetaComercio.setPrefHeight(100);

            // Imagen del comercio
            byte[] imagenComercio = comercio.getImagen();
            Image img;

            if (imagenComercio != null && imagenComercio.length > 0) {
                img = new Image(new ByteArrayInputStream(imagenComercio));
            } else {
                img = new Image(getClass().getResourceAsStream("/iconos/comercioSinImagen.png"));
            }

            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            tarjetaComercio.getChildren().add(imageView);


            // Nombre del comercio
            Label labelNombre = new Label(comercio.getNombre());
            labelNombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            //Direccion del comercio
            Label labelDireccion = new Label(comercio.getDireccion());
            labelDireccion.setStyle("-fx-font-size: 12; -fx-text-fill: #757575");

            tarjetaComercio.getChildren().addAll(labelNombre, labelDireccion);

            // Cambiar estilo al pasar el ratón por encima
            tarjetaComercio.setOnMouseEntered(event -> tarjetaComercio.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ddd; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 2);"));

            // Restaurar estilo al salir el ratón
            tarjetaComercio.setOnMouseExited(event -> tarjetaComercio.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ddd; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 4, 0, 0, 2);"));

            // Cambiar estilo al pulsar
            tarjetaComercio.setOnMousePressed(event -> tarjetaComercio.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ddd; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.3), 4, 0, 0, 2);"));

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

            // Añadir tarjeta al contenedor principal
            comerciosContainer.getChildren().add(tarjetaComercio);
        }
    }

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
//        mapView.addLayer(marcadorComercioLayer);

        mapVBox.getChildren().add(mapView);
    }

    private void mostrarUbicacion(String coordenadas, comercio comercio) {
        escribirLogInfo("Obteniendo coordenadas del comercio: " + comercio.getIdComercio());
        System.out.println(inicioInfoLogConsola() + "Obteniendo coordenadas del comercio: " + comercio.getIdComercio());

        // Si ya existe el HBox de ubicación, lo quitamos para evitar duplicados
        mapVBox.getChildren().removeIf(node -> "ubicacionBox".equals(node.getId()));

        // Texto con la ubicación
        Label ubicacionLabel = new Label("Ubicación del comercio: " + comercio.getDireccion() + ", "
                + comercio.getMunicipio() + ", " + comercio.getProvincia());
        ubicacionLabel.setStyle("-fx-font-size: 14px;");

        // Botón para abrir Google Maps
        Button abrirEnMapsBtn = new Button("Ver en Google Maps");
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