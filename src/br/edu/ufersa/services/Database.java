package br.edu.ufersa.services;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.services.implementations.DatabaseServiceImpl;
import br.edu.ufersa.services.implementations.skeletons.DatabaseService;
import br.edu.ufersa.utils.ServicePorts;

public class Database {

    private int serviceId;

    public Database(int id) {
        this.serviceId = id;
        this.exec(); 
    }

    private void exec() {
        
        try {

             // ------------------------------- Serviço de banco de dados --------------------------------------------

            DatabaseServiceImpl databaseObjRef = DatabaseServiceImpl.getInstance();
            DatabaseService databaseSkeleton = (DatabaseService) UnicastRemoteObject.exportObject(databaseObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.DATABASE_PORT.getValue() + serviceId );
            Registry databaseReg = LocateRegistry.getRegistry( ServicePorts.DATABASE_PORT.getValue() + serviceId );
            databaseReg.rebind("Database" + serviceId, databaseSkeleton);

            // -------------------------------------------------------------------------------------------------------

            // GUI.clearScreen();
            // System.out.println("Database service's copy #" + serviceId + " is running now: ");
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

}