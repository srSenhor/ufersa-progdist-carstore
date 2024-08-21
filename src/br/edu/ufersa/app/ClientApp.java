package br.edu.ufersa.app;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import br.edu.ufersa.client.Client;
import br.edu.ufersa.client.Employee;
import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.services.skeletons.AuthService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.ServicePorts;
import br.edu.ufersa.utils.UserType;

public class ClientApp {

    private int serverId;

    public ClientApp() {
        this.serverId = 0;
        this.init();
    }

    public ClientApp(int serverId) {
        this.serverId = serverId;
        this.init();
    }

    private void init() {

        Scanner cin = null;
        boolean trying;

        try {
            
            cin = new Scanner(System.in);
            trying = false;

            Registry reg = LocateRegistry.getRegistry( "localhost", ServicePorts.AUTH_PORT.getValue() + serverId );
            AuthService stub = (AuthService) reg.lookup("Auth" + serverId );

            SessionLogin userLogin;

            do {

                GUI.clearScreen();
                GUI.entryScreen();
                
                int loginOperation = cin.nextInt();
                cin.nextLine();
                
                System.out.println("\nPlease enter your credentials\n");
                System.out.print("Login   : ");
                String username = cin.nextLine();
                
                System.out.print("Password: ");
                String password = cin.nextLine();


                if (loginOperation == 1) {
                    userLogin = stub.auth(username, password);
                } else {
                    // Os funcionários já são registrados no sistema por padrão
                    userLogin = stub.signup(username, password, UserType.CLIENT);
                }


                if ( userLogin != null ) {
                    System.out.println("Successful logged in!");
                    trying = false;


                    System.out.println("Press any key to continue...");
                    cin.nextLine();
             
                    mainMenu(userLogin);

                    stub.logout(userLogin);
                    System.out.println("Successful logged out!");
                } else {
                    System.out.println("Failed to login, there is something wrong...");
                    
                    System.out.println("Press any key to continue...");
                    cin.nextLine();
                }

            } while (trying);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cin.close();
        }

    }

      private void mainMenu(SessionLogin sessionLogin){

        switch (sessionLogin.getType()) {
            case CLIENT:
                new Client(sessionLogin, serverId);
                break;
            case EMPLOYEE:
                new Employee(sessionLogin, serverId);
                break;
            default:
                System.err.println("Undefined type");
                break;
        }

    }

}
