package br.edu.ufersa.loadbalance;

import br.edu.ufersa.utils.ServerListWrapper;

public class RoundRobinBalance implements LoadBalance {

    private static int currentRound;

    public RoundRobinBalance() {
        currentRound = 0;
    }

    @Override
    public int balance(ServerListWrapper serverList) {
        return currentRound++ % serverList.getServerList().size() + 1;
    }
    
}
