package Src.AppUI;

import java.util.Date;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.zone.ZoneRulesException;
import java.util.Locale;
import Src.BusinessLogic.DUIFiller;
import Src.OpenWeatherAPI.CurrentWeatherAPI;
import Src.OpenWeatherAPI.WeatherForecast5Days;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import Src.WeatherDataStorage.DBAirPoll;
import Src.WeatherDataStorage.DBCurrweatherData;
import Src.WeatherDataStorage.DBweatherForecast;
import Src.BusinessLogic.TempApiStorage.CurrentWeatherAPIData;
import Src.BusinessLogic.TempApiStorage.WeatherForecastAPIData;

public class mainscreenController {
    private String city;
    private double longitude;
    private double latitude;

    private final CurrentWeatherAPI weatherAPI;
    private final WeatherForecast5Days forecastAPI;
    private final DBCurrweatherData currWeatherData;
    // private final DBAirPoll airPoll;
    private final DBweatherForecast weatherForecast;
    private Connection connection;

    public mainscreenController() {
        this.weatherAPI = new CurrentWeatherAPI();
        this.forecastAPI = new WeatherForecast5Days();
        this.weatherAPI.setController(this); // Set the controller reference
        this.forecastAPI.setController(this);
        this.currWeatherData = new DBCurrweatherData();
        // this.airPoll = new DBAirPoll();
        this.weatherForecast = new DBweatherForecast();
        try {
            // Establish a connection to the database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/weather_Cache", "root", "4820");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        // Set default city (Lahore) on startup
        weatherAPI.APIcall("Lahore");
        forecastAPI.APIcall("Lahore");
        // Set current day's day and date
        setDayAndDate();

    }

    // Method to set the current day's day and date
    private void setDayAndDate() {
        LocalDate currentDate = LocalDate.now();
        String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String date = currentDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        tfDayAndDate.setText(dayOfWeek + ", " + date);
    }

    private Stage mainWindow;

    public void setMainWindow(Stage mainwindow) {
        this.mainWindow = mainwindow;
    }

    public void initialize(String lat, String lon) {
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        currWeatherData.setController(this);
        weatherAPI.setController(this);
        weatherForecast.setController(this);
        boolean currWeatherExists = currWeatherData.isDataPresentByLatLon(connection, latitude, longitude);
        boolean weatherForecastExists = weatherForecast.isDataPresentByLatLon(connection, latitude, longitude);

        if (currWeatherExists && weatherForecastExists) {
            // Show a message box indicating that data is being fetched from the database
            showAlert("Data Found for both current weather and weather forecast",
                    "Fetching data from the database...");

            currWeatherData.displayDataFromDatabaseByLatLon(connection, latitude, longitude);
            weatherForecast.displayDataFromDatabaseByLatLon(connection, latitude, longitude);

        } else {
            showAlert("Data Not Found", "Fetching data from the API...");

            weatherAPI.APIcall(latitude, longitude);
            forecastAPI.APIcall(latitude, longitude);
        }

    }

    public void initialize(String cityName) {
        currWeatherData.setController(this);
        weatherForecast.setController(this);
        weatherAPI.setController(this); // Set the reference to this controller
        // Check if data exists in the database for current weather
        boolean currWeatherExists = currWeatherData.isDataPresentByCityName(connection, cityName);
        // Check if data exists in the database for weather forecast
        boolean weatherForecastExists = weatherForecast.isDataPresentByCityName(connection, cityName);
        if (currWeatherExists && weatherForecastExists) {
            // // Show a message box indicating that data is being fetched from the database
            showAlert("Data Found for both current weather and weather forecast",
                    "Fetching data from the database...");

            // Fetch data from the database for all tables
            currWeatherData.displayDataFromDatabaseByCityName(connection, cityName);
            weatherForecast.displayDataFromDatabaseByCityName(connection, cityName);
        } else {
            // Show a message box indicating that data is being fetched from the API
            showAlert("Data Not Found", "Fetching data from the API...");

            // Fetch data from the API for all tables
            weatherAPI.APIcall(cityName);
            forecastAPI.APIcall(cityName);

        }
    }

    // Method to show an alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private Label tfAQI;

    @FXML
    private Label tfCityAndCountryName;

    @FXML
    private Label tfCurrentTemp;

    @FXML
    private Label tfCurrentWeatherConditon;

    @FXML
    private ImageView tfCurrentWeatherIcon;

    @FXML
    private Label tfDay1;

    @FXML
    private ImageView tfDay1Icon;

    @FXML
    private Label tfDay1MaxTemp;

    @FXML
    private Label tfDay1MinTemp;

    @FXML
    private Label tfDay1WeatherConditon;

    @FXML
    private Label tfDay2;

    @FXML
    private ImageView tfDay2Icon;

    @FXML
    private Label tfDay2MaxTemp;

    @FXML
    private Label tfDay2MinTemp;

    @FXML
    private Label tfDay2WeatherConditon;

    @FXML
    private Label tfDay3;

    @FXML
    private ImageView tfDay3Icon;

    @FXML
    private Label tfDay3MaxTemp;

    @FXML
    private Label tfDay3MinTemp;

    @FXML
    private Label tfDay3WeatherConditon;

    @FXML
    private Label tfDay4;

    @FXML
    private ImageView tfDay4Icon;

    @FXML
    private Label tfDay4MaxTemp;

    @FXML
    private Label tfDay4MinTemp;

    @FXML
    private Label tfDay4WeatherConditon;

    @FXML
    private Label tfDay5;

    @FXML
    private ImageView tfDay5Icon;

    @FXML
    private Label tfDay5MaxTemp;

    @FXML
    private Label tfDay5MinTemp;
    @FXML
    private Label tfDay5WeatherConditon;

    @FXML
    private Label tfDayAndDate;

    @FXML
    private Label tfFeelsLike;

    @FXML
    private Label tfHumidity;

    @FXML
    private Label tfLatitude;

    @FXML
    private Label tfLongitude;

    @FXML
    private Label tfMaximumTemp;

    @FXML
    private Label tfMinimumTemp;

    @FXML
    private Label tfPressure;

    @FXML
    private Button tfSearchWeatherButton;

    @FXML
    private Label tfStandardTime;

    @FXML
    private Label tfSunrise;

    @FXML
    private Label tfSunset;

    @FXML
    private Label tfWindspeed;

    @FXML
    void ExpandAirPollutionInfo(ActionEvent event) {
        String lat = tfLatitude.getText();
        String lon = tfLongitude.getText();
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Src/AppUI/screen3.fxml"));
            Parent root = loader.load();
            Screen3Controller controller = loader.getController();
            controller.initialize(latitude, longitude); // Pass city name to Screen2Controller
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Air pollution");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
    }

    @FXML
    void SearchWeatherButton(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Src/AppUI/weatherui.fxml"));
            Parent root = loader.load();
            UiController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Search Weather");
            stage.setScene(new Scene(root));
            stage.show();
            // Close the current stage (optional)
            Node sourceNode = (Node) event.getSource();
            Stage currentStage = (Stage) sourceNode.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace(); // Handle exception appropriately
        }

    }

    public void updateUI(mainscreenController controller, CurrentWeatherAPIData jsonObject) {
        // Convert temperature units from Kelvin to Celsius
        double lat = jsonObject.getLatitude();
        double lon = jsonObject.getLongitude();
        String cityName = jsonObject.getCityName();// Changed variable name to cityName
        String weatherCondition = jsonObject.getWeatherMain();
        String weatherIconURL = jsonObject.getWeatherIcon(); // Icon of current weather
        double currentTemp = jsonObject.getTemperature();
        double feelsLike = jsonObject.getFeelsLike();
        double tempMin = jsonObject.getTempMin();
        double tempMax = jsonObject.getTempMax();
        int pressure = jsonObject.getPressure();
        int humidity = jsonObject.getHumidity();
        double windSpeed = jsonObject.getWindSpeed();
        String countryName = jsonObject.getCountry();
        int sunrise = jsonObject.getSunrise(); // Sunrise Time
        int sunset = jsonObject.getSunset();
        int timezone = jsonObject.getTimezone();

        double temperatureInCelsius = currentTemp - 273.15;
        double feelslikeInCelsius = feelsLike - 273.15;
        double mintemperatureInCelsius = tempMin - 273.15;
        double maxtemperatureInCelsius = tempMax - 273.15;
        // Format the temperature to have one digit after the decimal point
        String formattedTemperature = String.format("%.1f", temperatureInCelsius);
        String formattedFeelsLike = String.format("%.1f", feelslikeInCelsius);
        String formattedTempMin = String.format("%.1f", mintemperatureInCelsius);
        String formattedTempMax = String.format("%.1f", maxtemperatureInCelsius);
        String formattedWindSpeed = String.format("%.1f", windSpeed);

        // Set data to respective labels
        controller.tfCityAndCountryName.setText(cityName + ", " + countryName);
        controller.tfCurrentTemp.setText(formattedTemperature + " °C");
        controller.tfCurrentWeatherConditon.setText(weatherCondition);
        Image image = new Image(weatherIconURL);
        controller.tfCurrentWeatherIcon.setImage(image);
        controller.tfLatitude.setText(Double.toString(lat));
        controller.tfLongitude.setText(Double.toString(lon));
        controller.tfFeelsLike.setText(formattedFeelsLike + " °C");
        controller.tfHumidity.setText(Integer.toString(humidity) + " %");
        controller.tfMinimumTemp.setText(formattedTempMin + " °C");
        controller.tfMaximumTemp.setText(formattedTempMax + " °C");
        controller.tfSunrise.setText(formatTime(sunrise, timezone));
        controller.tfSunset.setText(formatTime(sunset, timezone));
        controller.tfPressure.setText(Integer.toString(pressure) + " hPa");
        controller.tfWindspeed.setText(formattedWindSpeed + " m/s");
        setcityname(cityName);
        setlong(lon);
        setlatitude(lat);
    }

    void setcityname(String cityName) {
        this.city = cityName;
    }

    void setlong(Double longi) {
        this.longitude = longi;
    }

    void setlatitude(Double lati) {
        this.latitude = lati;
    }

    double getlat() {
        return this.latitude;
    }

    double getlong() {
        return this.longitude;
    }

    String getcityname() {
        return this.city;
    }

    // Method to format time in hh:mm format
    private String formatTime(int timeInSeconds, int timezone) {
        // Convert sunrise and sunset from Unix timestamp to LocalDateTime
        LocalDateTime sunriseTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timeInSeconds),
                ZoneOffset.ofTotalSeconds(timezone));

        // Define the date time formatter for AM/PM format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");

        // Format sunrise and sunset times to AM/PM format
        String Sunrise = sunriseTime.format(formatter);
        return Sunrise;
    }

    public void updateForecast(WeatherForecastAPIData DataObj) {
        double[][] data = DataObj.getData();
        String[] iconUrls = DataObj.getIconUrls();
        String[] weatherConditions = DataObj.getWeatherCondition();
        // Update UI with forecast data for each day
        for (int i = 0; i < 5; i++) {
            // double temperature = data[i][0];
            String weatherCondition = weatherConditions[i];
            String iconUrl = iconUrls[i];
            double minTemperature = data[i][1];
            double maxTemperature = data[i][2];

            // Update UI for the ith day
            updateForecastForDay(i, minTemperature, maxTemperature, weatherCondition, iconUrl);
        }
    }

    // Method to update forecast data for a specific day in UI
    public void updateForecastForDay(int dayIndex, double minTemperature, double maxTemperature,
            String weatherCondition,
            String iconUrl) {
        // Update UI labels for the respective day
        switch (dayIndex) {
            case 0:
                tfDay1.setText(getDayName(dayIndex)); // Update day label
                tfDay1MinTemp.setText(formatTemperature(minTemperature) + " °C"); // Update minimum temperature label
                tfDay1MaxTemp.setText(formatTemperature(maxTemperature) + " °C"); // Update maximum temperature label
                tfDay1WeatherConditon.setText(weatherCondition); // Update weather condition label
                tfDay1Icon.setImage(new Image(iconUrl)); // Update weather icon
                break;
            case 1:
                tfDay2.setText(getDayName(dayIndex));
                tfDay2MinTemp.setText(formatTemperature(minTemperature) + " °C");
                tfDay2MaxTemp.setText(formatTemperature(maxTemperature) + " °C");
                tfDay2WeatherConditon.setText(weatherCondition);
                tfDay2Icon.setImage(new Image(iconUrl));
                break;
            case 2:
                tfDay3.setText(getDayName(dayIndex));
                tfDay3MinTemp.setText(formatTemperature(minTemperature) + " °C");
                tfDay3MaxTemp.setText(formatTemperature(maxTemperature) + " °C");
                tfDay3WeatherConditon.setText(weatherCondition);
                tfDay3Icon.setImage(new Image(iconUrl));
                break;
            case 3:
                tfDay4.setText(getDayName(dayIndex));
                tfDay4MinTemp.setText(formatTemperature(minTemperature) + " °C");
                tfDay4MaxTemp.setText(formatTemperature(maxTemperature) + " °C");
                tfDay4WeatherConditon.setText(weatherCondition);
                tfDay4Icon.setImage(new Image(iconUrl));
                break;
            case 4:
                tfDay5.setText(getDayName(dayIndex));
                tfDay5MinTemp.setText(formatTemperature(minTemperature) + " °C");
                tfDay5MaxTemp.setText(formatTemperature(maxTemperature) + " °C");
                tfDay5WeatherConditon.setText(weatherCondition);
                tfDay5Icon.setImage(new Image(iconUrl));
                break;
            default:
                break;
        }
    }

    // Method to get day name based on day index (0 for today, 1 for tomorrow, etc.)
    private String getDayName(int dayIndex) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Add the day index to the current date to get the desired date
        LocalDate desiredDate = currentDate.plusDays(dayIndex + 1);

        // Get the localized name of the day
        Locale locale = Locale.getDefault(); // Use default locale
        String dayName = desiredDate.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);

        return dayName;
    }

    // Method to format temperature
    private String formatTemperature(double temperature) {
        double temperatureInCelsius = temperature - 273.15;

        // Format the temperature to have one digit after the decimal point
        String formattedTemperature = String.format("%.1f", temperatureInCelsius);
        return formattedTemperature;
    }
}
