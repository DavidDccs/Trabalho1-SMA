package org.example;

public class Evento implements Comparable<Evento> {
    EventTypeEnum type;
    Double time;
    Fila fila;
    Fila destino;

    public Evento(EventTypeEnum type, Double time, Fila filaCaller, Fila filaDestino){
        this.type = type;
        this.time = time;
        this.fila = filaCaller;
        this.destino = filaDestino;
    }

    public Evento(EventTypeEnum type, Double time, Fila filaCaller){
        this.type = type;
        this.time = time;
        this.fila = filaCaller;
    }

    public EventTypeEnum getType(){
        return type;
    }

    public Double getTime(){
        return time;
    }
    
    public Fila getFila(){
        return fila;
    }

    public Fila getDestino(){
        return destino;
    }

    public void setCaller(Fila newCaller){
        this.fila = newCaller;
    }

    @Override
    public int compareTo(Evento otherEvent) {
        return this.time.compareTo(otherEvent.time);
    }

}
