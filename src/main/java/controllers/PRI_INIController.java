package controllers;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import jakarta.persistence.ManyToOne;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import model.comercio;
import model.producto;
import org.hibernate.Session;

import org.hibernate.Query;
import utilities.Localizacion;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static controllers.NavegacionController.mostrarError;
import static database.Sesion.newSession;
import static utilities.Localizacion.calcularDistancia;

public class PRI_INIController implements Initializable{

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
    private ScrollPane scrollPaneProductos;

    @FXML
    private FlowPane productosContainer;

    @FXML
    private Slider kmSlider;

    @FXML
    private VBox mapVBox;

    MapView mapView = new MapView();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Query<producto> qProducto = session.createQuery("from producto");
        List<producto> listaProducto = qProducto.getResultList();
        cargarProductos(listaProducto);
        cargarMapa();
    }

    @FXML
    public void btnNegociosAction(ActionEvent event){

    }

    @FXML
    public void btnBuscarAction(ActionEvent event) {
        String criterioBusqueda = barraBusqueda.getText();

        // Obtener ubicación del usuario
        double[] coordsUsuario = Localizacion.obtenerCoordenadasUsuario();
        if (coordsUsuario == null) {
            System.out.println("No se pudo obtener la ubicación del usuario.");
            return;
        }
        double latUsuario = coordsUsuario[0];
        double lonUsuario = coordsUsuario[1];

        // Buscar productos cuyo nombre coincida
        Query<producto> qProducto = session.createQuery("FROM producto WHERE nombre LIKE :criterio", producto.class);
        qProducto.setParameter("criterio", "%" + criterioBusqueda + "%");

        List<producto> productosFiltrados = new ArrayList<>();
        for (producto p : qProducto.getResultList()) {
            comercio c = p.getComercio(); // asumiendo getter correcto
            if (c != null && c.getCoordenadas() != null) {
                try {
                    String[] partes = c.getCoordenadas().split(",");
                    double latComercio = Double.parseDouble(partes[0].trim());
                    double lonComercio = Double.parseDouble(partes[1].trim());

                    double distancia = calcularDistancia(latUsuario, lonUsuario, latComercio, lonComercio);
                    if (distancia <= kmSlider.getValue()) { // filtrar a km
                        productosFiltrados.add(p);
                    }
                } catch (Exception e) {
                    System.out.println("Error al leer coordenadas del comercio: " + e.getMessage());
                }
            }
        }

        cargarProductos(productosFiltrados);
    }

    private void cargarProductos(List<producto> listaProductos) {
        productosContainer.getChildren().clear();

        for (producto producto : listaProductos) {
            // Crear contenedor principal tipo VBox (tarjeta vertical)
            VBox tarjetaProducto = new VBox();
            tarjetaProducto.setSpacing(8);
            tarjetaProducto.setPadding(new Insets(10));
            tarjetaProducto.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; " +
                    "-fx-border-radius: 10; -fx-border-color: #ddd; " +
                    "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 4, 0, 0, 2);");
            tarjetaProducto.setPrefWidth(300);
            tarjetaProducto.setPrefHeight(100);

            // Imagen del producto
            byte[] imagenProducto = producto.getImagen();
            Image img;

            if (imagenProducto != null && imagenProducto.length > 0) {
                img = new Image(new ByteArrayInputStream(imagenProducto));
            } else {
                img = new Image(getClass().getResourceAsStream("/iconos/productoSinImagen.png"));
            }

            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            tarjetaProducto.getChildren().add(imageView);


            // Nombre del producto
            Label labelNombre = new Label(producto.getNombre());
            labelNombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            // Precio
            Label labelPrecio = new Label(String.format("%.2f €", producto.getPrecio()));
            labelPrecio.setStyle("-fx-font-size: 14px; -fx-text-fill: #e53935;");

            // Comercio asociado (asegúrate de que producto.getComercio() no sea null)
            String nombreComercio = producto.getComercio() != null ? producto.getComercio().getNombre() : "Sin comercio";
            Label labelComercio = new Label("Comercio: " + nombreComercio);
            labelComercio.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575;");

            // Añadir elementos a la tarjeta
            tarjetaProducto.getChildren().addAll(labelNombre, labelPrecio, labelComercio);

            // Cambiar estilo al pasar el ratón por encima
            tarjetaProducto.setOnMouseEntered(event -> tarjetaProducto.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ddd; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 2);"));

            // Restaurar estilo al salir el ratón
            tarjetaProducto.setOnMouseExited(event -> tarjetaProducto.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ddd; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 4, 0, 0, 2);"));

            // Cambiar estilo al pulsar
            tarjetaProducto.setOnMousePressed(event -> tarjetaProducto.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ddd; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.3), 4, 0, 0, 2);"));

            // Evento de clic para mostrar las coordenadas en el mapa
            tarjetaProducto.setOnMouseClicked(event -> {
                String coordenadasProducto = producto.getComercio().getCoordenadas();
                if (!coordenadasProducto.isEmpty() || !coordenadasProducto.isBlank()) {
                    String coordenasProductoSeparadas[] = coordenadasProducto.split(",");
                    double latitud = Double.valueOf(coordenasProductoSeparadas[0]);
                    double longitud = Double.valueOf(coordenasProductoSeparadas[1]);

                    MapPoint puntoProducto = new MapPoint(latitud, longitud);
                    mapView.flyTo(0, puntoProducto, 0.01); // Centrar el mapa en las coordenadas del producto
                    mostrarUbicacion(coordenadasProducto);
                } else {
                    mostrarError("Coordenadas no disponibles", "El producto no tiene coordenadas asignadas.", "Por favor, verifica los datos del producto.");
                }
            });

            // Añadir tarjeta al contenedor principal
            productosContainer.getChildren().add(tarjetaProducto);
        }
    }

    private MapLayer marcadorProductoLayer = new MapLayer();

    private void cargarMapa() {
        MapPoint mapPoint = new MapPoint(37.8806571, -4.7913827);

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

        // Inicialización del mapa
        mapView.flyTo(0, mapPoint, 0.1);
        mapView.setZoom(15);

        // Añadir capa de marcador al mapa
        mapView.addLayer(marcadorProductoLayer);

        mapVBox.getChildren().add(mapView);
    }

    private void mostrarUbicacion(String coordenadas) {
        // Si ya existe el HBox de ubicación, lo quitamos para evitar duplicados
        mapVBox.getChildren().removeIf(node -> "ubicacionBox".equals(node.getId()));

        // Texto con la ubicación
        Label ubicacionLabel = new Label("Ubicación del producto: " + coordenadas);
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
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
