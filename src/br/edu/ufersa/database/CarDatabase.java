package br.edu.ufersa.database;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.edu.ufersa.services.DatabaseServiceImpl;
import br.edu.ufersa.services.skeletons.DatabaseService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;

public class CarDatabase {
    public static void main(String[] args) {
        
        try {

             // ------------------------------- Serviço de banco de dados --------------------------------------------

            DatabaseServiceImpl databaseObjRef = new DatabaseServiceImpl();
            DatabaseService databaseSkeleton = (DatabaseService) UnicastRemoteObject.exportObject(databaseObjRef, 0);

            LocateRegistry.createRegistry( ServicePorts.DATABASE_PORT.getValue() );
            Registry databaseReg = LocateRegistry.getRegistry( ServicePorts.DATABASE_PORT.getValue() );
            databaseReg.rebind("Database", databaseSkeleton);

            // -------------------------------------------------------------------------------------------------------

            GUI.clearScreen();
            System.out.println("Database is running now: ");
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
