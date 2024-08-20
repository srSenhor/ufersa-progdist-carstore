package br.edu.ufersa.entities;

import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;

import br.edu.ufersa.utils.UserType;

public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private boolean isLogged;
    private String username;
    private String password;
    private UserType userType;
    private List<Car> acquiredCars;

    public User(String username, String password, UserType userType) {
        this.isLogged = false;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.acquiredCars = new LinkedList<>();
    }

    public boolean isLogged() {
        return isLogged;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserType getType() {
        return userType;
    }

    public void userLoggedIn(){
        this.isLogged = true;
    }
    public void userLoggedOut(){
        this.isLogged = false;
    }

    public void acquireCar(Car car){
        if (car != null) {
            this.acquiredCars.add(car);
        }
    }

    @Override
    public String toString() {
        return "User [isLogged=" + isLogged + ", login=" + username + ", password=" + password + ", userType=" + userType + "]";
    }



}
