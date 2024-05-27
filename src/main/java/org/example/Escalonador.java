package org.example;

import java.util.PriorityQueue;

public class Escalonador {
    PriorityQueue<Evento> eventos;

    public Escalonador(){
        eventos = new PriorityQueue<>();
    }

    public void addEvento(Evento evento){
        eventos.add(evento);
    }

    public Evento getNextEvento(){
        return eventos.poll();
    }

    public boolean hasEventos(){
        return !eventos.isEmpty();
    }
}
