var map;
var geocoder;
var contextMenu;
var clickedLatLng;
var polylines = [];
var bounds;
var startMarker;
var endMarker;

var lat1, lng1;
var lat2, lng2;

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
        zoom: 12,
        center: new google.maps.LatLng(37.765015, -122.347183),
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("right"), mapOptions);

    contextMenu = $(document.createElement('ul')).attr('id', 'contextMenu');
    contextMenu.append(
        '<li><a href="#setStart">Partir daqui</a></li>' +
        '<li><a href="#setEnd">Chegar aqui</a></li>'
        );
    contextMenu.bind('contextmenu', function() {
        return false;
    });
    $(map.getDiv()).append(contextMenu);

    google.maps.event.addListener(map, 'rightclick', function(evt) {
        contextMenu.hide();

        var mapDiv = $(map.getDiv()), x = evt.pixel.x, y = evt.pixel.y;

        clickedLatLng = evt.latLng;

        if (x > mapDiv.width() - contextMenu.width())
            x -= contextMenu.width();
        if (y > mapDiv.height() - contextMenu.height())
            y -= contextMenu.height();

        contextMenu.css({
            top: y,
            left: x
        }).fadeIn(100);
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
                runQuery();
                geocoder.geocode({ 'latLng': startMarker.getPosition() }, function(results, status) {
                    if (status == google.maps.GeocoderStatus.OK) {
                        if (results[0]) {
                            $("#start").val(results[0].formatted_address);
                        }
                    }
                });
                break;

            case "setEnd":
                clearMap();
                setEndMarker(clickedLatLng);
                if (startMarker) {
                    startMarker.setMap(map);
                }
                lat2 = clickedLatLng.lat();
                lng2 = clickedLatLng.lng();
                runQuery();
                geocoder.geocode({ 'latLng': endMarker.getPosition() }, function(results, status) {
                    if (status == google.maps.GeocoderStatus.OK) {
                        if (results[0]) {
                            $("#end").val(results[0].formatted_address);
                        }
                    }
                });
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
        google.maps.event.addListener(map, name, function(){
            contextMenu.hide()
        });
    });
}

function setupAutoComplete() {
    geocoder = new google.maps.Geocoder();
    // TODO: boilerplate code!
    $("#start").autocomplete({
        source: function(request, response) {
            geocoder.geocode({'address': request.term}, function(results, status) {
                response($.map(results, function(item) {
                    return {
                        label: item.formatted_address,
                        value: item.formatted_address,
                        latitude: item.geometry.location.lat(),
                        longitude: item.geometry.location.lng()
                    }
                }));
            });
        },
        select: function(event, ui) {
            lat1 = ui.item.latitude;
            lng1 = ui.item.longitude;
            clearMap();
            var latlng = new google.maps.LatLng(lat1, lng1);
            setStartMarker(latlng);
            if (endMarker) {
                endMarker.setMap(map);
            }
            runQuery();
        }
    });
    $("#end").autocomplete({
        source: function(request, response) {
            geocoder.geocode({'address': request.term}, function(results, status) {
                response($.map(results, function(item) {
                    return {
                        label: item.formatted_address,
                        value: item.formatted_address,
                        latitude: item.geometry.location.lat(),
                        longitude: item.geometry.location.lng()
                    }
                }));
            });
        },
        select: function(event, ui) {
            lat2 = ui.item.latitude;
            lng2 = ui.item.longitude;
            clearMap();
            var latlng = new google.maps.LatLng(lat2, lng2);
            setEndMarker(latlng);
            if (startMarker) {
                startMarker.setMap(map);
            }
            runQuery();
        }
    });
}

function setupCallbacks() {
    $("#route").click(function() {
        runQuery();
    });
}

function runQuery() {
    if (!startMarker || !endMarker) {
        return;
    }

    data = {
        "start.latitude": lat1,
        "start.longitude": lng1,
        "end.latitude": lat2,
        "end.longitude": lng2
    };

    $.post("/route", data, function(response) {
        clearMap();
        addMapPath(response);
        addMapMarkers(response);
        map.fitBounds(bounds);
    }, "json");
}

function clearMap() {
    if (polylines) {
        for (var i in polylines) {
            polylines[i].setMap(null);
        }
    }

    if (startMarker) {
        startMarker.setMap(null);
    }

    if (endMarker) {
        endMarker.setMap(null);
    }

    $("#result li").remove();

    bounds = new google.maps.LatLngBounds();
}

function addMapPath(response) {
    for (var i = 0; i < response.length; i++) {
        var pos1 = response[i].start;
        var pos2 = response[i].end;
        var latlng1 = new google.maps.LatLng(pos1.latitude, pos1.longitude);
        var latlng2 = new google.maps.LatLng(pos2.latitude, pos2.longitude);
        var path = [];
        var scolor = response[i].routeType == -1 ? "#000000" : "#0000CC";

        path.push(latlng1);
        path.push(latlng2);

        polylines.push(new google.maps.Polyline({
            map: map,
            path: path,
            strokeColor: scolor,
            strokeOpacity: 0.75,
            strokeWeight: 4
        }));

        bounds.extend(latlng1);
        bounds.extend(latlng2);
    }
}

function addMapMarkers(response) {
    var start = response[0].start;
    var end = response[response.length-1].end;

    setStartMarker(new google.maps.LatLng(start.latitude, start.longitude));
    setEndMarker(new google.maps.LatLng(end.latitude, end.longitude));
}

function setStartMarker(position) {
    startMarker = new google.maps.Marker({
        map: map,
        position: position,
        icon: "img/start.png",
        draggable: true
    });

    google.maps.event.addListener(startMarker, 'dragend', function() {
        clearMap();
        startMarker.setMap(map);
        if (endMarker) {
            endMarker.setMap(map);
        }
        lat1 = startMarker.getPosition().lat();
        lng1 = startMarker.getPosition().lng();
        runQuery();

        geocoder.geocode({ 'latLng': startMarker.getPosition() }, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                if (results[0]) {
                    $("#start").val(results[0].formatted_address);
                }
            }
        });
    });
}

function setEndMarker(position) {
    endMarker = new google.maps.Marker({
        map: map,
        position: position,
        icon: "img/end.png",
        draggable: true
    });

    google.maps.event.addListener(endMarker, 'dragend', function() {
        clearMap();
        endMarker.setMap(map);
        if (startMarker) {
            startMarker.setMap(map);
        }
        lat2 = endMarker.getPosition().lat();
        lng2 = endMarker.getPosition().lng();
        runQuery();

        geocoder.geocode({ 'latLng': endMarker.getPosition() }, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                if (results[0]) {
                    $("#end").val(results[0].formatted_address);
                }
            }
        });
    });
}
