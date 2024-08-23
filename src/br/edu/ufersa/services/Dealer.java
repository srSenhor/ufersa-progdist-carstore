package br.edu.ufersa.services;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.services.implementations.DealerServiceImpl;
import br.edu.ufersa.services.implementations.skeletons.DealerService;
import br.edu.ufersa.utils.ServicePorts;

public class Dealer {

    private int serviceId;

    public Dealer(int id) {
        this.serviceId = id;
        this.exec(); 
    }

    private void exec() {        
        
        try {

            // ------------------------------- Serviços da loja de carros --------------------------------------------

            DealerServiceImpl dealerObjRef = new DealerServiceImpl(serviceId);
            DealerService dealerSkeleton = (DealerService) UnicastRemoteObject.exportObject(dealerObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.DEALER_PORT.getValue() + serviceId );
            Registry dealerReg = LocateRegistry.getRegistry( ServicePorts.DEALER_PORT.getValue() + serviceId );
            dealerReg.rebind("Dealer" + serviceId, dealerSkeleton);

            // -------------------------------------------------------------------------------------------------------

            // GUI.clearScreen();
            // System.out.println("Dealer service's copy #" + serviceId + " is running now: ");
            
        } catch (RemoteException e) {
            System.err.println("An error has ocurred in server: " + e.getMessage());
            e.printStackTrace();
        }

    }

}