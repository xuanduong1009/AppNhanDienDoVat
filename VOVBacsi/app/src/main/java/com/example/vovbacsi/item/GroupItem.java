// Ví dụ nội dung lớp GroupItem
package com.example.vovbacsi.item;

public class GroupItem {
    private String itemId;
    private String diseaseName;
    private String imageUrl;

    // Getter và Setter
    public GroupItem() {
        // Required empty constructor for Firebase
    }

    public GroupItem(String itemId, String diseaseName, String imageUrl) {
        this.itemId = itemId;
        this.diseaseName = diseaseName;
        this.imageUrl = imageUrl;
    }

    public String getItemId() {
        return itemId;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}