module com.pogoda.grat {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires javafx.web;
    requires google.maps.services;


    opens com.pogoda.grat to javafx.fxml;
    exports com.pogoda.grat;
}