package br.edu.ufersa.services.implementations.skeletons;

import java.rmi.Remote;
import java.rmi.RemoteException;

import br.edu.ufersa.entities.Message;
import br.edu.ufersa.utils.RSAKey;

public interface DealerService extends Remote {
    Message receive(String username, Message message) throws RemoteException;
    RSAKey getPuKey() throws RemoteException;
    void echo(String username, Message message) throws RemoteException;
}
