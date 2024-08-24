package br.edu.ufersa.services.implementations;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.entities.User;
import br.edu.ufersa.security.RSAImpl;
import br.edu.ufersa.security.SecurityCipher;
import br.edu.ufersa.services.implementations.skeletons.AuthService;
import br.edu.ufersa.services.implementations.skeletons.DealerService;
import br.edu.ufersa.services.implementations.skeletons.SessionService;
import br.edu.ufersa.utils.RSAKey;
import br.edu.ufersa.utils.ServicePorts;
import br.edu.ufersa.utils.UserType;

public class AuthServiceImpl implements AuthService {

    private static HashMap<String, User> users;
    private static SessionService sessionStub;
    private static DealerService dealerStub;
    private static RSAKey serverPublicKey;
    private int servicesId;

    public AuthServiceImpl() {
        users = new HashMap<>();
        this.servicesId = 0;
        this.init();
    }

    public AuthServiceImpl(int servicesId) {
        users = new HashMap<>();
        this.servicesId = servicesId;
        this.init();
    }

    @Override
    public SessionLogin auth(String username, String password) throws RemoteException {
        User user = users.get(username);

        if (user == null) { 
            System.out.println("AUTH: ERROR! User " + username + " doesn't exists");
            return null; 
        }

        if (user.getPassword().equals(password) && !user.isLogged()) {
            SecurityCipher bc = null;
            SessionLogin login = null;

            try {

                bc = new SecurityCipher();
                login = new SessionLogin(user.getUsername(), user.getType(), new RSAImpl(), serverPublicKey, bc.getKey());
            
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            user.userLoggedIn();
            sessionStub.openSession(username, login);
            
            System.out.println("AUTH: User " + username + " logged in");    

            return login;
        }

        System.out.println("AUTH: ERROR! Password for " + username + " is wrong");
        return null;
    }

    @Override
    public SessionLogin signup(String username, String password, UserType type) throws RemoteException {     
        if(username.isBlank() || password.isBlank() || type == UserType.UNDEFINED)
            return null;
        
        User user = users.get(username);

        if (user != null) {
            System.out.println("AUTH: ERROR! User " + username + " already exists");
            return null;
        }

        user = new User(username, password, type);
        users.put(username, user);

        SecurityCipher bc = null;
        SessionLogin login = null;

        try {

            bc = new SecurityCipher();
            login = new SessionLogin(user.getUsername(), user.getType(), new RSAImpl(), serverPublicKey, bc.getKey());
        
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        user.userLoggedIn();
        sessionStub.openSession(username, login);
            
        System.out.println("AUTH: New user created -> " + user.toString());

        return login;
    }

    @Override
    public boolean logout(SessionLogin login) throws RemoteException {
        User user = users.get(login.getUsername());
        if (user == null || user.isLogged() == false ) { return false; }

        user.userLoggedOut();
        sessionStub.closeSession(login.getUsername());

        return true;
    }

    @Override
    public void echo(SessionLogin login) throws RemoteException {
        sessionStub.echo(login.getUsername(), login);
    }

    private void init(){
        try {

            Registry reg = LocateRegistry.getRegistry( "localhost", ServicePorts.SESSION_PORT.getValue() + servicesId );
            sessionStub = (SessionService) reg.lookup("Session" + servicesId);
            // Registry reg = LocateRegistry.getRegistry( "localhost", ServicePorts.SESSION_PORT.getValue() );
            // sessionStub = (SessionService) reg.lookup( "Session" );
            Registry dealReg = LocateRegistry.getRegistry( "localhost", ServicePorts.DEALER_PORT.getValue() + servicesId );
            dealerStub = (DealerService) dealReg.lookup("Dealer" + servicesId);

            serverPublicKey = dealerStub.getPuKey();

            logout(signup("babaganush", "senha123", UserType.CLIENT));
            logout(signup("silvao", "senha456", UserType.EMPLOYEE));

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

}
