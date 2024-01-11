package com.example.communitygaming.Models;

public class Post {

    private String id;
    private String title;
    private String description;
    private String Image1;
    private String Image2;
    private String IdUsuario;
    private String category;
    private long fecha;

    public Post(){

    }

    public Post(String id, String title, String description, String image1, String image2, String idUsuario, String category, long fecha) {
        this.id = id;
        this.title = title;
        this.description = description;
        Image1 = image1;
        Image2 = image2;
        IdUsuario = idUsuario;
        this.category = category;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage1() {
        return Image1;
    }

    public void setImage1(String image1) {
        Image1 = image1;
    }

    public String getImage2() {
        return Image2;
    }

    public void setImage2(String image2) {
        Image2 = image2;
    }

    public String getIdUsuario() {
        return IdUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        IdUsuario = idUsuario;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
