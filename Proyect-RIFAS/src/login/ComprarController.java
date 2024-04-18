package login;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import oracle.jdbc.OracleTypes;



public class ComprarController implements Initializable {

    @FXML
    private StackPane contenedorPadre;

    @FXML
    private GridPane gridpane;

    @FXML
    private MenuButton menu_rifas;

    @FXML
    private Label lbl_rifa;

    @FXML
    private TextField tf_comprador;

    private String rifaSeleccionada;
    private int numeroSeleccionado;

    @FXML
    private void llenarMenuRifas() {
        menu_rifas.getItems().clear();
        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement mostrarrifas = conn.prepareCall("{call Obtener_Nombres_Rifas(?)}")) {

            mostrarrifas.registerOutParameter(1, OracleTypes.CURSOR);
            mostrarrifas.execute();

            ResultSet rs = (ResultSet) mostrarrifas.getObject(1);

            while (rs.next()) {
                String nombreRifa = rs.getString("nombre");
                MenuItem menuItem = new MenuItem(nombreRifa);
                menuItem.setOnAction(event -> {
                    rifaSeleccionada = nombreRifa;
                    mostrarNumerosRifa();
                });
                menu_rifas.getItems().add(menuItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mostrarNumerosRifa() {
        if (rifaSeleccionada == null || rifaSeleccionada.isEmpty()) {
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement numTotal = conn.prepareCall("{call Obtener_Numero_Total(?, ?)}")) {

            numTotal.setString(1, rifaSeleccionada);
            numTotal.registerOutParameter(2, Types.INTEGER);
            numTotal.execute();

            lbl_rifa.setText(rifaSeleccionada);
            List<Integer> numerosApartados = obtenerNumerosApartados(rifaSeleccionada);
            dibujarNumeros(numTotal.getInt(2), numerosApartados);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Integer> obtenerNumerosApartados(String nombreRifa) {
        List<Integer> numeros = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement numerosComprados = conn.prepareCall("{call Numeros_Apartados_Comprados(?, ?)}")) {

            numerosComprados.setString(1, nombreRifa);
            numerosComprados.registerOutParameter(2, OracleTypes.CURSOR);
            numerosComprados.execute();

            ResultSet rs = (ResultSet) numerosComprados.getObject(2);

            while (rs.next()) {
                numeros.add(rs.getInt("numero_elegido"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numeros;
    }

    private void dibujarNumeros(int totalNumeros, List<Integer> numerosApartados) {
        gridpane.getChildren().clear();
        gridpane.getColumnConstraints().clear();
        gridpane.getRowConstraints().clear();

        gridpane.setHgap(10);
        gridpane.setVgap(10);

        for (int i = 0; i < 10; i++) {
            gridpane.getColumnConstraints().add(new ColumnConstraints(50));
        }

        int numRows = (totalNumeros + 9) / 10;
        for (int i = 0; i < numRows; i++) {
            gridpane.getRowConstraints().add(new RowConstraints(50));
        }

        for (int i = 0; i < totalNumeros; i++) {
            int numero = i + 1;
            Button button = new Button(String.valueOf(numero));
            button.setPrefSize(50, 50);

            // Obtener estado del número desde la base de datos
            String estado = obtenerEstadoNumero(rifaSeleccionada, numero);

             if (estado.equalsIgnoreCase("Apartado")) {
                    button.setStyle("-fx-background-color: yellow");
                } else if (estado.equalsIgnoreCase("Comprado")) {
                    button.setStyle("-fx-background-color: red");
                } else {
                    button.setStyle("-fx-background-color: green");
                }
            int columna = i % 10;
            int fila = i / 10;
            gridpane.add(button, columna, fila);

            button.setOnAction(event -> {
                numeroSeleccionado = numero;
            });
        }
    }

    private String obtenerEstadoNumero(String nombreRifa, int numero) {
        String estado = "Disponible";

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement obtenerEstado = conn.prepareCall("{call OBTENER_ESTADOS_NUMEROS_RIFA(?, ?)}")) {

            obtenerEstado.setString(1, nombreRifa);
            obtenerEstado.registerOutParameter(2, OracleTypes.CURSOR);
            obtenerEstado.execute();

            ResultSet rs = (ResultSet) obtenerEstado.getObject(2);

            while (rs.next()) {
                if (rs.getInt("NUMERO_ELEGIDO") == numero) {
                    estado = rs.getString("ESTADO");
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return estado;
    }

    @FXML
    private void apartarNumero(ActionEvent event) {
        realizarAccion("Apartado");
    }

    @FXML
    private void comprarNumero(ActionEvent event) {
        realizarAccion("Comprado");
    }

    private void realizarAccion(String estado) {
        if (numeroSeleccionado == 0 || tf_comprador.getText().isEmpty()) {
            System.err.println("Debe seleccionar un número y especificar el comprador.");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement stmtInsertarPersona = conn.prepareCall("{call Insertar_Persona(?)}");
             CallableStatement stmtInsertarNumeroRifa = conn.prepareCall("{call Insertar_Numero_Rifa(?, ?, ?, ?)}")) {

            stmtInsertarPersona.setString(1, tf_comprador.getText());
            stmtInsertarPersona.execute();

            stmtInsertarNumeroRifa.setString(1, lbl_rifa.getText());
            stmtInsertarNumeroRifa.setInt(2, numeroSeleccionado);
            stmtInsertarNumeroRifa.setString(3, estado);
            stmtInsertarNumeroRifa.setString(4, tf_comprador.getText());
            stmtInsertarNumeroRifa.execute();

            // Actualizar estilo del botón apartado/comprado
            gridpane.getChildren().stream()
                    .filter(node -> node instanceof Button && ((Button) node).getText().equals(String.valueOf(numeroSeleccionado)))
                    .findFirst()
                    .ifPresent(node -> ((Button) node).setStyle("-fx-background-color: " + (estado.equals("Apartado") ? "yellow" : "red")));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Limpiar selección
        numeroSeleccionado = 0;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        llenarMenuRifas();
    }
    @FXML
private void irAMenu(ActionEvent event) {
    try {
        // Cargar MENU.fxml desde el mismo paquete (login)
        Parent root = FXMLLoader.load(getClass().getResource("MENU.fxml"));

        // Obtener el escenario actual
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        // Crear una nueva escena con la interfaz MENU.fxml
        Scene scene = new Scene(root);

        // Establecer la nueva escena en el escenario
        stage.setScene(scene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        // Manejar excepción de carga de la interfaz MENU.fxml
    }
}

}
