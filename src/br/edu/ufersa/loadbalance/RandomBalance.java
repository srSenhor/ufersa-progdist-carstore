package br.edu.ufersa.loadbalance;

import java.util.Random;

import br.edu.ufersa.utils.ServerListWrapper;

public class RandomBalance implements LoadBalance {

    @Override
    public int balance(ServerListWrapper serverList) {
        return new Random().nextInt(serverList.getServerList().size()) + 1;
    }
    
}