package br.edu.ufersa.server;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.edu.ufersa.services.Auth;
import br.edu.ufersa.services.Database;
import br.edu.ufersa.services.Dealer;
import br.edu.ufersa.services.Session;
import br.edu.ufersa.utils.GUI;

/*
 * Para lançar todos os serviços nas determinadas portas
 */

public class Services {

    // private static final int SERVICE_ID = 1;
    // private static final int SERVICE_ID = 2;
    private static final int SERVICE_ID = 3;

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(4);

        Runnable finalAction = () -> {
            GUI.clearScreen();
            System.out.println("Services at port xxxx" + SERVICE_ID + " are already to use:");
        };

        CyclicBarrier barrier = new CyclicBarrier(4, finalAction);

        Runnable auth = () -> {
            sleep(3000);
            new Auth(SERVICE_ID);
            await(barrier, "Auth");
        };

        Runnable dealer = () -> {
            sleep(2000);
            new Dealer(SERVICE_ID);
            await(barrier, "Dealer");
        };
        
        Runnable session = () -> {
            sleep(1000);
            new Session(SERVICE_ID);
            await(barrier, "Session");
        };

        Runnable database = () -> {
            new Database(SERVICE_ID);
            await(barrier, "Database");
        };

        executor.execute(database);
        executor.execute(session);
        executor.execute(dealer);
        executor.execute(auth);

        executor.shutdown();

    }   

    private static void sleep(long mirillis) {
        try {
            Thread.sleep(mirillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void await(CyclicBarrier barrier, String debug) {
        try {
            System.out.println(debug + " bateu na barreira");
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

}