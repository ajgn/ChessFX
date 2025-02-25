package com.chess.gui.chessfx;

import java.util.Date;
import java.util.Objects;

public class Partida {
    long timestamp;
    String nombre;
    String partida;

    public Partida(long timestamp, String nombre, String partida) {
        this.timestamp = timestamp;
        this.nombre = nombre;
        this.partida = partida;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Date getFecha() {
        return new Date(this.timestamp);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partida partida = (Partida) o;
        return timestamp == partida.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
        return getFecha() + " " + nombre + " " + partida;
    }

}
