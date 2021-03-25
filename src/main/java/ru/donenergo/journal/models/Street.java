package ru.donenergo.journal.models;

public class Street {
    int rn;
    String streetName;
    String streetType;
    String postCode;

    public Street() {
    }

    public int getRn() {
        return rn;
    }

    public void setRn(int rn) {
        this.rn = rn;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetType() {
        return streetType;
    }

    public void setStreetType(String streetType) {
        this.streetType = streetType;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
}
