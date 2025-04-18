package com.mba.my_mechanic.models;

import androidx.annotation.NonNull;

public class Mechanic {
    public String name, email, phone, garage_name, jobs_completed, specialization;
    public boolean available;

    public Mechanic() {} // Firestore needs empty constructor

    public Mechanic(String name, String email, String phone, String garage_name, String jobs_completed, String specialization, boolean available) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.garage_name = garage_name != null ? garage_name : "Not available";
        this.jobs_completed = jobs_completed != null ? jobs_completed : "0";
        this.specialization = specialization != null ? specialization : "Not specified";
        this.available = available;
    }

    @NonNull
    @Override
    public String toString() {
        return name + "\n" + email + "\n" + phone + "\n" + garage_name + "\n" + jobs_completed + "\n" + specialization;
    }
}