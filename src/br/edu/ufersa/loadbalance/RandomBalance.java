package br.edu.ufersa.loadbalance;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import br.edu.ufersa.utils.RegStubWrapper;
import br.edu.ufersa.utils.ServicePorts;

public class RandomBalance <T extends Remote> implements LoadBalance <T> {

    @SuppressWarnings("unchecked")
    @Override
    public void balance(RegStubWrapper<T> wrapper, ServicePorts service, String serviceName, int serverQuantity) {

        int serverId = ((int) new Random().nextInt(serverQuantity - 1)) + 1;

        try {

            Registry reg = LocateRegistry.getRegistry( "localhost", service.getValue() + serverId );
            T stub = (T) reg.lookup(serviceName + serverId );

            wrapper.setRegistry(reg);
            wrapper.setServiceStub(stub);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }
    
}
