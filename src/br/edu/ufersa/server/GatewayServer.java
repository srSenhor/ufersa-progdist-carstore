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

public class GatewayServer {
    public static void main(String[] args) {
        
        try {

            // ------------------------------- Serviço de Sessão -----------------------------------------------------

            SessionServiceImpl sessionObjRef = new SessionServiceImpl();
            SessionService sessionSkeleton = (SessionService) UnicastRemoteObject.exportObject(sessionObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.SESSION_PORT.getValue() );
            Registry sessionReg = LocateRegistry.getRegistry( ServicePorts.SESSION_PORT.getValue() );
            sessionReg.rebind("Session", sessionSkeleton);

            // -------------------------------------------------------------------------------------------------------

            // ------------------------------- Serviços da loja de carros --------------------------------------------

            DealerServiceImpl dealerObjRef = new DealerServiceImpl();
            DealerService dealerSkeleton = (DealerService) UnicastRemoteObject.exportObject(dealerObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.DEALER_PORT.getValue() );
            Registry dealerReg = LocateRegistry.getRegistry( ServicePorts.DEALER_PORT.getValue() );
            dealerReg.rebind("Dealer", dealerSkeleton);

            // -------------------------------------------------------------------------------------------------------

            // ------------------------------- Serviço de Autenticação -----------------------------------------------

            AuthServiceImpl authObjRef = new AuthServiceImpl();
            AuthService authSkeleton = (AuthService) UnicastRemoteObject.exportObject(authObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.AUTH_PORT.getValue() );
            Registry authReg = LocateRegistry.getRegistry( ServicePorts.AUTH_PORT.getValue() );
            authReg.rebind("Auth", authSkeleton);

            // -------------------------------------------------------------------------------------------------------

            GUI.clearScreen();
            System.out.println("Server is running now: ");
            
        } catch (Exception e) {
            System.err.println("An error has ocurred in server: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
