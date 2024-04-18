package login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MENUController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Método de inicialización, puedes realizar alguna inicialización aquí si es necesario
    }    
    
    @FXML
    private void abrirRifas(ActionEvent event) {
        abrirInterfaz("rifas.fxml", event);
    }

    @FXML
    private void abrirComprar(ActionEvent event) {
        abrirInterfaz("Comprar.fxml", event);
    }

    @FXML
    private void abrirEditar(ActionEvent event) {
        abrirInterfaz("Editar.fxml", event);
    }

    @FXML
    private void abrirGanador(ActionEvent event) {
        abrirInterfaz("Ganador.fxml", event);
    }

    @FXML
    private void abrirEliminar(ActionEvent event) {
        abrirInterfaz("Eliminar.fxml", event);
    }

    @FXML
    private void accionSalir(ActionEvent event) {
        // Obtener la referencia de la ventana actual (botón presionado)
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        
        // Cerrar la ventana actual
        stage.close();
    }

    private void abrirInterfaz(String fxmlFile, ActionEvent event) {
        try {
            // Cargar el archivo FXML correspondiente
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Crear una nueva escena
            Scene scene = new Scene(root);
            
            // Obtener la referencia de la ventana actual (botón presionado)
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            
            // Establecer la escena en la ventana
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.out.println("Error al abrir " + fxmlFile + ": " + ex.getMessage());
        }
    }
}
