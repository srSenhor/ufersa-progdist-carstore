package br.edu.ufersa.entities;

import java.io.Serializable;

import br.edu.ufersa.utils.CarType;

public class Car implements Serializable {

    private static final long serialVersionUID = 1L;    
    private CarType category;
    private long renavam;
    private String name;
    private int productionYear;
    private float price;
    
    public Car(CarType category, long renavam, String name, int productionYear, float price) {
        setCategory(category);
        setRenavam(renavam);
        setName(name);
        setProductionYear(productionYear);
        setPrice(price);
    }

    public CarType getCategory() {
        return category;
    }

    public void setCategory(CarType category) {
        this.category = category;
    }

    public long getRenavam() {
        return renavam;
    }

    public void setRenavam(long renavam) {
        
        if (renavam > 9999999999f && renavam <= Float.MAX_VALUE) {
            this.renavam = renavam;
        } else {
            this.renavam = -1;
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        
        if (productionYear > 1886 && productionYear < 2500) {
            this.productionYear = productionYear;
        } else {
            this.productionYear = 1886;
        }

    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {

        if (price >= 0 && price <= Float.MAX_VALUE) {
            this.price = price;
        } else {
            this.price = 130000f;
        }

    }

    @Override
    public String toString() {
        return  "= = = = = =  " + name + "  = = = = = =\n" +
                "Renavam   " + renavam      + "\n" +
                "Ano       " + productionYear      + "\n" +
                "Preco R$  " + price        + "\n" +
                "Categoria " + category    + "\n" +
                "= = = = = = = = = = = = = = = = = = = \n";
    }

    

}