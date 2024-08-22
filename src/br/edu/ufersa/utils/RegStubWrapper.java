package br.edu.ufersa.utils;

import java.rmi.Remote;
import java.rmi.registry.Registry;

public class RegStubWrapper <T extends Remote>{

    private Registry registry;
    private T serviceStub;
    
    public RegStubWrapper(Registry registry, T serviceStub) {
        this.registry = registry;
        this.serviceStub = serviceStub;
    }

    public Registry getRegistry() {
        return registry;
    }

    public T getServiceStub() {
        return serviceStub;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setServiceStub(T serviceStub) {
        this.serviceStub = serviceStub;
    }

}
