package com.example.ecommerce.domain;

public class RouletteSection {
    private String id;
    private String gift;
    private String percentage;

    public RouletteSection() {
        // Default constructor required for calls to DataSnapshot.getValue(RouletteSection.class)
    }

    public RouletteSection(String id, String gift, String percentage) {
        this.id = id;
        this.gift = gift;
        this.percentage = percentage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}
