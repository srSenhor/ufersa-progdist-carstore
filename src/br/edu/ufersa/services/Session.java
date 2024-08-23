package br.edu.ufersa.services;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.services.implementations.SessionServiceImpl;
import br.edu.ufersa.services.implementations.skeletons.SessionService;
import br.edu.ufersa.utils.ServicePorts;

public class Session {

    private int serviceId;

    public Session(int id) {
        this.serviceId = id;
        this.exec(); 
    }

    private void exec() {        
        
        try {

            // ------------------------------- Serviço de Sessão -----------------------------------------------------

            SessionServiceImpl sessionObjRef = new SessionServiceImpl();
            SessionService sessionSkeleton = (SessionService) UnicastRemoteObject.exportObject(sessionObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.SESSION_PORT.getValue() + serviceId );
            Registry sessionReg = LocateRegistry.getRegistry( ServicePorts.SESSION_PORT.getValue() + serviceId );
            sessionReg.rebind("Session" + serviceId, sessionSkeleton);

            // -------------------------------------------------------------------------------------------------------

            // GUI.clearScreen();
            // System.out.println("Session service's copy #" + serviceId + " is running now: ");
            
        } catch (RemoteException e) {
            System.err.println("An error has ocurred in server: " + e.getMessage());
            e.printStackTrace();
        }

    }

}