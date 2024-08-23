package br.edu.ufersa.loadbalance;

import br.edu.ufersa.utils.ServerListWrapper;

@FunctionalInterface   
public interface LoadBalance {
    int balance( ServerListWrapper serverList );
} 
