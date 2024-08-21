package br.edu.ufersa.database;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.services.DatabaseServiceImpl;
import br.edu.ufersa.services.skeletons.DatabaseService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class CarDatabaseService {

    private int serverId;

    public CarDatabaseService(int id) {
        this.serverId = id;
        this.exec(); 
    }

    private void exec() {
        
        try {

             // ------------------------------- Serviço de banco de dados --------------------------------------------

            DatabaseServiceImpl databaseObjRef = new DatabaseServiceImpl();
            DatabaseService databaseSkeleton = (DatabaseService) UnicastRemoteObject.exportObject(databaseObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.DATABASE_PORT.getValue() + serverId );
            Registry databaseReg = LocateRegistry.getRegistry( ServicePorts.DATABASE_PORT.getValue() + serverId );
            databaseReg.rebind("Database" + serverId, databaseSkeleton);

            // -------------------------------------------------------------------------------------------------------

            GUI.clearScreen();
            System.out.println("Database's copy #" + serverId + " is running now: ");
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
