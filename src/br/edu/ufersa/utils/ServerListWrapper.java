package br.edu.ufersa.utils;

import java.util.HashMap;

public class ServerListWrapper {

    private HashMap<Integer, Integer> serverList;
    
    public ServerListWrapper() {
        this.serverList = new HashMap<>();
    }

    public ServerListWrapper(int serverQuantity) {
        this.serverList = new HashMap<>(serverQuantity);
    }

    public HashMap<Integer, Integer> getServerList() {
        return serverList;
    }

    public void put(int key, int value) {
        this.serverList.put(key, value);
    }

}