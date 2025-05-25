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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.comercio;
import model.producto;
import model.productoFavorito;
import org.hibernate.Query;
import org.hibernate.Session;
import utilities.Localizacion;
import utilities.LogAdministrador;
import utilities.Paths;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static controllers.NavegacionController.mostrarError;
import static database.Sesion.newSession;
import static utilities.LogAdministrador.*;
import static utilities.LogAdministrador.escribirLogError;

public class PRI_FAVController implements Initializable {

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
    private VBox mapVBox;

    private final MapView mapView = new MapView();
    private final MapLayer marcadorProductoLayer = new MapLayer();
    private final int limiteResultados = 1000;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LogAdministrador.escribirLogInfo("Inicializando pantalla de favoritos.");

        filtroComboBox.setOnAction(event -> buscar());
        barraBusqueda.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                buscar();
            }
        });

        cargarProductosFavoritos();
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
            NavegacionController.navegar(stage, Paths.PRI_FAV);
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
            NavegacionController.navegar(stage, Paths.PRI_AJU);
        } catch (IOException e) {
            System.out.println("Error al navegar a ajustes: " + e.getMessage());
            escribirLogError("Error al navegar a ajustes: " + e.getMessage());
        }
    }

    @FXML
    public void btnBuscarAction(ActionEvent event) {
        buscar();
    }

    private void cargarProductosFavoritos() {
        Query<productoFavorito> qProducto = session.createQuery("FROM productoFavorito WHERE usuario = :usuario", productoFavorito.class);
        qProducto.setParameter("usuario", Sesion.usuario);
        qProducto.setMaxResults(limiteResultados);

        List<productoFavorito> listaProductosFavoritos = qProducto.getResultList();
        List<producto> productos = new ArrayList<>();
        for (productoFavorito pf : listaProductosFavoritos) {
            productos.add(pf.getProducto());
        }

        cargarProductos(productos);
    }

    private void buscar() {
        String filtroSeleccionado = filtroComboBox.getValue();
        String criterioBusqueda = barraBusqueda.getText();

        Query<productoFavorito> qProductoFavorito = session.createQuery("FROM productoFavorito WHERE usuario = :usuario", productoFavorito.class);
        qProductoFavorito.setParameter("usuario", Sesion.usuario);
        qProductoFavorito.setMaxResults(limiteResultados);

        List<productoFavorito> listaProductosFavoritos = qProductoFavorito.getResultList();

        List<productoFavorito> productosFiltrados = new ArrayList<>();
        for (productoFavorito productoFavorito : listaProductosFavoritos) {
            if (productoFavorito.getProducto().getNombre().toLowerCase().contains(criterioBusqueda.toLowerCase())) {
                productosFiltrados.add(productoFavorito);
            }
        }

        List<producto> productosFinales = new ArrayList<>();
        for (productoFavorito pf : productosFiltrados) {
            productosFinales.add(pf.getProducto());
        }

        if (filtroSeleccionado != null) {
            switch (filtroSeleccionado) {
                case "Precio Ascendente":
                    productosFinales.sort((p1, p2) -> Double.compare(p1.getPrecio(), p2.getPrecio()));
                    break;
                case "Precio Descendente":
                    productosFinales.sort((p1, p2) -> Double.compare(p2.getPrecio(), p1.getPrecio()));
                    break;
                case "Nombre":
                    productosFinales.sort((p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()));
                    break;
            }
        }

        cargarProductos(productosFinales);
    }

    private void cargarProductos(List<producto> listaProductos) {
        LogAdministrador.escribirLogInfo("Cargando lista de productos favoritos.");
        productosContainer.getChildren().clear();

        for (producto producto : listaProductos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/TarjetaProducto.fxml"));
                Node tarjetaProducto = loader.load();

                TarjetaProductoController controller = loader.getController();
                controller.setProducto(producto);

                tarjetaProducto.setOnMouseClicked(event -> {
                    String coordenadasProducto = producto.getComercio().getCoordenadas();
                    if (coordenadasProducto != null && !coordenadasProducto.isBlank()) {
                        String[] coordenasProductoSeparadas = coordenadasProducto.split(",");
                        double latitud = Double.parseDouble(coordenasProductoSeparadas[0]);
                        double longitud = Double.parseDouble(coordenasProductoSeparadas[1]);

                        MapPoint puntoProducto = new MapPoint(latitud, longitud);
                        mapView.flyTo(0, puntoProducto, 0.01);
                        mostrarUbicacion(coordenadasProducto, producto);
                    } else {
                        mostrarError("Coordenadas no disponibles", "El producto no tiene coordenadas asignadas.",
                                "No se encuentran las coordenadas del producto");
                        LogAdministrador.escribirLogError("El producto no tiene coordenadas asignadas " +
                                " Producto: " + producto.getIdProducto() + " | en Tienda:" + producto.getComercio());
                    }
                });

                productosContainer.getChildren().add(tarjetaProducto);
            } catch (IOException e) {
                LogAdministrador.escribirLogError("Error al cargar la tarjeta del producto: " + e.getMessage());
                mostrarError("Error al cargar producto", "No se pudo cargar la tarjeta del producto.", e.getMessage());
            }
        }
    }

    private void cargarMapa() {
        LogAdministrador.escribirLogInfo("Cargando mapa");

        MapPoint mapPoint = new MapPoint(Localizacion.obtenerCoordenadasUsuario()[0], Localizacion.obtenerCoordenadasUsuario()[1]);

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

        mapView.addLayer(marcadorProductoLayer);
        mapVBox.getChildren().add(mapView);
    }

    private void mostrarUbicacion(String coordenadas, producto producto) {
        LogAdministrador.escribirLogInfo("Obteniendo coordenadas del producto: " + producto.getIdProducto());

        mapVBox.getChildren().removeIf(node -> "ubicacionBox".equals(node.getId()));

        Label ubicacionLabel = new Label("Ubicación del producto: " + producto.getComercio().getDireccion() + ", "
                + producto.getComercio().getMunicipio() + ", " + producto.getComercio().getProvincia());
        ubicacionLabel.setStyle("-fx-font-size: 14px;");

        Button abrirEnMapsBtn = new Button("Ver en Google Maps");
        abrirEnMapsBtn.setStyle("-fx-background-color: #de4733; -fx-text-fill: #FFFFFF; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 15;");
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

    private void abrirEnNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            LogAdministrador.escribirLogError("Error al abrir el navegador: " + e.getMessage());
        }
    }

}