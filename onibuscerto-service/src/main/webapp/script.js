var map;
var contextMenu;
var clickedLatLng;
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
    $("#loading")
        .hide()
        .ajaxStart(function() {
            $("input:button").hide();
            $(this).show();
        })
        .ajaxStop(function() {
            $(this).hide();
            $("input:button").show();
        });
}

function setupMapWidget() {
    var mapOptions = {
        zoom: 15,
        center: new google.maps.LatLng(36.90773, -116.76477),
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("right"), mapOptions);

    contextMenu = $(document.createElement('ul')).attr('id', 'contextMenu');
    contextMenu.append(
        '<li><a href="#setStart">Partir daqui</a></li>' +
        '<li><a href="#setEnd">Chegar aqui</a></li>'
    );
    contextMenu.bind('contextmenu', function() {return false;});
    $(map.getDiv()).append(contextMenu);

    google.maps.event.addListener(map, 'rightclick', function(evt) {
        contextMenu.hide();

        var mapDiv = $(map.getDiv()), x = evt.pixel.x, y = evt.pixel.y;

        clickedLatLng = evt.latLng;

        if (x > mapDiv.width() - contextMenu.width())
            x -= contextMenu.width();
        if (y > mapDiv.height() - contextMenu.height())
            y -= contextMenu.height();

        contextMenu.css({top: y, left: x}).fadeIn(100);
    });

    contextMenu.find('a').click(function() {
        contextMenu.fadeOut(75);

        var action = $(this).attr('href').substr(1);
        switch(action) {
            case "setStart":
                clearMap();
                setStartMarker(clickedLatLng);
                if (endMarker) {
                    endMarker.setMap(map);
                }
                lat1 = clickedLatLng.lat();
                lng1 = clickedLatLng.lng();
                break;

            case "setEnd":
                setEndMarker(clickedLatLng);
                if (startMarker) {
                    startMarker.setMap(map);
                }
                lat1 = clickedLatLng.lat();
                lng1 = clickedLatLng.lng();
                break;
        }

        return false;
    });

    contextMenu.find('a').hover(function() {
        $(this).parent().addClass('hover');
    }, function() {
        $(this).parent().removeClass('hover');
    });

    $.each('click dragstart zoom_changed maptypeid_changed'.split(' '), function(i, name){
        google.maps.event.addListener(map, name, function(){ contextMenu.hide() });
    });
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

    setStartMarker(new google.maps.LatLng(start.latitude, start.longitude));
    setEndMarker(new google.maps.LatLng(end.latitude, end.longitude));
}

function setStartMarker(position) {
    startMarker = new google.maps.Marker({
        map: map,
        position: position,
        icon: "img/start.png"
    });
}

function setEndMarker(position) {
    endMarker = new google.maps.Marker({
        map: map,
        position: position,
        icon: "img/end.png"
    });
}