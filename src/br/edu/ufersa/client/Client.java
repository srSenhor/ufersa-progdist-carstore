package br.edu.ufersa.client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.edu.ufersa.entities.Message;
import br.edu.ufersa.entities.Request;
import br.edu.ufersa.entities.SessionLogin;
import br.edu.ufersa.loadbalance.LoadBalance;
import br.edu.ufersa.loadbalance.RandomBalance;
import br.edu.ufersa.security.SecurityCipher;
import br.edu.ufersa.services.skeletons.DealerService;
import br.edu.ufersa.utils.GUI;
import br.edu.ufersa.utils.RegStubWrapper;
import br.edu.ufersa.utils.ServicePorts;
import br.edu.ufersa.utils.UserType;

public class Client implements Serializable {
    
    private static final long serialVersionUID = 1L;
    protected UserType userType;
    protected SessionLogin sessionLogin;
    protected DealerService dealerStub;
    protected int serviceId;

    public Client () {}

    public Client(SessionLogin sessionLogin, int serviceId) {
        this.sessionLogin = sessionLogin;
        this.userType = UserType.CLIENT;
        // this.serviceId = serviceId;
        this.exec();
    }

    public UserType getUserType() {
        return userType;
    }
    
    public SessionLogin getSessionLogin() {
        return sessionLogin;
    }
    
    protected void exec() {
        
        Scanner cin = null;
        int op;

        try {

            cin = new Scanner(System.in);
            op = 0;
            
            do {
                GUI.clearScreen();
                GUI.clientMenu();

                op = cin.nextInt();
                cin.nextLine();

                Message response = new Message("", "");

                switch (op) {
                    case 1:
                        search(response, op, cin);

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 2:
                        list(response, op, cin);
                        
                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 3:
                        stock(response, op, cin);
                        
                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 4:
                        buy(response, op, cin);

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                    case 5:
                        System.out.println("bye my friend!");
                        break;
                    default:
                        System.err.println("undefined operation");

                        System.out.println("Press any key to continue...");
                        cin.nextLine();
                        break;
                }
            } while(op != 5);

            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cin.close();
        }

    }

     protected void search(Message response, int op, Scanner cin) {
        GUI.searchOps();
        int searchOption = cin.nextInt();
        cin.nextLine();

        if (searchOption == 1) {
            System.out.print("Renavam: ");
            long renavam = cin.nextLong();
            cin.nextLine();

            response.setContent(send(op, sessionLogin.getUsername(), renavam, null, -1, -1, searchOption));

        } else {
            System.out.print("Name: ");
            String name = cin.nextLine();

            response.setContent(send(op, sessionLogin.getUsername(), -1, name, -1, -1, searchOption));
        }

        System.out.println(response.getContent());
    }

    protected void list(Message response, int op, Scanner cin) {
        GUI.listOps();
        int listOption = cin.nextInt();
        cin.nextLine();

        response.setContent(send(op, sessionLogin.getUsername(), -1L, null, -1, -1.0f, listOption));
        System.out.println(response.getContent());
    }

    protected void stock(Message response, int op, Scanner cin) {
        GUI.stockOps();
        int stockOption = cin.nextInt();
        cin.nextLine();

        response.setContent(send(op, sessionLogin.getUsername(), -1L, null, -1, -1.0f, stockOption));
        System.out.println(response.getContent());
    }

    protected void buy(Message response, int op, Scanner cin) {
        GUI.buyOps();
        String name = cin.nextLine();

        long renavam;

        
        /*
        TODO dar um jeito da tela ficar atualizando a lista de carros
        * Usar uma thread pra ficar recuperando o valor e outra pra ficar imprimindo?
        */
        // Thread t0 = new Thread(new ThreadRefresh(response, name, 0));
        // t0.start();
        
        response.setContent(send(1, sessionLogin.getUsername(), -1, name, -1, -1, 2));
        System.out.print("""
                This cars are available
                """ + 
                response.getContent() +
                "Renavam: ");

        
        renavam = cin.nextLong();
        cin.nextLine();

        // t0.interrupt();

        System.out.println("Are you sure you want to buy this car? [y] for yes / [n] for no");
        String confirm = cin.next(); 

        if (confirm.toLowerCase().charAt(0) == 'y') {
            response.setContent(send(op, sessionLogin.getUsername(), renavam, name, -1, -1, -1));
            cin.nextLine();
        } else {
            System.err.println("Cancelled operation");
            cin.nextLine();
        }

        System.out.println(response.getContent());
    }


    protected String send(int operationType, String username, long renavam, String name, int productionYear, float price, int category) {

        // TODO: Definir o stub do serviço de carros aqui, com o balanceador de carga

        // LoadBalance<DealerService> random = (wrapper, service, serverQuantity) -> {

        //         int serviceId = ((int) new Random().nextInt(serverQuantity - 1)) + 1;

        //         try {

        //             Registry reg = LocateRegistry.getRegistry( "localhost", ServicePorts.DEALER_PORT.getValue() + serviceId );
        //             DealerService stub = (DealerService) reg.lookup( "Dealer" + serviceId );

        //             wrapper.setRegistry(reg);
        //             wrapper.setServiceStub(stub);

        //         } catch (RemoteException e) {
        //             e.printStackTrace();
        //         } catch (NotBoundException e) {
        //             e.printStackTrace();
        //         }

        // };

        LoadBalance<DealerService> random = new RandomBalance<>();
        RegStubWrapper<DealerService> wrapper = new RegStubWrapper<DealerService>(null, null);

        random.balance(wrapper, ServicePorts.DEALER_PORT, "Dealer", 3);

        dealerStub = wrapper.getServiceStub();


        String request = new Request(operationType, userType, category, renavam, name, username, productionYear, price).toString();
        String response = "";

        try {

            SecurityCipher bc = new SecurityCipher(this.sessionLogin.getAesKey());
            request = bc.enc(request);

            String hash = bc.genHash(request);           
            hash = this.sessionLogin.getSessionRSA().sign(hash);

            // Message messageResponse = proxy.receive(this, operationType, new Message(request, hash));
            Message messageResponse = dealerStub.receive(username, new Message(request, hash));

            if (messageResponse == null) {
                response =  "cannot do this, please try again...'";
            } else {
                String responseTestHash = this.sessionLogin.getSessionRSA().checkSign(messageResponse.getHash(), this.sessionLogin.getServerPublicKey());
    
                if(!bc.genHash(messageResponse.getContent()).equals(responseTestHash)) {
                    response = "an error has ocurred, please try again";
                } else {
                    response = bc.dec(messageResponse.getContent());
                }
                
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {           
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {            
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {            
            e.printStackTrace();
        } catch (BadPaddingException e) {           
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return response;
    }

}