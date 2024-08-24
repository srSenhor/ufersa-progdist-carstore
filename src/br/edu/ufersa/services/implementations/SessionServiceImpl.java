package br.edu.ufersa.services.implementations;

import java.rmi.RemoteException;
import java.util.HashMap;

import javax.crypto.SecretKey;

import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.services.implementations.skeletons.SessionService;
import br.edu.ufersa.utils.RSAKey;

public class SessionServiceImpl implements SessionService {

    private static HashMap<String, RSAKey> sessionPublicKeys;
    private static HashMap<String, SecretKey> sessionAesKeys;

    public SessionServiceImpl() {
        sessionPublicKeys = new HashMap<>();
        sessionAesKeys = new HashMap<>();
    }
    
    @Override
    public RSAKey getRSAKey(String username) throws RemoteException {
        return sessionPublicKeys.get(username);
    }

    @Override
    public SecretKey getAESKey(String username) throws RemoteException {
        return sessionAesKeys.get(username);
    }

    @Override
    public void openSession(String username, SessionLogin login) throws RemoteException {
        sessionPublicKeys.put(username, login.getSessionRSA().getPublicKey());
        sessionAesKeys.put(username, login.getAesKey());

        System.out.println("SESSION: Opened session -> user " + username);
    }

    @Override
    public void closeSession(String username) throws RemoteException {
        sessionPublicKeys.remove(username);
        sessionAesKeys.remove(username);

        System.out.println("SESSION: Closed session -> user " + username);
    }

    @Override
    public void echo(String username, SessionLogin login) throws RemoteException {
        sessionPublicKeys.put(username, login.getSessionRSA().getPublicKey());
        sessionAesKeys.put(username, login.getAesKey());
    }

}
