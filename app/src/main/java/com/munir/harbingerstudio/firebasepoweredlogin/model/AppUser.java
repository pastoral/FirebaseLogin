package com.munir.harbingerstudio.firebasepoweredlogin.model;

import android.location.Location;
import android.net.Uri;

/**
 * Created by AppUser on 8/18/2017.
 */

public class AppUser {
    private String uid;
    private String name;
    private String email;
    private String phoneNumber;
    private Uri photoURL;
    private String providerId;
    private String location;
    private String[] model;
    private String[] imei;
    private String userCategoryText;
    private String userCategoryImage;


    public AppUser() {
    }

    public AppUser(String uid, String name, String email, String phoneNumber, Uri photoURL, String providerId, String location, String[] model, String[] imei, String userCategoryText, String userCategoryImage) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.photoURL = photoURL;
        this.providerId = providerId;
        this.location = location;
        this.model = model;
        this.imei = imei;
        this.userCategoryText = userCategoryText;
        this.userCategoryImage = userCategoryImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public Uri getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(Uri photoURL) {
        this.photoURL = photoURL;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String[] getModel() {
        return model;
    }

    public void setModel(String[] model) {
        this.model = model;
    }

    public String[] getImei() {
        return imei;
    }

    public void setImei(String[] imei) {
        this.imei = imei;
    }

    public String getUserCategoryText() {
        return userCategoryText;
    }

    public void setUserCategoryText(String userCategoryText) {
        this.userCategoryText = userCategoryText;
    }

    public String getUserCategoryImage() {
        return userCategoryImage;
    }

    public void setUserCategoryImage(String userCategoryImage) {
        this.userCategoryImage = userCategoryImage;
    }
}