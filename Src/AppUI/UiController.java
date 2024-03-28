package Src.AppUI;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Src.BusinessLogic.DUIFiller;
import Src.OpenWeatherAPI.CurrentWeatherAPI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UiController {

    @FXML
    private ListView<String> locationList;

    @FXML
    private TextField tfcityname;

    @FXML
    private TextField tflatitude;

    @FXML
    private TextField tflongitude;

    private Stage mainWindow;

    public void setMainWindow(Stage mainwindow) {
        this.mainWindow = mainwindow;
    }

    @FXML
    void oncitysearch(ActionEvent event) {
        String cityName = tfcityname.getText();
        try {
            DUIFiller duiFiller = new DUIFiller();

            duiFiller.setcity(cityName);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Src/AppUI/screen2.fxml"));
            Parent root = loader.load();
            Screen2Controller controller = loader.getController();
            controller.initialize(cityName); // Pass city name to Screen2Controller
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
    }

    @FXML

    void onlatsearch(ActionEvent event) {
        double latitude = Double.parseDouble(tflatitude.getText());
        double longitude = Double.parseDouble(tflongitude.getText());
        try {

            DUIFiller duiFiller = new DUIFiller();

            // Pass latitude and longitude to DUIFiller
            duiFiller.setLatLong(latitude, longitude);
            duiFiller.setcity(null);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Src/AppUI/screen2.fxml"));
            Parent root = loader.load();
            Screen2Controller controller = loader.getController();

            // Fetch weather data using latitude and longitude
            CurrentWeatherAPI weatherAPI = new CurrentWeatherAPI();
            String weatherData = weatherAPI.getData(latitude, longitude);

            // Parse JSON data with Gson
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(weatherData, JsonObject.class);

            // Update UI elements with parsed data
            weatherAPI.parseJSON(jsonObject, controller);

            // Show the stage
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
    }

    public void initialize() {
        // Add initial items to the ListView
        locationList.getItems().addAll("Lahore");
    }

}
