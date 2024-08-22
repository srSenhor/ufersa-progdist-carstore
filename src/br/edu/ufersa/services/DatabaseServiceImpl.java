package br.edu.ufersa.services;

import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import br.edu.ufersa.entities.Car;
import br.edu.ufersa.services.skeletons.DatabaseService;
import br.edu.ufersa.utils.CarType;

public class DatabaseServiceImpl implements DatabaseService {

    // TODO: Usar Lock e Unlock por aqui

    private static ConcurrentHashMap<Long, Car> cars;
    private static ConcurrentHashMap<String, Integer> carsStock;
    private static DatabaseServiceImpl instance;
    
    private DatabaseServiceImpl() {
        cars = new ConcurrentHashMap<>();
        carsStock = new ConcurrentHashMap<>();

        this.init();
    }

    public static DatabaseServiceImpl getInstance() {
        
        if (instance == null) {
            instance = new DatabaseServiceImpl();
        }

        return instance;

    }

    public Car find(long renavam) {
        Car car = cars.get(renavam);

        if (car != null) {
            System.out.println("DATABASE: Founded -> \n" + car.toString());
            return car;
        }
        
        System.err.println("DATABASE: ERROR! This car isn't in the database");
        return car;
    }
    
    public List<Car> findCar(String name) {
        List<Car> car_list = new LinkedList<>();
        
        if (cars.isEmpty()) {
            System.out.println("DATABASE: ERROR! There is no cars in the database");
        } else {
            cars.forEach((Long renavam, Car car) -> {
                if (car.getName().equalsIgnoreCase(name)) {
                    car_list.add(car);
                }
            });

            System.out.println("DATABASE: Returning the list of car " + name);
        }

        return car_list;
    }

    public String getStock() {
        if (cars.size() == 0) {
            System.err.println("DATABASE: ERROR! There is no cars in database");
            return "There is no cars registred...";
        }

        StringBuilder responseString = new StringBuilder();
    
        // responseString.append("""
        //         = = = = = =  CAR STOCK  = = = = = =
        //         """);

        carsStock.forEach((String name, Integer quant) -> {
            responseString.append(name);
            responseString.append("\t");
            responseString.append("x" + quant);
            responseString.append("\n");
        });

        // responseString.append("""
        //         = = = = = = = = = = = = = = = = = =
        //         """);
        
        System.out.println("DATABASE: Sending stock");
        return responseString.toString();
        
    }

    public boolean create(CarType carType, long renavam, String name, int productionYear, float price) throws RemoteException {

        Car car = cars.get(renavam);

        if (car != null) {
            System.err.println("DATABASE: ERROR! This car already exists in the database");
            return false;
        }
        
        car = new Car(carType, renavam, name.toUpperCase(), productionYear, price);
        cars.put(renavam, car);

        Integer quant_car = carsStock.get(name.toUpperCase());
        if (quant_car == null) {
            carsStock.put(name.toUpperCase(), 1);
        } else {
            carsStock.put(name.toUpperCase(), ++quant_car);
        }

        return true;

    }

    public boolean update(CarType carType, long renavam, String name, int productionYear, float price) throws RemoteException {
        
        Car car = cars.get(renavam);

        if (car == null) {
            System.err.println("DATABASE: ERROR! This car isn't in the database");
            return false;
        }

        Integer quant_car = carsStock.get(car.getName().toUpperCase());

            if (quant_car > 0) {
                carsStock.put(car.getName(), --quant_car);
            } 

            car.setProductionYear(productionYear);
            car.setCategory(carType);
            car.setName(name);
            car.setPrice(price);
            
            cars.put(renavam, car);

            quant_car = carsStock.get(car.getName());
            
            if (quant_car == null) {
                carsStock.put(car.getName().toUpperCase(), 1);
            } else {
                carsStock.put(car.getName().toUpperCase(), ++quant_car);
            }

            return true;
    }

    public boolean delete(long renavam) {

        Car car = cars.remove(renavam);
        
        if (car == null) {
            System.err.println("DATABASE: ERROR! Cannot find this car in database");
            return false;
        } else {
            Integer quant_car = carsStock.get(car.getName());

            if (quant_car > 0) {
                carsStock.put(car.getName(), --quant_car);
            } 

            return true;
        }


    }

    public List<Car> getSorted(){
        List<Car> car_list = new LinkedList<>();
        
        if (cars.isEmpty()) {
            System.out.println("DATABASE: ERROR! There is no cars in the database");
        } else {
            cars.forEach((Long renavam, Car car) -> {
                car_list.add(car);
            });

            car_list.sort(new Comparator<Car>() {
                @Override
                public int compare(Car o1, Car o2) {
                    return o1.getName().compareTo(o2.getName());
                    
                }            
            });
            System.out.println("DATABASE: Returning the sorted list of cars");
        }
        
        return car_list;
    }

    private void init() {
        try {

            this.create(CarType.EXECUTIVE, 14926874359l, "audi a3", 2019, 50000f);
            this.create(CarType.ECONOMY, 72439120382l, "nissan march", 2012, 86000f);
            this.create(CarType.INTERMEDIATE, 51029874625l, "toyota etios", 2017, 70000f);
            this.create(CarType.ECONOMY, 14243939238l, "ford ka", 2009, 90000f);
            this.create(CarType.EXECUTIVE, 29018475092l, "chevrolet cruze", 2021, 40000f);
            this.create(CarType.ECONOMY, 73849201742l, "nissan march", 2015, 65000f);
            this.create(CarType.EXECUTIVE, 45829637102l, "toyota corolla", 2022, 42000f);
            this.create(CarType.INTERMEDIATE, 37905164293l, "ford ka sedan", 2018, 60000f);
            this.create(CarType.EXECUTIVE, 90583274625l, "honda civic", 2020, 45000f);
            this.create(CarType.ECONOMY, 62458390271l, "hyundai hb20s", 2012, 80000f);
            this.create(CarType.ECONOMY, 87251903416l, "fiat novo uno", 2010, 95000f);
            this.create(CarType.INTERMEDIATE, 18573964205l, "renault logan", 2016, 62000f);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
