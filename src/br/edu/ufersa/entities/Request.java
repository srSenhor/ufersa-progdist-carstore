package br.edu.ufersa.entities;

import java.io.Serializable;

import br.edu.ufersa.utils.CarType;
import br.edu.ufersa.utils.UserType;

public class Request implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private int operationType;
    private UserType userType;    
    private CarType category;
    private long renavam;
    private String name;
    private String username;
    private int productionYear;
    private float price;
    
    public Request(int operationType, UserType userType, CarType category, long renavam, String name, String username,
            int productionYear, float price) {
        this.operationType = operationType;
        this.userType = userType;
        this.category = category;
        this.renavam = renavam;
        this.name = name;
        this.username = username;
        this.productionYear = productionYear;
        this.price = price;
    }

    public int getOperationType() {
        return operationType;
    }
    public UserType getUserType() {
        return userType;
    }
    public CarType getCategory() {
        return category;
    }
    public long getRenavam() {
        return renavam;
    }
    public String getName() {
        return name;
    }
    public String getUsername() {
        return username;
    }
    public int getProductionYear() {
        return productionYear;
    }
    public float getPrice() {
        return price;
    }

    public static Request fromString(String text) {
        String fields[] = text.split("/");
        int operationType = Integer.parseInt(fields[0]);
        UserType userType = UserType.valueOf(fields[1]);
        String username = fields[2];
        long renavam = Long.parseLong(fields[3]);
        String name = fields[4];
        int productionYear = Integer.parseInt(fields[5]);
        float price = Float.parseFloat(fields[6]);
        CarType category = CarType.valueOf(fields[7]);

        return new Request(operationType, userType, category, renavam, name, username, productionYear, price);
    }

}
