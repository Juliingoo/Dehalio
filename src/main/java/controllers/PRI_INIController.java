package controllers;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import jakarta.persistence.ManyToOne;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import model.producto;
import org.hibernate.Session;

import org.hibernate.Query;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static controllers.NavegacionController.mostrarError;
import static database.Sesion.newSession;

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
    private VBox mapVBox;

    MapView mapView = new MapView();

    @FXML
    public void btnInicioAction(ActionEvent event){

    }

    @FXML
    public void btnNegociosAction(ActionEvent event){

    }

    @FXML
    public void btnBuscarAction(ActionEvent event) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Query<producto> qProducto = session.createQuery("from producto");
        List<producto> listaProducto = qProducto.getResultList();
        cargarProductos(listaProducto);
        cargarMapa();
    }

    private void cargarProductos(List<producto> listaProductos) {
        productosContainer.getChildren().clear();

        for (producto producto : listaProductos) {
            // Crear contenedor principal tipo VBox (tarjeta vertical)
            VBox tarjetaProducto = new VBox();
            tarjetaProducto.setSpacing(8);
            tarjetaProducto.setPadding(new Insets(10));
            tarjetaProducto.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ddd; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 4, 0, 0, 2);");
            tarjetaProducto.setMaxWidth(300);

            // Imagen del producto
            if (producto.getImagen() != null) {
                Image img = new Image(new ByteArrayInputStream(producto.getImagen()));
                ImageView imageView = new ImageView(img);
                imageView.setFitWidth(280);
                imageView.setPreserveRatio(true);
                tarjetaProducto.getChildren().add(imageView);
            }

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

            // Evento de clic para mostrar las coordenadas en el mapa
            tarjetaProducto.setOnMouseClicked(event -> {
                String coordenadasProducto = producto.getComercio().getCoordenadas();

                if (!coordenadasProducto.isEmpty() || !coordenadasProducto.isBlank()) {
                    String coordenasProductoSeparadas[] = coordenadasProducto.split(",");
                    double latitud = Double.valueOf(coordenasProductoSeparadas[0]);
                    double longitud = Double.valueOf(coordenasProductoSeparadas[1]);

                    MapPoint puntoProducto = new MapPoint(latitud, longitud);
                    mapView.flyTo(0, puntoProducto, 0.01); // Centrar el mapa en las coordenadas del producto

                } else {
                    mostrarError("Coordenadas no disponibles", "El producto no tiene coordenadas asignadas.", "Por favor, verifica los datos del producto.");
                }
            });

            // Añadir tarjeta al contenedor principal
            productosContainer.getChildren().add(tarjetaProducto);
        }
    }

    private void cargarMapa() {
        MapPoint mapPoint = new MapPoint(37.8806571, -4.7913827);

        // El MapView se ajusta al tamaño del VBox
        mapView.prefWidthProperty().bind(mapVBox.widthProperty());
        mapView.prefHeightProperty().bind(mapVBox.heightProperty());
        VBox.setVgrow(mapView, Priority.ALWAYS);

        //Clip para esquinas redondeadas
        Rectangle clip = new Rectangle();
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        clip.widthProperty().bind(mapView.widthProperty());
        clip.heightProperty().bind(mapView.heightProperty());
        mapView.setClip(clip);

        // Inicialización del mapa
        mapView.flyTo(0, mapPoint, 0.1);
        mapView.setZoom(15);

        mapVBox.getChildren().add(mapView);
    }


}
