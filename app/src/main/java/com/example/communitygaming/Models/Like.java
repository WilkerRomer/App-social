package com.example.communitygaming.Models;

public class Like {

    private String id;
    private String idPost;
    private String idUsuario;
    private long fecha;

    public Like(){

    }

    public Like(String id, String idPost, String idUsuario, long fecha) {
        this.id = id;
        this.idPost = idPost;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
