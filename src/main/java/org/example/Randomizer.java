package org.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Randomizer {
    Double seed = 1.0;
    final int a = 1987571865;
    final int c = 174153;
    final Double m = Math.pow(2, 24);
    int maxRounds = -1;
    int currentRound = 0;

    Queue<Double> pseudoRandom = new LinkedList<Double>();

    public Randomizer(Integer seed){
        if(seed == null){
            this.seed = null;
            return;
        }
        this.seed = (Double) seed.doubleValue();
    }

    public Randomizer(Integer seed, int maxRounds){
        this.seed = (Double) seed.doubleValue();
        this.maxRounds = maxRounds;
    }

    public void addPseudoRandom (Double pseudoRandom){
        this.pseudoRandom.add(pseudoRandom);
    }

    public void addPseudoRandomList (ArrayList<Double> pseudoRandom){
        this.pseudoRandom.addAll(pseudoRandom);
    }

    public Double random(){
        if(seed == null){
            Double number =  getPseudoRandom();
            if(number == null){
                //caso não tenha mais números pseudo randomicos antes do novo "round"
                throw new RuntimeException("No pseudo random numbers available");
            }
            return number;
        }
        seed = (a * seed + c) % m;
        currentRound++;
        return seed / m;
    }

    public boolean canContinue(){
        if(maxRounds == -1){
            return !this.pseudoRandom.isEmpty();
        }
        return currentRound < maxRounds;
    }

    public void setSeed(Double seed){
        this.seed = seed;
    }

    public Double getPseudoRandom(){
        return pseudoRandom.poll();
    }

}
