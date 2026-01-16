package model;

public class Client {
    private int id;
    private int arrivalTime;
    private int serviceTime;
    private int initialServiceTime;
    private int serviceStartTime;
    private int leaveQueueTime;

    public Client(int id, int arrivalTime, int serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.initialServiceTime = serviceTime;
        this.serviceStartTime = -1;
        this.leaveQueueTime = -1;
    }

    public int getId() { return id; }
    public int getArrivalTime() { return arrivalTime; }
    public int getServiceTime() { return serviceTime; }
    public int getServiceStartTime() { return serviceStartTime; }
    public int getLeaveQueueTime() { return leaveQueueTime; }

    public void setServiceStartTime(int time) {
        this.serviceStartTime = time;
    }

    public void setLeaveQueueTime(int time) {
        this.leaveQueueTime = time;
    }

    public synchronized void decrementServiceTime() {
        if (this.serviceTime > 0) {
            this.serviceTime--;
        }
    }

    public int getWaitingTime() {
        if (leaveQueueTime == -1) return -1; //clientul e inca in coada
        return leaveQueueTime - arrivalTime - initialServiceTime;
    }

    @Override
    public String toString() {
        return "Client " + id + " (AT:" + arrivalTime + ", ST:" + serviceTime +
                ", IniST:" + initialServiceTime + ", LeaveQ:" + leaveQueueTime + ")";
    }
}