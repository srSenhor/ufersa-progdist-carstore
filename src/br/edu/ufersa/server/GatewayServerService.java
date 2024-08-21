package br.edu.ufersa.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.services.AuthServiceImpl;
import br.edu.ufersa.services.DealerServiceImpl;
import br.edu.ufersa.services.SessionServiceImpl;
import br.edu.ufersa.services.skeletons.AuthService;
import br.edu.ufersa.services.skeletons.DealerService;
import br.edu.ufersa.services.skeletons.SessionService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class GatewayServerService {
    
    private int serverId;

    public GatewayServerService(int id) {
        this.serverId = id;
        this.exec(); 
    }

    private void exec() {        
        
        try {

            // ------------------------------- Serviço de Sessão -----------------------------------------------------

            SessionServiceImpl sessionObjRef = new SessionServiceImpl();
            SessionService sessionSkeleton = (SessionService) UnicastRemoteObject.exportObject(sessionObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.SESSION_PORT.getValue() + serverId );
            Registry sessionReg = LocateRegistry.getRegistry( ServicePorts.SESSION_PORT.getValue() + serverId );
            sessionReg.rebind("Session" + serverId, sessionSkeleton);

            // -------------------------------------------------------------------------------------------------------

            // ------------------------------- Serviços da loja de carros --------------------------------------------

            DealerServiceImpl dealerObjRef = new DealerServiceImpl(serverId);
            DealerService dealerSkeleton = (DealerService) UnicastRemoteObject.exportObject(dealerObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.DEALER_PORT.getValue() + serverId );
            Registry dealerReg = LocateRegistry.getRegistry( ServicePorts.DEALER_PORT.getValue() + serverId );
            dealerReg.rebind("Dealer" + serverId, dealerSkeleton);

            // -------------------------------------------------------------------------------------------------------

            // ------------------------------- Serviço de Autenticação -----------------------------------------------

            AuthServiceImpl authObjRef = new AuthServiceImpl(serverId);
            AuthService authSkeleton = (AuthService) UnicastRemoteObject.exportObject(authObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.AUTH_PORT.getValue() + serverId );
            Registry authReg = LocateRegistry.getRegistry( ServicePorts.AUTH_PORT.getValue() + serverId );
            authReg.rebind("Auth" + serverId, authSkeleton);

            // -------------------------------------------------------------------------------------------------------

            GUI.clearScreen();
            System.out.println("Gateway's copy #" + serverId + " is running now: ");
            
        } catch (Exception e) {
            System.err.println("An error has ocurred in server: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
