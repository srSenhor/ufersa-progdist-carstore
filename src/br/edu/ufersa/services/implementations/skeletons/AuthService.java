package br.edu.ufersa.services.implementations.skeletons;

import java.rmi.Remote;
import java.rmi.RemoteException;

import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.utils.UserType;

public interface AuthService extends Remote {
    SessionLogin auth(String login, String password) throws RemoteException;
    SessionLogin signup(String login, String password, UserType type) throws RemoteException;
    boolean logout(SessionLogin login) throws RemoteException;
    void echo(SessionLogin login) throws RemoteException;
}
