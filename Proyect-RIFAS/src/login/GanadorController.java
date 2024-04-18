package login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class GanadorController {

    @FXML
    private MenuButton men_rifass;

    @FXML
    private Button ganador;

    @FXML
    private Label TXT_NOMBRE;

    @FXML
    private Label TXT_NUMERO;

    private int idRifaSeleccionada; // Para almacenar el ID de la rifa seleccionada

    @FXML
    private void initialize() {
        cargarRifas();
    }

    private void cargarRifas() {
        men_rifass.getItems().clear(); // Limpiar items previos

        try {
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT ID_RIFA, NOMBRE FROM RIFAS");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MenuItem menuItem = new MenuItem(rs.getString("NOMBRE"));
                int idRifa = rs.getInt("ID_RIFA");
                menuItem.setOnAction(e -> {
                    idRifaSeleccionada = idRifa;
                    men_rifass.setText(menuItem.getText()); // Actualizar texto del botón con el nombre de la rifa seleccionada
                });
                men_rifass.getItems().add(menuItem);
            }

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar excepción de consulta a la base de datos
        }
    }

    @FXML
    private void obtenerGanador() {
        if (idRifaSeleccionada == 0) {
            return; // No se ha seleccionado ninguna rifa
        }

        try {
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT CANTIDAD_TOTAL_NUMERO FROM RIFAS WHERE ID_RIFA = ?");
            stmt.setInt(1, idRifaSeleccionada);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int cantidadTotalNumeros = rs.getInt("CANTIDAD_TOTAL_NUMERO");

                Random random = new Random();
                int numeroGanador = random.nextInt(cantidadTotalNumeros) + 1; // Generar número aleatorio entre 1 y cantidadTotalNumeros

                mostrarResultado("Ganador", "Número: " + numeroGanador);

                // Actualizar el ganador en la base de datos (guardar el número del ganador en la tabla RIFAS)
                guardarGanadorEnBD(numeroGanador);
            }

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar excepción de consulta a la base de datos
        }
    }

    private void mostrarResultado(String nombre, String numero) {
        TXT_NOMBRE.setText(nombre);
        TXT_NUMERO.setText(numero);
    }

    private void guardarGanadorEnBD(int numeroGanador) {
        try {
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE RIFAS SET GANADOR = ? WHERE ID_RIFA = ?");
            stmt.setString(1, "Número: " + numeroGanador);
            stmt.setInt(2, idRifaSeleccionada);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar excepción de actualización en la base de datos
        }
    }

    @FXML
    private void irAMenu(ActionEvent event) {
        try {
            // Cargar MENU.fxml desde el mismo paquete (login)
            Parent root = FXMLLoader.load(getClass().getResource("MENU.fxml"));

            // Obtener el escenario actual
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

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
