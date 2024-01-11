package com.example.communitygaming.Models;

public class SliderItem {

    String imageUrl;
    long fecha;

    public SliderItem(){

    }

    public SliderItem(String imageUrl, long fecha) {
        this.imageUrl = imageUrl;
        this.fecha = fecha;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
