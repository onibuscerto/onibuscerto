var map;
var poly;

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
        $.get("route/"+$("input#start").val()+"/"+$("input#end").val(), function(data) {
            var path = [];

            for (var i = 0; i < data.length; i++) {
                var pos = data[i];
                path.push(new google.maps.LatLng(pos.latitude, pos.longitude));
            }

            if (poly) {
                poly.setMap(null);
            }

            poly = new google.maps.Polyline({
                path: path,
                strokeColor: "#0000CC",
                opacity: 0.4
            });
            poly.setMap(map);
        }, "json");
    });
}