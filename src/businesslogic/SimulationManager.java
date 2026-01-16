package businesslogic;

import gui.SimulationFrame;
import model.Server;
import model.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SimulationManager {
    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Client> clients;
    private List<Client> processedClients; //clientii ce au fost procesati
    private Scheduler.SelectionPolicy selectionPolicy;
    private boolean simulationRunning;

    private int numQueues = 1;
    private int numClients = 1;
    private int minArrival = 0;
    private int maxArrival = 0;
    private int minService = 0;
    private int maxService = 0;
    private int timeLimit = 0;
    private int currentTime = 0;

    public SimulationManager(SimulationFrame frame) {
        this.frame = frame;
        this.scheduler = new Scheduler(numQueues, 100);
        this.selectionPolicy = Scheduler.SelectionPolicy.SHORTEST_TIME;
        scheduler.changeStrategy(selectionPolicy);
        this.simulationRunning = false;
        this.processedClients = new ArrayList<>();
    }

    public void updateParameters(int numQueues, int numClients,
                                 int minArrival, int maxArrival,
                                 int minService, int maxService) {
        this.numQueues = numQueues;
        this.numClients = numClients;
        this.minArrival = minArrival;
        this.maxArrival = maxArrival;
        this.minService = minService;
        this.maxService = maxService;

        if(scheduler != null) {
            scheduler.shutdown();
        }

        this.scheduler = new Scheduler(numQueues, 100);
        scheduler.changeStrategy(selectionPolicy);
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void generateRandomClients() {
        clients = new ArrayList<>();
        processedClients = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < numClients; i++) {
            int arrival = rand.nextInt(maxArrival - minArrival + 1) + minArrival;
            int service = rand.nextInt(maxService - minService + 1) + minService;
            clients.add(new Client(i + 1, arrival, service));
        }

        Collections.sort(clients, new Comparator<Client>() {
            @Override
            public int compare(Client c1, Client c2) {
                return Integer.compare(c1.getArrivalTime(), c2.getArrivalTime());
            }
        });

        frame.showGeneratedClients(clients);
        frame.log("Clients generated with parameters:");
        frame.log("Arrival time range: [" + minArrival + ", " + maxArrival + "]");
        frame.log("Service time range: [" + minService + ", " + maxService + "]");
        frame.log("Number of queues: " + numQueues);
    }

    public void runSimulation() {
        frame.log("\n Simulation started \n");
        frame.log("Time limit: " + timeLimit + "\n");
        frame.log("Initial clients: " + clients.size() + "\n");

        simulationRunning = true;
        currentTime = 0;
        processedClients.clear();

        while (currentTime <= timeLimit && simulationRunning) {
            StringBuilder logEntry = new StringBuilder();
            logEntry.append("\n Time ").append(currentTime).append(" \n");

            for (Server server : scheduler.getServers()) {
                server.setCurrentTime(currentTime);
            }

            Iterator<Client> it = clients.iterator();
            while (it.hasNext()) {
                Client c = it.next();
                if (c.getArrivalTime() == currentTime) {
                    scheduler.dispatchClient(c);
                    it.remove();
                }
            }

            logEntry.append("Remaining waiting clients: ").append(clients.size()).append("\n");

            List<Server> servers = scheduler.getServers();
            for (int i = 0; i < servers.size(); i++) {
                Server s = servers.get(i);
                logEntry.append("Queue ").append(i + 1).append(": ").append(s.toString()).append("\n");
            }

            frame.log(logEntry.toString());

            if (isSimulationFinished(servers)) {
                frame.log("\nAll clients processed or time limit reached.\n");
                calculateAverageWaitingTime();
                break;
            }

            currentTime++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                simulationRunning = false;
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (currentTime > timeLimit) {
            calculateAverageWaitingTime();
        }

        frame.log("\n Simulation ended \n");
    }

    private boolean isSimulationFinished(List<Server> servers) {
        // verific dacă toți clienții au fost procesați
        if (clients.isEmpty()) {
            boolean allServersEmpty = true;
            for (Server server : servers) {
                if (!server.isEmpty()) {
                    allServersEmpty = false;
                    break;
                }
            }
            return allServersEmpty;
        }
        return false;
    }

    private void calculateAverageWaitingTime() {
        // toți clienții procesați de pe toate serverele

        List<Client> allProcessedClients = new ArrayList<>(processedClients);
        for (Server server : scheduler.getServers()) {
            allProcessedClients.addAll(server.getProcessedClients());
        }

        if (allProcessedClients.isEmpty()) {
            frame.log("No clients were processed completely.");
            return;
        }

        int totalWaitingTime = 0;
        int validClientCount = 0;

        for (Client client : allProcessedClients) {
            if (client.getLeaveQueueTime() != -1) {
                int waitingTime = client.getWaitingTime();
                if (waitingTime >= 0) {
                    totalWaitingTime += waitingTime;
                    validClientCount++;
                }
            }
        }
        if (validClientCount > 0) {
            double averageWaitingTime = (double) totalWaitingTime / validClientCount;
            frame.log("\n Average waiting time: " + String.format("%.2f", averageWaitingTime));
            frame.log("\n");
        } else {
            frame.log("No clients completed their service.");
        }
    }

    public void stopSimulation() {
        simulationRunning = false;
    }

    public void cleanup() {
        if(scheduler != null) {
            scheduler.shutdown();
        }
    }
}