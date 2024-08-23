package br.edu.ufersa.server.gateway;

import java.rmi.Remote;
import java.rmi.RemoteException;

import br.edu.ufersa.entities.Message;
import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.utils.UserType;

public interface GatewayService extends Remote {

    boolean redirectAuth(SessionLogin login) throws RemoteException;
    SessionLogin redirectAuth(String username, String password) throws RemoteException;  
    SessionLogin redirectAuth(String username, String password, UserType userType) throws RemoteException;  
    Message redirectDealer(String username, Message message) throws RemoteException;
    // RSAKey redirectDealer() throws RemoteException;
    
}
