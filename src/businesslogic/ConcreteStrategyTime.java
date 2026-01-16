package businesslogic;
import model.Client;
import model.Server;
import java.util.List;

public class ConcreteStrategyTime implements Strategy {
    @Override
    public void addClient(List<Server> servers, Client client) {
        Server selectedServer = null;
        int minWaitingTime = Integer.MAX_VALUE;

        for (Server server : servers) {
            if (!server.isFull() && server.getWaitingPeriod() < minWaitingTime) {
                minWaitingTime = server.getWaitingPeriod();
                selectedServer = server;
            }
        }

        if (selectedServer != null) {
            selectedServer.addClient(client);
        }
    }
}

