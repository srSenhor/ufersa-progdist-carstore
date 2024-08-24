package br.edu.ufersa.loadbalance;

import java.util.HashMap;

import br.edu.ufersa.utils.ServerListWrapper;

public class LeastConnectionsBalance implements LoadBalance {

    @Override
    public int balance(ServerListWrapper serverList) {

        HashMap<Integer, Integer> map = serverList.getServerList();

        int leastConnectionsServer = 1;
        int leastConnection = map.get(1);

        for (int i = 1; i <= 3; i++) {
            
            int nextConnections = map.get(i);
            
            if ( nextConnections < leastConnection) {
                leastConnection = nextConnections;
                leastConnectionsServer = i;
            }
            
        }

        serverList.put(leastConnectionsServer, ++leastConnection);

        return leastConnectionsServer;

    }

    public void disconnect(ServerListWrapper serverList, int serverId) {

        int connection = serverList.getServerList().get(serverId);
        serverList.put(serverId, --connection);

    }
    
}
