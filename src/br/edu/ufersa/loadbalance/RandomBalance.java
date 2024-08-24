package br.edu.ufersa.loadbalance;

import java.security.SecureRandom;

import br.edu.ufersa.utils.ServerListWrapper;

public class RandomBalance implements LoadBalance {

    @Override
    public int balance(ServerListWrapper serverList) {
        return new SecureRandom().nextInt(serverList.getServerList().size()) + 1;
    }
    
}