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
import br.edu.ufersa.loadbalance.LeastConnectionsBalance;
import br.edu.ufersa.loadbalance.LoadBalance;
import br.edu.ufersa.services.implementations.skeletons.AuthService;
import br.edu.ufersa.services.implementations.skeletons.DealerService;
import br.edu.ufersa.utils.ServerListWrapper;
import br.edu.ufersa.utils.ServicePorts;
import br.edu.ufersa.utils.UserType;

public class GatewayServiceImpl implements GatewayService {

    private static HashMap<Integer, AuthService> authStubs;
    private static HashMap<Integer, DealerService> dealerStubs;
    private static ReadWriteLock lock;
    // private static ExecutorService executor;
    private static ServerListWrapper servicesList;
    private static LoadBalance algorithm;
    private static int nServers;

    public GatewayServiceImpl(int serverQuantity) {
        nServers = serverQuantity;
        servicesList = new ServerListWrapper(serverQuantity);
        lock = new ReentrantReadWriteLock();
        // executor = Executors.newFixedThreadPool(serverQuantity);

        // -------------- Algoritmos de balanceamento de carga -----------

        // algorithm = new RandomBalance();
        // algorithm = new RoundRobinBalance();
        algorithm = new LeastConnectionsBalance();

        // ---------------------------------------------------------------

        this.init(); 
    }

    @Override
    public boolean redirectAuth(SessionLogin login) throws RemoteException {
        
        int serviceId = algorithm.balance(servicesList);
        System.out.println("GATEWAY: service on port xxxx" + serviceId + " was selected to logout try");

        Lock wLock = lock.writeLock();
        wLock.lock();

        boolean sucessfulLogout = authStubs.get(serviceId).logout(login);

        if (sucessfulLogout) {

            System.out.println("GATEWAY: successfuly logged out, sending broadcast to others copies...");

            for (int count = 1; count < authStubs.size(); count++) {
                if (count != serviceId) {
                    try {
                        authStubs.get(count).logout(login);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }             
            };

        }

        wLock.unlock();
        
        return sucessfulLogout;

    }

    @Override
    public SessionLogin redirectAuth(String username, String password, UserType userType) throws RemoteException {
        
        int serviceId = algorithm.balance(servicesList);
        System.out.println("GATEWAY: service on port xxxx" + serviceId + " was selected to authentication try");

        Lock wLock = lock.writeLock();
        wLock.lock();

        SessionLogin login = authStubs.get(serviceId).signup(username, password, userType);

        if (login != null) {

            System.out.println("GATEWAY: new user was successfuly registred, sending broadcast to others copies...");

            for (int i = 1; i <= authStubs.size(); i++) {
                if (i != serviceId) {
                    try {
                        authStubs.get(i).echo(login);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        wLock.unlock();

        return login;

    }

    @Override
    public SessionLogin redirectAuth(String username, String password) throws RemoteException {
        
        int serviceId = algorithm.balance(servicesList);
        System.out.println("GATEWAY: service on port xxxx" + serviceId + " was selected to login try");

        Lock wLock = lock.writeLock();
        wLock.lock();

        SessionLogin login = authStubs.get(serviceId).auth(username, password);

        if (login != null) {

            System.out.println("GATEWAY: sucessful logged in, sending broadcast to others copies...");

            for (int i = 1; i <= authStubs.size(); i++) {
                if (i != serviceId) {
                    try {
                        authStubs.get(i).echo(login);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        wLock.unlock();

        return login;

    }

    @Override
    public Message redirectDealer(String username, Message message) throws RemoteException {
        
        int serviceId = algorithm.balance(servicesList);
        System.out.println("GATEWAY: service on port xxxx" + serviceId + " was selected to car service try");

        Lock wLock = lock.writeLock();
        wLock.lock();

        Message response = dealerStubs.get(serviceId).receive(username, message);

        if (response != null) {

            System.out.println("GATEWAY: sucessfully received message, sending broadcast to others copies...");

            for (int i = 1; i <= dealerStubs.size(); i++) {
                if (i != serviceId) {
                    try {
                        dealerStubs.get(i).echo(username, message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        wLock.unlock();

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
