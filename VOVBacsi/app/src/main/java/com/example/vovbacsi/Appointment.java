package com.example.vovbacsi;

public class Appointment {
    private String doctorName;
    private String specialization;
    private String appointmentTime;

    // Constructor
    public Appointment(String doctorName, String specialization, String appointmentTime) {
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.appointmentTime = appointmentTime;
    }

    // Getter methods
    public String getDoctorName() {
        return doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    // Setter methods
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
}
