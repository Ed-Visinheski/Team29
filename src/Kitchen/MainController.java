//package Kitchen;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class MainController extends Application {
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        // Load the FXML file for the main page
//        Parent root = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
//
//        // Set up the scene
//        Scene scene = new Scene(root);
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("Restaurant Kitchen Interface");
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    // Handle View Inventory button action
//    public void handleViewInventory() throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("InventoryUI.fxml"));
//        Parent root = loader.load();
//        Stage stage = new Stage();
//        stage.setScene(new Scene(root));
//        stage.setTitle("Inventory Management Interface");
//        stage.show();
//    }
//
//    // Handle View Waste button action
//    public void handleViewWaste() throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("WasteUI.fxml"));
//        Parent root = loader.load();
//        Stage stage = new Stage();
//        stage.setScene(new Scene(root));
//        stage.setTitle("Waste Management Interface");
//        stage.show();
//    }
//}