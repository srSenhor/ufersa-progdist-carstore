package br.edu.ufersa.services;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.services.implementations.AuthServiceImpl;
import br.edu.ufersa.services.implementations.skeletons.AuthService;
import br.edu.ufersa.utils.ServicePorts;

public class Auth {

    private int serviceId;

    public Auth(int id) {
        this.serviceId = id;
        this.exec(); 
    }

    private void exec() {        
        
        try {

            // ------------------------------- Serviço de Autenticação -----------------------------------------------

            AuthServiceImpl authObjRef = new AuthServiceImpl(serviceId);
            AuthService authSkeleton = (AuthService) UnicastRemoteObject.exportObject(authObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.AUTH_PORT.getValue() + serviceId );
            Registry authReg = LocateRegistry.getRegistry( ServicePorts.AUTH_PORT.getValue() + serviceId );
            authReg.rebind("Auth" + serviceId, authSkeleton);

            // -------------------------------------------------------------------------------------------------------

            // GUI.clearScreen();
            // System.out.println("Auth service's copy #" + serviceId + " is running now: ");
            
        } catch (RemoteException e) {
            System.err.println("An error has ocurred in server: " + e.getMessage());
            e.printStackTrace();
        }

    }

}