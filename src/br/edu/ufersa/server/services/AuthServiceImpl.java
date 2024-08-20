package br.edu.ufersa.server.services;

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
import br.edu.ufersa.server.services.skeletons.AuthService;
import br.edu.ufersa.server.services.skeletons.DealerService;
import br.edu.ufersa.server.services.skeletons.SessionService;
import br.edu.ufersa.utils.RSAKey;
import br.edu.ufersa.utils.ServicePorts;
import br.edu.ufersa.utils.UserType;

public class AuthServiceImpl implements AuthService {

    private static HashMap<String, User> users;
    private static SessionService sessionStub;
    private static DealerService dealerStub;
    private static RSAKey serverPuKey;

    public AuthServiceImpl() {
        users = new HashMap<>();
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
                login = new SessionLogin(user.getUsername(), user.getType(), new RSAImpl(), serverPuKey, bc.getKey());
            
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
            login = new SessionLogin(user.getUsername(), user.getType(), new RSAImpl(), serverPuKey, bc.getKey());
        
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

    private void init(){
        try {

            Registry reg = LocateRegistry.getRegistry("localhost", ServicePorts.SESSION_PORT.getValue());
            sessionStub = (SessionService) reg.lookup("Session");
            Registry dealReg = LocateRegistry.getRegistry("localhost", ServicePorts.DEALER_PORT.getValue());
            dealerStub = (DealerService) dealReg.lookup("Dealer");

            serverPuKey = dealerStub.getPuKey();

            logout(signup("babaganush", "senha123", UserType.CLIENT));
            logout(signup("silvao", "senha456", UserType.EMPLOYEE));

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
