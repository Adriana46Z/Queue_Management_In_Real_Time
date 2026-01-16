package businesslogic;
import model.Client;
import model.Server;
import java.util.List;

public class ConcreteStrategyQueue implements Strategy {
    @Override
    public void addClient(List<Server> servers, Client client) {
        Server selectedServer = null;
        int minQueueSize = Integer.MAX_VALUE;

        for (Server server : servers) {
            if (!server.isFull() && server.getClientsCount() < minQueueSize) {
                minQueueSize = server.getClientsCount();
                selectedServer = server;
            }
        }

        if (selectedServer != null) {
            selectedServer.addClient(client);
        }
    }
}

