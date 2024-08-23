package br.edu.ufersa.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.server.gateway.GatewayService;
import br.edu.ufersa.server.gateway.GatewayServiceImpl;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class GatewayServer {
    
    private static int nServers;

    public GatewayServer(int serverQuantity) {
        nServers = serverQuantity;
        this.exec(); 
    }

    private void exec() {        
        
        try {

            // ------------------------------- Gateway Server --------------------------------------------------------

            GatewayServiceImpl gatewayObjRef = new GatewayServiceImpl(nServers);
            GatewayService gatewaySkeleton = (GatewayService) UnicastRemoteObject.exportObject(gatewayObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.GATEWAY_PORT.getValue() );
            Registry gatewayReg = LocateRegistry.getRegistry( ServicePorts.GATEWAY_PORT.getValue() );
            gatewayReg.rebind("Gateway", gatewaySkeleton);

            // -------------------------------------------------------------------------------------------------------

            GUI.clearScreen();
            System.out.println("Gateway server is running now: ");
            
        } catch (RemoteException e) {
            System.err.println("An error has ocurred in server: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new GatewayServer(3);
    }

}