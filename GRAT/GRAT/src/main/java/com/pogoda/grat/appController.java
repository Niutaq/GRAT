package com.pogoda.grat;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class appController {

    // IDs

    @FXML
    private TextField city;

    @FXML
    private Button getData;

    @FXML
    private Text temp;

    @FXML
    private Text temp_max;

    @FXML
    private Text temp_min;

    @FXML
    private Text temp_feels;

    @FXML
    private Text pressure;

    @FXML
    private Text humidity;

    @FXML
    private Text sunrise;

    @FXML
    private Text sunset;

    @FXML
    private Label time;

    @FXML
    private ImageView weather_condition;

    @FXML
    private ImageView rain;

    @FXML
    private ImageView snow;

    @FXML
    private Button hist;

    @FXML
    private Text co;

    @FXML
    private Text no2;

    @FXML
    private Text o3;

    @FXML
    private Text so2;

    // Initialization
    @FXML
    public void initialize() {

        // Timer
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0),
                event -> {
                    LocalTime NOW = LocalTime.now();
                    time.setText(NOW.format(dateFormat));
                }
        ), new KeyFrame(Duration.seconds(1)));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // Search history
        List<String> histo = new ArrayList<>();

        hist.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Historia wyszukiwania:");
            alert.setHeaderText("Lista miast:");
            alert.setContentText(String.join("\n", histo ));
            alert.showAndWait();
        });

        // Get city's name
        getData.setOnAction(event -> {
            String CITY = city.getText().trim();
            if (!CITY.equals("")) {

                histo.add(CITY);

                // Coordinates for city
                String coords = getUrlContent("https://api.openweathermap.org/geo/1.0/direct?q=" + CITY + "&limit=1&appid= your OWM API key ");

                JSONArray coordsList = new JSONArray(coords.toString());
                JSONObject coordsCity = coordsList.getJSONObject(0);
                double lat = coordsCity.getDouble("lat");
                double lon = coordsCity.getDouble("lon");

                // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                String output = getUrlContent("http://api.openweathermap.org/data/2.5/weather?q=" + CITY +"&appid= your OWM API key ");
                // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                String output2 = getUrlContent("http://api.openweathermap.org/data/2.5/air_pollution?lat=" + lat + "&lon=" + lon + "&appid= your OWM API key ");

                JSONObject obj2 = new JSONObject(output2);
                JSONArray pollList = obj2.getJSONArray("list");
                JSONObject pollObj =  pollList.getJSONObject(0);
                JSONObject components = pollObj.getJSONObject("components");

                // CO
                Double CO = components.getDouble("co");
                co.setText(CO.toString());
                // NO2
                Double NO2 = components.getDouble("no2");
                no2.setText(NO2.toString());
                // O3
                Double O3 = components.getDouble("o3");
                o3.setText(O3.toString());
                // SO2
                Double SO2 = components.getDouble("so2");
                so2.setText(SO2.toString());



                if (!output.isEmpty()) {
                    JSONObject obj = new JSONObject(output);

                    // Wschód i Zachód Słońca
                    JSONObject sys = obj.getJSONObject("sys");

                    long sr = sys.getLong("sunrise") * 1000;
                    long ss = sys.getLong("sunset") * 1000;

                    String srTime = new SimpleDateFormat("HH:mm").format(new Date(sr));
                    String ssTime = new SimpleDateFormat("HH:mm").format(new Date(ss));

                    sunrise.setText(srTime);
                    sunset.setText(ssTime);

                    LocalTime t = LocalTime.now();
                    LocalTime srTimeComp = LocalTime.parse(srTime);
                    LocalTime ssTimeComp = LocalTime.parse(ssTime);

                    // Weather
                    JSONArray weatherList = obj.getJSONArray("weather");
                    JSONObject weatherObj =  weatherList.getJSONObject(0);
                    String weather = weatherObj.getString("main");
                    String weatherExact = weatherObj.getString("description");

                    // Temperature
                    double tempe = obj.getJSONObject("main").getDouble("temp") - 273.00;
                    tempe = Double.parseDouble(String.format("%.2f\n", tempe).replace(",", "."));

                    // Rain and snow
                    // Rainfall

                    rain.setOpacity(0);
                    snow.setOpacity(0);
                    if(weather.equals("Rain")) {
                        Image rainn = new Image(getClass().getResourceAsStream("images/rain_icon.png"));
                        temp.setText("Temperature: " + tempe + "°C");
                        weather_condition.setImage(rainn);
                        rain.setOpacity(0.73);
                    }
                    // Light snowfall
                    else if(weather.equals("Snow") && weatherExact.equals("light snow")) {
                        Image snoww = new Image(getClass().getResourceAsStream("images/snow_icon.png"));
                        temp.setText("Temperature: " + tempe + "°C");
                        weather_condition.setImage(snoww);
                        snow.setOpacity(0.06);
                    }
                    // Snowfall
                    else if(weather.equals("Snow") && weatherExact.equals("snow")) {
                        Image snoww = new Image(getClass().getResourceAsStream("images/snow2_icon.png"));
                        temp.setText("Temperature: " + tempe + "°C");
                        weather_condition.setImage(snoww);
                        snow.setOpacity(0.06);
                    }
                    // Heavy snowfall
                    else if(weather.equals("Snow") && weatherExact.equals("snow")) {
                        Image snoww = new Image(getClass().getResourceAsStream("images/snow3_icon.png"));
                        temp.setText("Temperature: " + tempe + "°C");
                        weather_condition.setImage(snoww);
                        snow.setOpacity(0.06);
                    }
                    // Slightly cloudy and Sun or Moon
                    else if (weather.equals("Clouds") && weatherExact.equals("few clouds")) {
                                if(t.isAfter(srTimeComp) && t.isBefore(ssTimeComp)) {
                            Image clouds = new Image(getClass().getResourceAsStream("images/clouds_sun_icon.png"));
                            temp.setText("Temperature: " + tempe + "°C");
                            weather_condition.setImage(clouds);
                        } else {
                            Image clouds = new Image(getClass().getResourceAsStream("images/clouds_moon_icon.png"));
                            temp.setText("Temperature: " + tempe + "°C");
                            weather_condition.setImage(clouds);
                        }
                    }
                    // Moderate cloudiness
                    else if(weather.equals("Clouds") && weatherExact.equals("scattered clouds")) {
                        Image clouds = new Image(getClass().getResourceAsStream("images/clouds2_icon.png"));
                        temp.setText("Temperature: " + tempe + "°C");
                        weather_condition.setImage(clouds);
                    }
                     // Moderate/Heavy cloudiness
                    else if(weather.equals("Clouds") && weatherExact.equals("broken clouds")) {
                        Image clouds = new Image(getClass().getResourceAsStream("images/clouds3_icon.png"));
                        temp.setText("Temperature: " + tempe + "°C");
                        weather_condition.setImage(clouds);
                    }
                    // Heavy cloudiness
                    else if(weather.equals("Clouds") && weatherExact.equals("overcast clouds")) {
                        Image clouds = new Image(getClass().getResourceAsStream("images/clouds4_icon.png"));
                        temp.setText("Temperature: " + tempe + "°C");
                        weather_condition.setImage(clouds);
                    }
                    // Drizzle
                    else if(weather.equals("Drizzle")) {
                        Image drizzle = new Image(getClass().getResourceAsStream("images/drizzle_icon.png"));
                        temp.setText("Temperatura: " + tempe + "°C");
                        weather_condition.setImage(drizzle);
                    }
                    // Thunderstorm
                    else if(weather.equals("Thunderstorm")) {
                        Image thund = new Image(getClass().getResourceAsStream("images/thunderstorm_icon.png"));
                        temp.setText("Temperature: " + tempe + "°C");
                        weather_condition.setImage(thund);
                    }

                    // Sun - clear sky
                    if(t.isAfter(srTimeComp) && t.isBefore(ssTimeComp)) {
                        if(weather.equals("Clear") && weatherExact.equals("clear sky")) {
                        Image slonce = new Image(getClass().getResourceAsStream("images/sun_icon.png"));
                        temp.setText("Temperatura: " + tempe + "°C");
                        weather_condition.setImage(slonce);
                        }
                    }
                    // Moon - clear sky
                    else {
                        if(weather.equals("Clear") && weatherExact.equals("clear sky")) {
                        Image ksiezyc = new Image(getClass().getResourceAsStream("images/moon_icon.png"));
                        temp.setText("Temperature: " + tempe + "°C");
                        weather_condition.setImage(ksiezyc);
                        }
                    }

                    // Sensed temperature
                    double feels = obj.getJSONObject("main").getDouble("feels_like") - 273.00;
                    feels = Double.parseDouble(String.format("%.2f\n", feels).replace(",", "."));
                    temp_feels.setText("Sensed temperature: " + feels + "°C");

                    // Max. temperature
                    double max = obj.getJSONObject("main").getDouble("temp_max") - 273.00;
                    max = Double.parseDouble(String.format("%.2f\n", max).replace(",", "."));
                    temp_max.setText("Max. temperature: " + max + "°C");

                    // Min. temperature
                    double min = obj.getJSONObject("main").getDouble("temp_min") - 273.00;
                    min = Double.parseDouble(String.format("%.2f\n", min).replace(",", "."));
                    temp_min.setText("Min. temperature: " + min + "°C");

                    // Pressure
                    double pres = obj.getJSONObject("main").getDouble("pressure");
                    pres = Double.parseDouble(String.format("%.2f\n", pres).replace(",", "."));
                    pressure.setText("Pressure: " + pres + " hPa");

                    // Humidity
                    double humi = obj.getJSONObject("main").getDouble("humidity");
                    humi = Double.parseDouble(String.format("%.2f\n", humi).replace(",", "."));
                    humidity.setText("Humidity: " + humi + " %");
                }

            } else caution();

        });
    }

    public String getUrlContent(String urlAdress) {
        StringBuffer content = new StringBuffer();

        try {
            URL url = new URL(urlAdress);
            URLConnection urlConn = url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();

        } catch (Exception e) {
            caution2();
        }
        return content.toString();
    }

    public void caution() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Uwaga!");
        alert.setHeaderText("The search field is empty.");
        alert.showAndWait();
    }

    public void caution2() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Uwaga!");
        alert.setHeaderText("Invalid city name entered.");
        alert.showAndWait();
    }
}

