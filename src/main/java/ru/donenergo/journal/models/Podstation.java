package ru.donenergo.journal.models;

import java.util.List;

public class Podstation {
    private int rn;
    private String podstType;
    private int num;
    private String numStr;
    private int resNum;
    private int dateRn;
    private int isActive;
    private String address;
    private int trCount;
    private List<Transformator> transformators;
    private List<Transformator> pTransformators;


    public Podstation() {
    }

    @Override
    public String toString() {
        return podstType + "-" + num;
    }

    public Podstation(int rn, String podstType, int num, String numStr, int resNum, int dateRn, int isActive, String address) {
        this.rn = rn;
        this.podstType = podstType;
        this.num = num;
        this.numStr = numStr;
        this.resNum = resNum;
        this.dateRn = dateRn;
        this.isActive = isActive;
        this.address = address;
    }

    public List<Transformator> getpTransformators() {
        return pTransformators;
    }

    public void setpTransformators(List<Transformator> pTransformators) {
        this.pTransformators = pTransformators;
    }

    public int getTrCount() {
        return trCount;
    }

    public void setTrCount(int trCount) {
        this.trCount = trCount;
    }

    public List<Transformator> getTransformators() {
        return transformators;
    }

    public void setTransformators(List<Transformator> transformators) {
        this.transformators = transformators;
    }

    public int getRn() {
        return rn;
    }

    public void setRn(int rn) {
        this.rn = rn;
    }

    public String getPodstType() {
        return podstType;
    }

    public void setPodstType(String podstType) {
        this.podstType = podstType;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getNumStr() {
        return numStr;
    }

    public void setNumStr(String numStr) {
        this.numStr = numStr;
    }

    public int getResNum() {
        return resNum;
    }

    public void setResNum(int resNum) {
        this.resNum = resNum;
    }

    public int getDateRn() {
        return dateRn;
    }

    public void setDateRn(int dateRn) {
        this.dateRn = dateRn;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
