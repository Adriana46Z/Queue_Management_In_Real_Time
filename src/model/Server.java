package model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    private BlockingQueue<Client> clientsQueue;
    private AtomicInteger waitingPeriod;
    private volatile boolean running;
    private final int maxCapacity;
    private int currentTime;
    private List<Client> processedClients;

    public Server(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.clientsQueue = new LinkedBlockingQueue<>(maxCapacity);
        this.waitingPeriod = new AtomicInteger(0);
        this.running = true;
        this.currentTime = 0;
        this.processedClients = new ArrayList<>();
    }

    public boolean addClient(Client client) {
        if (isFull()) return false;
        boolean added = clientsQueue.add(client);
        if (added) {
            waitingPeriod.addAndGet(client.getServiceTime());
        }
        return added;
    }

    public boolean isFull() {
        return clientsQueue.size() >= maxCapacity;
    }

    public boolean isEmpty() {
        return clientsQueue.isEmpty();
    }

    public int getClientsCount() {
        return clientsQueue.size();
    }

    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }

    public void stopRunning() {
        this.running = false;
    }

    public void setCurrentTime(int time) {
        this.currentTime = time;
    }

    public List<Client> getProcessedClients() {
        return processedClients;
    }

    @Override
    public String toString() {
        if (clientsQueue.isEmpty()) {
            return "closed";
        }

        StringBuilder sb = new StringBuilder();
        for (Client client : clientsQueue) {
            sb.append(client.toString()).append("; ");
        }

        return sb.toString();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Client current = clientsQueue.peek();
                if (current != null) {
                    synchronized (this) {
                        if (current.getServiceStartTime() == -1) {
                            current.setServiceStartTime(currentTime);
                        }

                        current.decrementServiceTime();
                        waitingPeriod.decrementAndGet();

                        if (current.getServiceTime() <= 0) {
                            Client finishedClient = clientsQueue.poll(); //eliminare client procesat
                            if (finishedClient != null) {
                                finishedClient.setLeaveQueueTime(currentTime);
                                processedClients.add(finishedClient);
                            }
                        }
                    }
                }
                Thread.sleep(1000);
                currentTime++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}