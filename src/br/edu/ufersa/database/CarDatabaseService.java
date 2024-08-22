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

    // // private static final int serverId = 1;
    // private static final int serverId = 2;
    // // private static final int serverId = 3;
    
    // private static CarDatabaseService instance;
    private int serverId;

    public CarDatabaseService(int id) {
        this.serverId = id;
        this.exec(); 
    }

    // public static CarDatabaseService getInstance(/*int serverId*/) {
        
    //     if (instance == null) instance = new CarDatabaseService(/*serverId*/);

    //     return instance;
    // }

    // public static void main(String[] args) {
    private void exec() {
        
        try {

             // ------------------------------- Serviço de banco de dados --------------------------------------------

            DatabaseServiceImpl databaseObjRef = DatabaseServiceImpl.getInstance();
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
