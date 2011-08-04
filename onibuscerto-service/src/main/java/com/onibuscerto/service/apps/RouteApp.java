package com.onibuscerto.service.apps;

import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.service.utils.GlobalPosition;
import java.util.Collection;
import java.util.LinkedList;

@Service
@At("/route/:source/:target")
public class RouteApp {

    @Get
    public Reply<Collection<GlobalPosition>> get(
            @Named("source") String source,
            @Named("target") String target) {
        DatabaseController databaseController = new DatabaseController();
        databaseController.beginTransaction();

        // Encontra a Stop de origem e a de destino
        Stop srcNode = databaseController.getStopFactory().getStopById(source);
        Stop tgtNode = databaseController.getStopFactory().getStopById(target);
        int departureTime = 0;

        if (srcNode == null || tgtNode == null) {
            // WTF, o lugar não existe
            databaseController.endTransaction(false);
            databaseController.close();
            return null;
        }

        // Encontra o caminho e converte pra uma Collection de GlobalPositions
        Collection<Stop> path = databaseController.getShortestPath(srcNode, tgtNode, departureTime);
        Collection<GlobalPosition> ret = new LinkedList<GlobalPosition>();
        for (Stop stop : path) {
            ret.add(new GlobalPosition(stop.getLatitude(), stop.getLongitude()));
        }

        // Faz um rollback da transação
        databaseController.endTransaction(false);
        databaseController.close();

        return Reply.with(ret).as(Json.class).type("application/json");
    }
}
