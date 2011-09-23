package com.onibuscerto.service.apps;

import com.google.sitebricks.At;
import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Post;
import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.Connection;
import com.onibuscerto.api.entities.Location;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.TransportConnection;
import com.onibuscerto.api.entities.Trip;
import com.onibuscerto.api.entities.WalkingConnection;
import com.onibuscerto.api.factories.ConnectionFactory;
import com.onibuscerto.api.utils.GlobalPosition;
import com.onibuscerto.api.utils.QueryResponseConnection;
import java.util.Collection;
import java.util.LinkedList;

@Service
@At("/route")
public class RouteApp {

    private GlobalPosition start = new GlobalPosition(0, 0);
    private GlobalPosition end = new GlobalPosition(0, 0);

    private double distance(double lat1, double lng1, double lat2, double lng2) {
        double ans, theta;

        lat1 *= Math.PI / 180.0;
        lng1 *= Math.PI / 180.0;
        lat2 *= Math.PI / 180.0;
        lng2 *= Math.PI / 180.0;

        theta = lng1 - lng2;
        ans = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(theta);
        ans = Math.acos(ans) * 180.0 / Math.PI;
        ans = ans * 60 * 1.1515 * 1609.344;

        return ans;
    }

    @Post
    public Reply<Collection<QueryResponseConnection>> post() {
        DatabaseController databaseController = new DatabaseController();
        databaseController.beginTransaction();

        // Cria nós de origem e destino
        Location srcNode = databaseController.getLocationFactory().createLocation();
        Location tgtNode = databaseController.getLocationFactory().createLocation();

        srcNode.setLatitude(start.getLatitude());
        srcNode.setLongitude(start.getLongitude());

        tgtNode.setLatitude(end.getLatitude());
        tgtNode.setLongitude(end.getLongitude());

        // Conecta nós de origem e destino com todas as stops
        ConnectionFactory connectionFactory = databaseController.getConnectionFactory();
        for (Stop stop : databaseController.getLocationFactory().getAllStops()) {
            WalkingConnection wc1 = connectionFactory.createWalkingConnection(srcNode, stop);
            double d1 = distance(srcNode.getLatitude(), srcNode.getLongitude(),
                    stop.getLatitude(), stop.getLongitude());
            wc1.setWalkingDistance(d1);

            WalkingConnection wc2 = connectionFactory.createWalkingConnection(stop, tgtNode);
            double d2 = distance(stop.getLatitude(), stop.getLongitude(),
                    tgtNode.getLatitude(), tgtNode.getLongitude());
            wc2.setWalkingDistance(d2);
        }

        // Conecta a origem com o destino também
        WalkingConnection wc = connectionFactory.createWalkingConnection(srcNode, tgtNode);
        double d3 = distance(srcNode.getLatitude(), srcNode.getLongitude(), tgtNode.getLatitude(), tgtNode.getLongitude());
        wc.setWalkingDistance(d3);

        int departureTime = 9*60*60;

        if (srcNode == null || tgtNode == null) {
            // WTF, o lugar não existe
            databaseController.endTransaction(false);
            databaseController.close();
            throw new RuntimeException("Fudeu, o lugar não existe.");
            //return null;
        }

        // Encontra o caminho e converte pra uma Collection de GlobalPositions
        Collection<Connection> path = databaseController.getShortestPath(srcNode, tgtNode, departureTime);
        Collection<QueryResponseConnection> ret = new LinkedList<QueryResponseConnection>();

        if (path == null) {
            // WTF, não tem caminho!
            databaseController.endTransaction(false);
            databaseController.close();
            throw new RuntimeException("WTF não tem caminho!");
            //return null;
        }

        for (Connection connection : path) {
            QueryResponseConnection qrc = new QueryResponseConnection();
            qrc.setStart(new GlobalPosition(connection.getSource().getLatitude(),
                    connection.getSource().getLongitude()));
            qrc.setEnd(new GlobalPosition(connection.getTarget().getLatitude(),
                    connection.getTarget().getLongitude()));

            if (connection instanceof WalkingConnection) {
                qrc.setRouteType(-1);
            } else {
                TransportConnection transportConnection = (TransportConnection) connection;
                Trip trip = databaseController.getTripFactory().getTripById(transportConnection.getTripId());

                if (trip.getRoute().hasRouteColor()) {
                    qrc.setRouteColor(trip.getRoute().getRouteColor());
                }

                qrc.setDepartureTime(transportConnection.getDepartureTime());
                qrc.setStartStopName(((Stop) transportConnection.getSource()).getName());
                qrc.setRouteType(trip.getRoute().getType().toInt());
                qrc.setRouteLongName(trip.getRoute().getLongName());
            }

            ret.add(qrc);
        }

        // Faz um rollback da transação
        databaseController.endTransaction(false);
        databaseController.close();

        return Reply.with(ret).as(Json.class).type("application/json");
    }

    public GlobalPosition getEnd() {
        return end;
    }

    public void setEnd(GlobalPosition end) {
        this.end = end;
    }

    public GlobalPosition getStart() {
        return start;
    }

    public void setStart(GlobalPosition start) {
        this.start = start;
    }
}
