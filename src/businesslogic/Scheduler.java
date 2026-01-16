package businesslogic;

import model.Client;
import model.Server;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {
    public enum SelectionPolicy {
        SHORTEST_QUEUE,
        SHORTEST_TIME
    }
    private List<Server> servers;
    private int maxNoServers;
    private int maxClientsPerServer;
    private Strategy strategy;
    private ExecutorService executorService;

    public Scheduler(int maxNoServers, int maxClientsPerServer) {
        if (maxNoServers <= 0 || maxClientsPerServer <= 0) {
            throw new IllegalArgumentException("Number of servers and clients per server must be positive");
        }

        this.maxNoServers = maxNoServers;
        this.maxClientsPerServer = maxClientsPerServer;
        this.servers = new ArrayList<>(maxNoServers);
        this.executorService = Executors.newFixedThreadPool(maxNoServers);

        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server(maxClientsPerServer);
            servers.add(server);
            executorService.execute(server);
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("Policy cannot be null");
        }

        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ConcreteStrategyQueue();
        }
        else if (policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new ConcreteStrategyTime();
        }
    }

    public void dispatchClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (strategy == null) {
            throw new IllegalStateException("Strategy not initialized");
        }
        strategy.addClient(servers, client);
    }

    public List<Server> getServers() {
        return new ArrayList<>(servers);
    }

    public void shutdown() {
        for (Server server : servers) {
            server.stopRunning();
        }
        executorService.shutdown();
    }
}