package businesslogic;

import model.Client;
import model.Server;

import java.util.List;

public interface Strategy {
    void addClient(List<Server> servers, Client client);
}