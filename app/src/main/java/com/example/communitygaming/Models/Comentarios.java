package com.example.communitygaming.Models;

public class Comentarios {

    private String id;
    private String comment;
    private String idUsuario;
    private String idPost;
    long fecha;

    public  Comentarios(){

    }

    public Comentarios(String id, String comment, String idUsuario, String idPost, long fecha) {
        this.id = id;
        this.comment = comment;
        this.idUsuario = idUsuario;
        this.idPost = idPost;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
