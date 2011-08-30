var map;
var polyline;
var bounds;
var startMarker;
var endMarker;

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
        data = {
            source: $("input#start").val(),
            target: $("input#end").val()
        };

        $.post("/route", data, function(response) {
            clearMap();
            addMapPath(response);
            addMapMarkers(response);
            map.fitBounds(bounds);
        }, "json");
    });
}

function clearMap() {
    if (polyline) {
        polyline.setMap(null);
    }

    if (startMarker) {
        startMarker.setMap(null);
    }

    if (endMarker) {
        endMarker.setMap(null);
    }

    bounds = new google.maps.LatLngBounds();
}

function addMapPath(response) {
    var path = [];

    for (var i = 0; i < response.length; i++) {
        var pos = response[i];
        var latlng = new google.maps.LatLng(pos.latitude, pos.longitude);
        path.push(latlng);
        bounds.extend(latlng);
    }

    polyline = new google.maps.Polyline({
        map: map,
        path: path,
        strokeColor: "#0000CC",
        opacity: 0.4
    });
}

function addMapMarkers(response) {
    var start = response[0];
    var end = response[response.length-1];

    startMarker = new google.maps.Marker({
        map: map,
        position: new google.maps.LatLng(start.latitude, start.longitude),
        icon: "img/start.png"
    });

    endMarker = new google.maps.Marker({
        map: map,
        position: new google.maps.LatLng(end.latitude, end.longitude),
        icon: "img/end.png"
    });
}