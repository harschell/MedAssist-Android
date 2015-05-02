package com.example.root.medassist.model;

public class Doctor {
    private String title, thumbnailUrl, address,registered,phoneNum;
    private int consultation;
    private double rating;
    private String speciality;

    public Doctor() {
    }

    public Doctor(String name, String thumbnailUrl, String add, String registered, String phoneNum, int consultation, double rating,
                  String special) {
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.address = add;
        this.rating = rating;
        this.speciality = special;
        this.registered = registered;
        this.phoneNum = phoneNum;
        this.consultation = consultation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
    public String getRegistration(){
        return registered;
    }
    public void setRegistered(int registered){
        if (registered == 1)
            this.registered = "Yes";
        else
            this.registered = "No";
    }
    public String getPhone(){
        return phoneNum;
    }
    public void setPhoneNum(String PhoneNum){
        this.phoneNum = PhoneNum;
    }
    public int getConsultation(){
        return consultation;
    }
    public void setConsultation(int consultation){
        this.consultation = consultation;
    }

}