package com.onibuscerto.service.servlets;

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
import flexjson.JSONSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RouteServlet extends HttpServlet {

    private DatabaseController databaseController;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        databaseController = new DatabaseController();
    }

    @Override
    public void destroy() {
        super.destroy();
        databaseController.close();
    }

    protected synchronized Collection<QueryResponseConnection> runQuery(GlobalPosition start,
            GlobalPosition end, int departureTime) {
        Collection<QueryResponseConnection> ret = new LinkedList<QueryResponseConnection>();
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
            double d1 = start.getDistanceTo(stop.getGlobalPosition());
            wc1.setWalkingDistance(d1);

            WalkingConnection wc2 = connectionFactory.createWalkingConnection(stop, tgtNode);
            double d2 = stop.getGlobalPosition().getDistanceTo(end);
            wc2.setWalkingDistance(d2);
        }

        // Conecta a origem com o destino também
        WalkingConnection wc = connectionFactory.createWalkingConnection(srcNode, tgtNode);
        double d3 = start.getDistanceTo(end);
        wc.setWalkingDistance(d3);

        // Encontra o caminho e converte pra uma Collection de GlobalPositions
        Collection<Connection> path = databaseController.getShortestPath(srcNode, tgtNode, departureTime);

        // Monta o objeto contendo a resposta da consulta
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
        return ret;
    }

    protected void doResponse(HttpServletResponse response, Collection<QueryResponseConnection> path)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            new JSONSerializer().serialize(path, out);
        } finally {
            out.close();
        }
    }
    
    protected GlobalPosition stringToGlobalPosition(String lat, String lng) {
        return new GlobalPosition(Double.parseDouble(lat), Double.parseDouble(lng));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Collection<QueryResponseConnection> path;

        // Obtém os parâmetros da consulta passados por POST
        GlobalPosition start = stringToGlobalPosition(
                request.getParameter("start.latitude"),
                request.getParameter("start.longitude"));
        GlobalPosition end = stringToGlobalPosition(
                request.getParameter("end.latitude"),
                request.getParameter("end.longitude"));
        String spDeparture[] = request.getParameter("departure").split(":");
        int departureTime = Integer.parseInt(spDeparture[0])*3600 + Integer.parseInt(spDeparture[1])*60;

        // Executa a consulta e imprime o JSON no PrintWriter do Servlet
        path = runQuery(start, end, departureTime);
        doResponse(response, path);
    }
}
