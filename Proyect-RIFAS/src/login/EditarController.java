package login;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditarController implements Initializable {

    @FXML
    private MenuButton ite_rifas_edit;
    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_premio;
    @FXML
    private DatePicker date_fechaa;

    private Connection conn;
    private int idRifaSeleccionada; // Variable para almacenar el ID de la rifa seleccionada

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conn = DatabaseConnector.getConnection(); // Obtener conexión a la base de datos
            cargarRifas(); // Cargar las rifas disponibles en el MenuButton
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar excepción de conexión a la base de datos
        }
    }

    private void cargarRifas() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT ID_RIFA, NOMBRE FROM RIFAS");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MenuItem menuItem = new MenuItem(rs.getString("NOMBRE"));
                int idRifa = rs.getInt("ID_RIFA");
                menuItem.setOnAction(e -> cargarDetallesRifa(idRifa));
                ite_rifas_edit.getItems().add(menuItem);
            }

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar excepción de consulta a la base de datos
        }
    }

    private void cargarDetallesRifa(int idRifa) {
        try {
            idRifaSeleccionada = idRifa; // Almacenar el ID de la rifa seleccionada
            PreparedStatement stmt = conn.prepareStatement("SELECT NOMBRE, PREMIO, FECHA FROM RIFAS WHERE ID_RIFA = ?");
            stmt.setInt(1, idRifa);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txt_nombre.setText(rs.getString("NOMBRE"));
                txt_premio.setText(rs.getString("PREMIO"));
                date_fechaa.setValue(rs.getDate("FECHA").toLocalDate());
            }

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar excepción de consulta a la base de datos
        }
    }

    @FXML
    private void actualizarRifa(ActionEvent event) {
        try {
            String nombre = txt_nombre.getText();
            String premio = txt_premio.getText();
            LocalDate fecha = date_fechaa.getValue();

            // Obtener el nombre de la rifa seleccionada
            String nombreRifa = ite_rifas_edit.getText();

            PreparedStatement stmt = conn.prepareStatement("UPDATE RIFAS SET NOMBRE = ?, PREMIO = ?, FECHA = ? WHERE ID_RIFA = ?");
            stmt.setString(1, nombre);
            stmt.setString(2, premio);
            stmt.setDate(3, java.sql.Date.valueOf(fecha));
            stmt.setInt(4, idRifaSeleccionada); // Usar el ID de la rifa seleccionada

            int rowsUpdated = stmt.executeUpdate();
            stmt.close();

            if (rowsUpdated > 0) {
                // Limpiar los campos después de la actualización
                txt_nombre.clear();
                txt_premio.clear();
                date_fechaa.setValue(null);
                System.out.println("Rifa actualizada exitosamente.");
            } else {
                System.out.println("No se encontró la rifa para actualizar.");
            }
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
