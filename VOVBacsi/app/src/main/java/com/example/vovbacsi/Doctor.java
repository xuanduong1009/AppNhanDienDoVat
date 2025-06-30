package com.example.vovbacsi;

public class Doctor {
    private String name;
    private String specialization;
    private String degree;
    private String birthYear;
    private String department;
    private String unit;
    private String specialty;
    private String treatableDiseases;
    private double price;
    private String imageUrl;

    // Default constructor (required for Firebase)
    public Doctor() {}

    // Full constructor to initialize all fields
    public Doctor(String name, String specialization, String degree, String birthYear,
                  String department, String unit, String specialty, String treatableDiseases,
                  double price, String imageUrl) {
        this.name = name;
        this.specialization = specialization;
        this.degree = degree;
        this.birthYear = birthYear;
        this.department = department;
        this.unit = unit;
        this.specialty = specialty;
        this.treatableDiseases = treatableDiseases;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getter and setter methods for each attribute
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getBirthYear() { return birthYear; }
    public void setBirthYear(String birthYear) { this.birthYear = birthYear; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getTreatableDiseases() { return treatableDiseases; }
    public void setTreatableDiseases(String treatableDiseases) { this.treatableDiseases = treatableDiseases; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
