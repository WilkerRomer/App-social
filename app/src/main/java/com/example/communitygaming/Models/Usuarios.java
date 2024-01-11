package com.example.communitygaming.Models;

public class Usuarios {

    private String id;
    private String email;
    private String usuario;
    private String phone;
    private String imageProfile;
    private String imageFondo;
    private long fecha;
    private long lastConnect;
    private boolean online;

    public Usuarios(){

    }

    public Usuarios(String id, String email, String usuario, String phone, String imageProfile, String imageFondo, long fecha, long lastConnect, boolean online) {
        this.id = id;
        this.email = email;
        this.usuario = usuario;
        this.phone = phone;
        this.imageProfile = imageProfile;
        this.imageFondo = imageFondo;
        this.fecha = fecha;
        this.lastConnect = lastConnect;
        this.online = online;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getImageFondo() {
        return imageFondo;
    }

    public void setImageFondo(String imageFondo) {
        this.imageFondo = imageFondo;
    }

    public long getLastConnect() {
        return lastConnect;
    }

    public void setLastConnect(long lastConnect) {
        this.lastConnect = lastConnect;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
