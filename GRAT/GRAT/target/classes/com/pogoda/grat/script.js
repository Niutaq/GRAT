const options = {
    key: "your Windy's API key",
    //key: 'xur4F21CzYIYf70appBnjQCFVrbhwcP2',

    verbose: true,

    lat: 50.040341912270925,
    lon: 21.99914443307795,
    zoom: 10,
};

windyInit(options, windyAPI => {
    const { map } = windyAPI;
    map.removeControl();
    L.popup()
        .setLatLng([50.040341912270925, 21.99914443307795])
        .openOn(map);
    map.attributionControl.setPrefix(false);
});