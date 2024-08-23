package br.edu.ufersa.database;

import br.edu.ufersa.services.Database;

public class CarDatabase {

    // private static final int ID = 1;
    private static final int ID = 2;
    // private static final int ID = 3;

    public static void main(String[] args) {

        // for (int id = 1; id <= 3; id++) {
        //     new CarDatabaseService(id);
        // }
    
        new Database(ID);
        
    }

}