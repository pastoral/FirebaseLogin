package com.munir.harbingerstudio.firebasepoweredlogin.model;

import android.location.Location;
import android.net.Uri;

/**
 * Created by AppUser on 8/18/2017.
 */

public class AppUser {
    private String uId;
    private String name;
    private String email;
    private String phoneNumber;
    private Location location;
    private String area;
    private String gender;
    private Uri photoUrl;


    public AppUser(String uId, String name, String email, Uri photoUrl) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public AppUser(String name, String email, Uri photoUrl) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public AppUser(String name, String email, Uri photoUrl, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
    }

    public AppUser(String uId, String name, String email, Uri photoUrl, String phoneNumber) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
    }

    public AppUser(String uId, String name, String email, String phoneNumber, Location location, String area, String gender, Uri photoUrl) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.area = area;
        this.gender = gender;
        this.photoUrl = photoUrl;
    }

    public AppUser(String name, String email, String phoneNumber, Location location, String area, String gender, Uri photoUrl) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.area = area;
        this.gender = gender;
        this.photoUrl = photoUrl;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }
}
