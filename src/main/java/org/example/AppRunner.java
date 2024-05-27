package org.example;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.*;
public class AppRunner {
    private Escalonador escalonador = new Escalonador();
    private ArrayList<Fila> filasList = new ArrayList<>();
    private double lastEventTime = 0.0;
    private Randomizer randomizer;

    public void setup() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("FileStarter.yml");
        Map<String, Object> obj = yaml.load(inputStream);
        fileSetup(obj);
        mainLoop();
    }

    public void mainLoop(){
        while(randomizer.canContinue()) {
            try {
                selectEvent();
            } catch (RuntimeException e) {
                System.out.println("No more pseudo random numbers available");
                break;
            }
        }
        printFinalResults();
    }

    private void selectEvent() {
        Evento nextEvent = escalonador.getNextEvento();
        switch (nextEvent.type) {
            case CHEGADA -> {
                handleChegada(nextEvent);
            }
            case SAIDA -> {
                handleSaida(nextEvent);
            }
            case PASSAGEM -> {
                handlePassagem(nextEvent);
            }
        }
    }

    private void handleChegada(Evento evento) {
        acumulaTempo(evento);
        Fila fila = evento.fila;
        if (fila.getPopulation() < fila.getCapacity()) {
            fila.in();
            if (fila.getPopulation() <= fila.getServers()) {
                Fila destino = getPassagem(fila);
                if(destino != null){
                    scheduleEvent(EventTypeEnum.PASSAGEM, evento.getTime(), evento.getFila(), destino);
                } else {
                    scheduleEvent(EventTypeEnum.SAIDA,evento.getTime(),evento.getFila(),null);
                }
            }
        } else {
            fila.loss();
        }
        scheduleEvent(EventTypeEnum.CHEGADA, evento.getTime(), evento.getFila(), null);
    }

    private void handleSaida(Evento evento) {
        acumulaTempo(evento);
        Fila fila = evento.fila;
        fila.out();
        if (fila.getPopulation() >= fila.getServers()) {
            Fila destino = getPassagem(fila);
            if(destino != null){
                scheduleEvent(EventTypeEnum.PASSAGEM, evento.getTime(), evento.getFila(), destino);
            } else {
                scheduleEvent(EventTypeEnum.SAIDA, evento.getTime(), evento.getFila(), null);
            }
        }
    }

    private void handlePassagem(Evento evento) {
        acumulaTempo(evento);
        Fila origem = evento.getFila();
        Fila destino = evento.getDestino();
        origem.out();
        if (origem.getPopulation() >= origem.getServers()) {
            Fila destino2 = getPassagem(origem);
            if (destino2 != null) {
                scheduleEvent(EventTypeEnum.PASSAGEM, evento.getTime(), evento.getFila(), destino2);
            } else {
                scheduleEvent(EventTypeEnum.SAIDA, evento.getTime(), evento.getFila(), null);
            }
        }
        if (destino.getPopulation() < destino.getCapacity()) {
            destino.in();
            if (destino.getPopulation() <= destino.getServers()) {
                Fila destino2 = getPassagem(destino);
                if (destino2 != null) {
                    scheduleEvent(EventTypeEnum.PASSAGEM, evento.getTime(), destino, destino2);
                } else {
                    scheduleEvent(EventTypeEnum.SAIDA, evento.getTime(), destino, null);
                }
            }
        } else {
            destino.loss();
        }
    }

    private void acumulaTempo(Evento evento) {
        for (Fila fila : filasList) {
            double timeBefore = fila.times.getOrDefault(fila.getPopulation(),0.0);
            double timeToAdd = evento.getTime() - lastEventTime;
            double newTime = timeBefore + timeToAdd;
            fila.setTimes(fila.getPopulation(), newTime);
        }
        lastEventTime = evento.getTime();
    }

    private void scheduleEvent(EventTypeEnum eventTypeToSchedule, double tempo, Fila origem, Fila destino) {
        double tempoDemandado = 0;
        switch (eventTypeToSchedule) {
            case CHEGADA -> {
                tempoDemandado = timeDemanded(origem.getMinArrival(), origem.getMaxArrival());
                Evento eventoToSchedule = new Evento(
                        EventTypeEnum.CHEGADA,
                        tempo + tempoDemandado,
                        origem,
                        null
                );
                escalonador.addEvento(eventoToSchedule);
            }
            case SAIDA -> {
                tempoDemandado = timeDemanded(origem.getMinService(), origem.getMaxService());
                Evento eventoToSchedule = new Evento(
                        EventTypeEnum.SAIDA,
                        tempo + tempoDemandado,
                        origem,
                        null
                );
                escalonador.addEvento(eventoToSchedule);
            }
            case PASSAGEM -> {
                tempoDemandado = timeDemanded(origem.getMinService(), origem.getMaxService());
                Evento eventoToSchedule = new Evento(
                        EventTypeEnum.PASSAGEM,
                        tempo + tempoDemandado,
                        origem,
                        destino
                );
                escalonador.addEvento(eventoToSchedule);
            }
        }
    }

    private Fila getPassagem(Fila origem){
        double randomNum = randomizer.random();
        double sum = 0.0;
        Fila destino = null;
        for (Fila.Destination destination : origem.getDestinations()) {
            sum += destination.getProbability();
            if (sum >= randomNum) {
                destino = destination.getFila();
                break;
            }
        }
        return destino;
    }

    private double timeDemanded(double minTime, double maxTime) {
        double randomNumber = randomizer.random();
        return (maxTime - minTime) * randomNumber + minTime;
    }

    private void printFinalResults() {
        System.out.println("--- RESULTADOS FINAIS ---\n");
        for (Fila fila : filasList) {
            System.out.println("\nFila: " + fila.getName());
            System.out.println("-------------------------");
            for (Map.Entry<Integer, Double> time : fila.times.entrySet()){
                int population = time.getKey();
                double popTime = time.getValue();
                double percentage = popTime / lastEventTime * 100;
                System.out.println("- Clients : " + population + "    Time: " + popTime + "    Percentage: " + String.format("%.2f", percentage) + "%");
            }
            System.out.println("- Perdas: " + fila.getLoss());
        }

        System.out.println("\nTempo total de simulação: " + lastEventTime);
    }

    private void fileSetup(Map<String, Object> obj) {
        int rounds = obj.get("rndnumbersPerSeed") != null ? (Integer) obj.get("rndnumbersPerSeed") : 100_000;
        Integer seed = (Integer) obj.get("seed");
        
        ArrayList<Double> pseudoRandom = (ArrayList<Double>) obj.get("rndnumbers");

        if (seed == null && pseudoRandom != null) {
            randomizer = new Randomizer(null);
            randomizer.addPseudoRandomList(pseudoRandom);
        } else if (seed == null) {
            int randomSeed = (int) Math.floor(Math.random() * 1000);
            randomizer = new Randomizer(randomSeed, rounds);
        } else {
            randomizer = new Randomizer(seed, rounds);
        }
    
        LinkedHashMap<String, Object> queues = (LinkedHashMap<String, Object>) obj.get("queues");

        for (Map.Entry<String, Object> entry : queues.entrySet()) {
            LinkedHashMap<String, Object> queue = (LinkedHashMap<String, Object>) entry.getValue();
            String name = entry.getKey();
            Integer capacity = (Integer) queue.get("capacity");
            int servers = (int) queue.get("servers");
            Double minArrival = (Double) queue.get("minArrival");
            Double maxArrival = (Double) queue.get("maxArrival");
            double minService = (double) queue.get("minService");
            double maxService = (double) queue.get("maxService");
            Fila fila = null;
            if (minArrival != null && maxArrival != null) {
                fila = new Fila(name, capacity, servers, minArrival, maxArrival, minService, maxService);
            } else {
                fila = new Fila(name, capacity, servers, minService, maxService);
            }
            if (fila != null) {
                filasList.add(fila);
            }
        }

        List<LinkedHashMap<String, Object>> network = (List<LinkedHashMap<String, Object>>) obj.get("network");

        //podem ou não haver conexões
        if(network != null){
            for(HashMap<String, Object> connection : network){
                String origin = (String) connection.get("source");
                String destination = (String) connection.get("target");
                double probability = (double) connection.get("probability");
                Fila originFila = filasList.stream().filter(fila -> fila.getName().equals(origin)).collect(Collectors.toList()).get(0);
                Fila destinationFila = filasList.stream().filter(fila -> fila.getName().equals(destination)).collect(Collectors.toList()).get(0);
                originFila.addDestination(destinationFila, probability);
            }
        }

        LinkedHashMap<String, Object> arrivals = (LinkedHashMap<String, Object>) obj.get("arrivals");
        
        for (Map.Entry<String, Object> entry : arrivals.entrySet()) {
            String name = entry.getKey();
            double arrivalTime = (double) entry.getValue();
            Fila fila = filasList.stream().filter(fila1 -> fila1.getName().equals(name)).collect(Collectors.toList()).get(0);
            Evento evento = new Evento(EventTypeEnum.CHEGADA, arrivalTime, fila, null);
            escalonador.addEvento(evento);
        }

    }
}