package com.example.vovbacsi.item;

public class HospitalItem {
    private String id;
    private String name;
    private String imageUrl;

    public HospitalItem() {
        // Required empty constructor for Firebase
    }

    public HospitalItem(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}