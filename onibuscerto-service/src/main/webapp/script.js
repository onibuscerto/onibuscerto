var map;

$(document).ready(function() {
    setupUI();
    setupMapWidget();
    setupAutoComplete();
    setupCallbacks();
});

function setupUI() {
    $("input:button").button();
}

function setupMapWidget() {
    var mapOptions = {
        zoom: 15,
        center: new google.maps.LatLng(36.90773, -116.76477),
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("right"), mapOptions);
}

function setupAutoComplete() {
    var availableStops = [
        "FUR_CREEK_RES",
        "BEATTY_AIRPORT",
        "BULLFROG",
        "STAGECOACH",
        "NADAV",
        "NANAA",
        "DADAN",
        "EMSI",
        "AMV",
    ];
    $("#start").autocomplete({ source: availableStops });
    $("#end").autocomplete({ source: availableStops });
}

function setupCallbacks() {
    $("#route").click(function() {
        // TODO: obter o caminho do service e mostrar no mapa
        /*$.get("route", function(data) {
            new google.maps.Marker({
                position: new google.maps.LatLng(data.latitude, data.longitude),
                map: map, 
                title: "Hello World!"
            });   
        }, "json");*/
    });
}