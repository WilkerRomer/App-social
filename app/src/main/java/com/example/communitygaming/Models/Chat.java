package com.example.communitygaming.Models;

import java.util.ArrayList;

public class Chat {

    private String id;
    private String idUsuario1;
    private String idUsuario2;
    private int idNotification;
    private boolean isWriting;
    private long fecha;
    private ArrayList<String> ids;

    public Chat(){

    }

    public Chat(String id, String idUsuario1, String idUsuario2, int idNotification, boolean isWriting, long fecha, ArrayList<String> ids) {
        this.id = id;
        this.idUsuario1 = idUsuario1;
        this.idUsuario2 = idUsuario2;
        this.idNotification = idNotification;
        this.isWriting = isWriting;
        this.fecha = fecha;
        this.ids = ids;
    }

    public boolean isWriting() {
        return isWriting;
    }

    public void setWriting(boolean writing) {
        isWriting = writing;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getIdUsuario1() {
        return idUsuario1;
    }

    public void setIdUsuario1(String idUsuario1) {
        this.idUsuario1 = idUsuario1;
    }

    public String getIdUsuario2() {
        return idUsuario2;
    }

    public void setIdUsuario2(String idUsuario2) {
        this.idUsuario2 = idUsuario2;
    }

    public String getId() {
        return id;
    }

    public void setId(String idChat) {
        this.id = idChat;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public int getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(int idNotification) {
        this.idNotification = idNotification;
    }
}
