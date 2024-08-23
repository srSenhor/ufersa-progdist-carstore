package br.edu.ufersa.server.gateway;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import br.edu.ufersa.entities.Message;
import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.loadbalance.LoadBalance;
import br.edu.ufersa.loadbalance.RandomBalance;
import br.edu.ufersa.services.implementations.skeletons.AuthService;
import br.edu.ufersa.services.implementations.skeletons.DealerService;
import br.edu.ufersa.utils.ServerListWrapper;
import br.edu.ufersa.utils.ServicePorts;
import br.edu.ufersa.utils.UserType;

public class GatewayServiceImpl implements GatewayService {

    private static HashMap<Integer, AuthService> authStubs;
    private static HashMap<Integer, DealerService> dealerStubs;
    private static ServerListWrapper servicesList;
    private static ReadWriteLock lock;
    private static LoadBalance algorithm;
    private static int nServers;

    public GatewayServiceImpl(int serverQuantity) {
        nServers = serverQuantity;
        servicesList = new ServerListWrapper(serverQuantity);
        lock = new ReentrantReadWriteLock();
        algorithm = new RandomBalance();
        this.init(); 
    }

    @Override
    public boolean redirectAuth(SessionLogin login) throws RemoteException {
        
        int serviceId = algorithm.balance(servicesList);

        Lock wLock = lock.writeLock();
        wLock.lock();

        authStubs.get(serviceId).logout(login);

        if (authStubs.get(serviceId).logout(login)) {

            for (int i = 1; i <= authStubs.size(); i++) {
                if (i != serviceId) {
                    try {
                        authStubs.get(i).logout(login);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            wLock.unlock();

            return true;

        }

        return false;

    }

    @Override
    public SessionLogin redirectAuth(String username, String password, UserType userType) throws RemoteException {
        
        int serviceId = algorithm.balance(servicesList);

        Lock wLock = lock.writeLock();
        wLock.lock();

        SessionLogin login = authStubs.get(serviceId).signup(username, password, userType);

        if (login != null) {

            for (int i = 1; i <= authStubs.size(); i++) {
                if (i != serviceId) {
                    try {
                        authStubs.get(i).signup(username, password, userType);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            wLock.unlock();

            return login;

        }

        return null;

    }

    @Override
    public SessionLogin redirectAuth(String username, String password) throws RemoteException {
        
        int serviceId = algorithm.balance(servicesList);

        Lock wLock = lock.writeLock();
        wLock.lock();

        SessionLogin login = authStubs.get(serviceId).auth(username, password);

        if (login != null) {

            for (int i = 1; i <= authStubs.size(); i++) {
                if (i != serviceId) {
                    try {
                        authStubs.get(i).auth(username, password);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            wLock.unlock();
            
            return login;

        }

        return null;

    }

    @Override
    public Message redirectDealer(String username, Message message) throws RemoteException {
        
        int serviceId = algorithm.balance(servicesList);

        Lock wLock = lock.writeLock();
        wLock.lock();

        Message response = dealerStubs.get(serviceId).receive(username, message);

        if (response != null) {

            for (int i = 1; i <= dealerStubs.size(); i++) {
                if (i != serviceId) {
                    try {
                        dealerStubs.get(i).receive(username, message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            wLock.unlock();

            return response;

        }

        return response;

    }

    private void init() {        
        
        authStubs = new HashMap<>(nServers);
        dealerStubs = new HashMap<>(nServers);

        for (int serviceId = 1; serviceId <= nServers; serviceId++) {

            Registry reg;
            AuthService authStub = null;
            DealerService dealerStub = null;

            try {

                reg = LocateRegistry.getRegistry( "localhost", ServicePorts.AUTH_PORT.getValue() + serviceId );
                authStub = (AuthService) reg.lookup( "Auth" + serviceId );
                reg = LocateRegistry.getRegistry( "localhost", ServicePorts.DEALER_PORT.getValue() + serviceId );
                dealerStub = (DealerService) reg.lookup( "Dealer" + serviceId );
                
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }

            authStubs.put(serviceId, authStub);
            dealerStubs.put(serviceId, dealerStub);

            servicesList.put(serviceId, 0);

        }

    }

}
