package br.edu.ufersa.server.services.skeletons;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import br.edu.ufersa.entities.Car;
import br.edu.ufersa.utils.CarType;

public interface DatabaseService extends Remote {
    Car find(long renavam) throws RemoteException;
    List<Car> findCar(String name) throws RemoteException;
    List<Car> getSorted() throws RemoteException;
    String getStock() throws RemoteException;
    boolean create(CarType carType, long renavam, String name, int productionYear, float price) throws RemoteException;
    boolean update(CarType carType, long renavam, String name, int productionYear, float price) throws RemoteException;
    boolean delete(long renavam) throws RemoteException;
}
