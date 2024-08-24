package br.edu.ufersa.services.implementations;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.edu.ufersa.entities.Car;
import br.edu.ufersa.entities.Message;
import br.edu.ufersa.entities.Request;
import br.edu.ufersa.security.RSAImpl;
import br.edu.ufersa.security.SecurityCipher;
import br.edu.ufersa.services.implementations.skeletons.DatabaseService;
import br.edu.ufersa.services.implementations.skeletons.DealerService;
import br.edu.ufersa.services.implementations.skeletons.SessionService;
import br.edu.ufersa.utils.CarType;
import br.edu.ufersa.utils.RSAKey;
import br.edu.ufersa.utils.ServicePorts;

public class DealerServiceImpl implements DealerService {

    private static DatabaseService dbStub;
    private static SessionService sessionStub;
    private static RSAImpl rsa;
    private int servicesId;

    public DealerServiceImpl() {
        rsa = new RSAImpl();
        this.servicesId = 0;
        this.init();
    }

    public DealerServiceImpl(int servicesId) {
        rsa = new RSAImpl();
        this.servicesId = servicesId;
        this.init();
    }

    @Override
    public Message receive(String username, Message message) throws RemoteException { 

        if (message == null || message.getContent() == null || message.getHash() == null) {
            return null;
        }
        
        RSAKey clientPublicKey = sessionStub.getRSAKey(username);

        String receivedHash = rsa.checkSign(message.getHash(), clientPublicKey);
        SecurityCipher bc = null;
        String content = "";

        try {
            bc = new SecurityCipher(sessionStub.getAESKey(username));
            
            if(!bc.genHash(message.getContent()).equals(receivedHash)) {
                System.err.println("failed to receive this request, please try again");
                System.err.println("(stop try, weirdo)");
                return null;
            }
            
            content = bc.dec(message.getContent());

        } catch (InvalidKeyException e) {
            e.printStackTrace();
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
        }

        Request req = Request.fromString(content);

        System.out.println("DEALER: Request from " + username + " -> " + req.toString());

        Message response = null;

        if (req.getUserType().getValue() == 0) {
            switch (req.getOperationType()) {
                case 1:
                    response = (req.getSelectedOption()  == 1) ? 
                    this.searchByRenavam(req, bc) : 
                    this.searchByName(req, bc);

                    break;
                case 2:
                    response = (req.getSelectedOption()  == 1) ? 
                    this.list(req, bc) : 
                    this.listByCategory(req, bc);

                    break;
                case 3:
                    response = (req.getSelectedOption() == 1) ? 
                    this.stock(req, bc) : 
                    this.stockByName(req, bc);

                    break;
                case 4:
                    response = this.buy(req, bc);

                    break;
                default:
                    return null;
            }
        } else if (req.getUserType().getValue() == 1) {
            switch (req.getOperationType()) {
                case 1:
                    response = (req.getSelectedOption()  == 1) ? 
                    this.searchByRenavam(req, bc) : 
                    this.searchByName(req, bc);

                    break;
                case 2:
                    response = (req.getSelectedOption()  == 1) ? 
                    this.list(req, bc) : 
                    this.listByCategory(req, bc);

                    break;
                case 3:
                    response = (req.getSelectedOption()  == 1) ? 
                    this.stock(req, bc) : 
                    this.stockByName(req, bc);

                    break;
                case 4:
                    response = this.buy(req, bc);
                    
                    break;
                case 5:
                    response = this.add(req, bc);

                    break;
                case 6:
                    response = this.update(req, bc);
            
                    break;
                case 7:
                    response = this.remove(req, bc);

                    break;
                default:
                    return null;
            }
        } else {
            return null;
        }

        return response;

    }

    @Override 
    public void echo(String username, Message message) throws RemoteException {

        RSAKey clientPublicKey = sessionStub.getRSAKey(username);

        String receivedHash = rsa.checkSign(message.getHash(), clientPublicKey);
        SecurityCipher bc = null;
        String content = "";

        
        try {
            bc = new SecurityCipher(sessionStub.getAESKey(username));
            
            if(bc.genHash(message.getContent()).equals(receivedHash)) {
                content = bc.dec(message.getContent());
            }

        } catch (NoSuchAlgorithmException | RemoteException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        
        Request req = Request.fromString(content);
        
        switch (req.getOperationType()) {
            case 4:
                this.buy(req, bc);
                
                return;
            case 5:
                this.add(req, bc);

                return;
            case 6:
                this.update(req, bc);
        
                return;
            case 7:
                this.remove(req, bc);

                return;
            default:
                return;
        }

    }

    @Override
    public RSAKey getPuKey() throws RemoteException {
        return rsa.getPublicKey();
    }

    private Message searchByName(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");        
        
        List<Car> named_cars = dbStub.findCar(req.getName());
        
        if (named_cars == null || named_cars.isEmpty()) {
            response.setContent("no cars available for this name");
        } else {
            StringBuilder responseString = new StringBuilder();
            for (Car car : named_cars) {
                responseString.append(car.toString());
                responseString.append("\n");
            }
            
            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;
    }

    private Message searchByRenavam(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");
        
        Car searchedCar = dbStub.find(req.getRenavam());
        
        if (searchedCar == null) {
            response.setContent("cannot find the vehicle for renavam " + req.getRenavam());
        } else {
            response.setContent(searchedCar.toString());
        }
        
        this.prepare(response, bc);

        return response;
    }

    private Message list(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");
                
        List<Car> carList = dbStub.getSorted();
        
        if (carList.isEmpty()) {
            response.setContent("no cars available in the system");
        } else {
            StringBuilder responseString = new StringBuilder();
            for (Car car : carList) {
                responseString.append(car.toString());
                responseString.append("\n");
            }

            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;
    }

    private Message listByCategory(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");

        List<Car> economyCars = new LinkedList<>();
        List<Car> intermediateCars = new LinkedList<>();
        List<Car> executiveCars = new LinkedList<>();
        
        List<Car> carList = dbStub.getSorted();
        
        if (carList.isEmpty()) {
            response.setContent("no cars available in the system");
        } else {
            
            for (Car car : carList) {

                switch (car.getCategory()) {
                    case ECONOMY:
                        economyCars.add(car);                    
                        break;
                    case INTERMEDIATE:
                        intermediateCars.add(car);
                        break;
                    case EXECUTIVE:
                        executiveCars.add(car);
                        break;
                    default:
                        System.err.println("DEALER: This type of car doesn't exist");
                        break;
                }

            }

            StringBuilder responseString = new StringBuilder();
                
            responseString.append("ECONOMY CARS:\n");
            
            for (Car car : economyCars) {
                responseString.append(car.toString());
                responseString.append("\n");
            }

            responseString.append("\n");
            responseString.append("INTERMEDIATE CARS:\n");

            for (Car car : intermediateCars) {
                responseString.append(car.toString());
                responseString.append("\n");
            }

            responseString.append("\n");
            responseString.append("EXECUTIVE CARS:\n");

            for (Car car : executiveCars) {
                responseString.append(car.toString());
                responseString.append("\n");
            }

            responseString.append("\n");

            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;
    }
    
    private Message add(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");

        boolean attempt = dbStub.create(convert(req.getSelectedOption()), req.getRenavam(), req.getName(), req.getProductionYear(), req.getPrice());
        
        if (attempt) {
            response.setContent("Successfully add the car\nPlease check the stock");
        } else {
            response.setContent("This car is already registred");
        }

        this.prepare(response, bc);

        return response;
    }

    private Message remove(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");
        
        boolean attempt = dbStub.delete(req.getRenavam());

        if (attempt) {
            response.setContent("Successfully remove the car\nPlease check the stock");
        } else {
            response.setContent("Cannot remove the car");
        }
        
        this.prepare(response, bc);

        return response;
    }

    private Message update(Request req, SecurityCipher bc) throws RemoteException {        
        Message response  = new Message("", "");
        
        boolean attempt = dbStub.update(convert(req.getSelectedOption()), req.getRenavam(), req.getName(), req.getProductionYear(), req.getPrice());

        if (attempt) {
            response.setContent("Successfully update the car\nPlease check the stock");
        } else {
            response.setContent("Cannot update the car");
        }
        
        this.prepare(response, bc);

        return response;
    }
    
    private Message stock(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");

        // TODO: Arrumar a exibição de stock no caso de ser só um, criando um outro método suponho eu
        String quantity = dbStub.getStock();

        if (quantity == null) {
            response.setContent("There is no cars registred...");
        } else {
            StringBuilder responseString = new StringBuilder();
    
            responseString.append("""
                    = = = = = = = =  CAR STOCK  = = = = = = = =
                    """);
            responseString.append("\tThere is " + quantity + " cars on stock\n");
            responseString.append("""
                    = = = = = = = = = = = = = = = = = = = = = =
                    """);
    
            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;
    }

    private Message stockByName(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");

        String quantity = dbStub.getStock();

        if (quantity == null) {
            response.setContent("There is no cars registred...");
        } else {
            response.setContent(quantity);
        }

        this.prepare(response, bc);

        return response;
    }

    private Message buy(Request req, SecurityCipher bc) throws RemoteException {
        Message response = new Message("", "");
        
        Car purchased_car = dbStub.find(req.getRenavam());

        if (purchased_car == null) {
            response.setContent("This car isn't available");
        } else {
            System.out.println("Antes de remover o carro");
            dbStub.delete(req.getRenavam());
            System.out.println("Depois de remover o carro");

            StringBuilder responseString = new StringBuilder(); 
            
            responseString.append("Acquired car:\n");
            responseString.append(purchased_car);
            responseString.append("\n");
    
            response.setContent(responseString.toString());
        }

        this.prepare(response, bc);

        return response;
    }

    private void prepare(Message message, SecurityCipher bc){
        try {

            message.setContent(bc.enc(message.getContent()));
            message.setHash(bc.genHash(message.getContent()));
            message.setHash(rsa.sign(message.getHash()));

        } catch (InvalidKeyException e) {
            e.printStackTrace();
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
        }
    }

    private CarType convert(int value) {
        switch (value) {
            case 0:
                return CarType.ECONOMY;
            case 1:
                return CarType.INTERMEDIATE;
            case 2:
                return CarType.EXECUTIVE;            
            default:
                return CarType.UNDEFINED;
        }
    }

    private void init() {
        try {

            Registry reg = LocateRegistry.getRegistry( "localhost", ServicePorts.SESSION_PORT.getValue() + servicesId );
            sessionStub = (SessionService) reg.lookup("Session" + servicesId);
            // Registry reg = LocateRegistry.getRegistry( "localhost", ServicePorts.SESSION_PORT.getValue() );
            // sessionStub = (SessionService) reg.lookup( "Session" );
            Registry dbReg = LocateRegistry.getRegistry( "localhost", ServicePorts.DATABASE_PORT.getValue() + servicesId );
            dbStub = (DatabaseService) dbReg.lookup("Database" + servicesId);
            
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } 
    }
}