package br.edu.ufersa.loadbalance;

import java.rmi.Remote;

import br.edu.ufersa.utils.RegStubWrapper;
import br.edu.ufersa.utils.ServicePorts;

// @FunctionalInterface   
public interface LoadBalance <T extends Remote> {
    void balance( RegStubWrapper<T> wrapper, ServicePorts service, String serviceName, int serverQuantity );
} 
