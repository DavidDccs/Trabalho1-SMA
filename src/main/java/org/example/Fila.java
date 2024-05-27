package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fila{
    private String name;
    private int population;
    private Integer capacity;
    private int servers;
    private double minArrival;
    private double maxArrival;
    private double minService;
    private double maxService;
    private int loss;
    public Map<Integer, Double> times;
    public ArrayList<Destination> destinations;

    public Fila(String name, Integer capacity, int servers, double minArrival, double maxArrival, double minService, double maxService){
        this.name = name;
        this.servers = servers;
        this.capacity = capacity != null && capacity > 0 ? capacity : Integer.MAX_VALUE;
        this.minArrival = minArrival;
        this.maxArrival = maxArrival;
        this.minService = minService;
        this.maxService = maxService;
        this.population = 0;
        this.loss = 0;
        this.times = new HashMap<>();
        this.destinations = new ArrayList<>();
    }

    public Fila(String name, Integer capacity, int servers, double minService, double maxService){
        this.name = name;
        this.servers = servers;
        this.capacity = capacity != null && capacity > 0 ? capacity : Integer.MAX_VALUE;
        this.minArrival = -1.0;
        this.maxArrival = -1.0;
        this.minService = minService;
        this.maxService = maxService;
        this.population = 0;
        this.loss = 0;
        this.times = new HashMap<>();
        this.destinations = new ArrayList<>();
    }

    
    public void in(){
        population = population + 1;
    }
    
    public void out(){
        population = population - 1;
    }
    
    public void loss(){
        loss = loss + 1;
    }
    
    public int getPopulation(){
        return population;
    }
    
    public String getName(){
        return name;
    }

    public int getCapacity(){
        return capacity;
    }

    public int getServers(){
        return servers;
    }

    public double getMinArrival(){
        return minArrival;
    }

    public double getMaxArrival(){
        return maxArrival;
    }

    public double getMinService(){
        return minService;
    }

    public double getMaxService(){
        return maxService;
    }

    public int getLoss(){
        return loss;
    }

    public void addDestination(Fila fila, double probability){
        destinations.add(new Destination(fila, probability));
        destinations.sort((d1, d2) -> Double.compare(d1.getProbability(), d2.getProbability()));
    }

    public void setTimes(int key, double time){
        if (times.containsKey(key)){
            times.replace(key, time);
        } else {
            times.put(key, time);
        }
    }

    public ArrayList<Destination> getDestinations(){
        return destinations;
    }

    public Fila getDestionation(double prob){
        //ele faz o calculo da prob aqui em vez de passar por parametro
        double sum = 0.0;
        for(Destination destination : destinations){
            sum += destination.getProbability();
            if(sum >= prob){
                return destination.getFila();
            }
        }
        return null;
    }

    //usar para debuggar caso necessário
    public String toString(){
        return "Fila: " + name + "\n" +
                "Population: " + population + "\n" +
                "Capacity: " + capacity + "\n" +
                "Servers: " + servers + "\n" +
                "Min Arrival: " + minArrival + "\n" +
                "Max Arrival: " + maxArrival + "\n" +
                "Min Service: " + minService + "\n" +
                "Max Service: " + maxService + "\n" +
                "Loss: " + loss + "\n";
    }

    //usar para debuggar caso necessário
    public String getDestionationString(){
        String result = "";
        for(Destination destination : destinations){
            result += destination.getFila().getName() + " - " + destination.getProbability() + "\n";
        }
        return result;
    }
    public class Destination{
        Fila fila;
        double probability;

        public Destination(Fila fila, double probability){
            this.fila = fila;
            this.probability = probability;
        }

        public Fila getFila(){
            return fila;
        }

        public double getProbability(){
            return probability;
        }
    }
}
