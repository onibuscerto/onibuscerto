package com.onibuscerto.service.apps;

import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Post;
import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.Connection;
import com.onibuscerto.api.entities.Location;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.service.utils.GlobalPosition;
import java.util.Collection;
import java.util.LinkedList;

@Service
@At("/route")
public class RouteApp {

    private String source;
    private String target;

    @Post
    public Reply<Collection<GlobalPosition>> post() {
        DatabaseController databaseController = new DatabaseController();
        databaseController.beginTransaction();

        // Encontra a Stop de origem e a de destino
        Stop srcNode = databaseController.getLocationFactory().getStopById(source);
        Stop tgtNode = databaseController.getLocationFactory().getStopById(target);
        int departureTime = 0;

        if (srcNode == null || tgtNode == null) {
            // WTF, o lugar não existe
            databaseController.endTransaction(false);
            databaseController.close();
            throw new RuntimeException("Fudeu, o lugar não existe.");
            //return null;
        }

        // Encontra o caminho e converte pra uma Collection de GlobalPositions
        Collection<Connection> path = databaseController.getShortestPath(srcNode, tgtNode, departureTime);
        Collection<GlobalPosition> ret = new LinkedList<GlobalPosition>();

        if (path == null) {
            // WTF, não tem caminho!
            databaseController.endTransaction(false);
            databaseController.close();
            throw new RuntimeException("WTF não tem caminho!");
            //return null;
        }

        for (Connection connection : path) {
            Location location = connection.getSource();
            ret.add(new GlobalPosition(location.getLatitude(), location.getLongitude()));
        }
        ret.add(new GlobalPosition(tgtNode.getLatitude(), tgtNode.getLongitude()));

        // Faz um rollback da transação
        databaseController.endTransaction(false);
        databaseController.close();

        return Reply.with(ret).as(Json.class).type("application/json");
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
