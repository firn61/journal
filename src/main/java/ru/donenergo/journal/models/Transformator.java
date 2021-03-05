package ru.donenergo.journal.models;

import java.util.List;

public class Transformator {
    private int rn;
    private int tpRn;
    private int num;
    private String fider;
    private int power;
    private int uA;
    private int uB;
    private int uC;
    private int iA;
    private int iB;
    private int iC;
    private int iN;
    private String dateTime;
    private String monter;
    private int linesCount;
    private List<Line> listLines;

    public Transformator() {
    }

    public Transformator(int rn, int tpRn, int num, String fider, int power, int uA, int uB, int uC, int iA, int iB, int iC, int iN, String dateTime, String monter) {
        this.rn = rn;
        this.tpRn = tpRn;
        this.num = num;
        this.fider = fider;
        this.power = power;
        this.uA = uA;
        this.uB = uB;
        this.uC = uC;
        this.iA = iA;
        this.iB = iB;
        this.iC = iC;
        this.iN = iN;
        this.dateTime = dateTime;
        this.monter = monter;
    }

    public int getLinesCount() {
        return linesCount;
    }

    public void setLinesCount(int linesCount) {
        this.linesCount = linesCount;
    }

    public List<Line> getListLines() {
        return listLines;
    }

    public void setListLines(List<Line> listLines) {
        this.listLines = listLines;
    }

    public int getRn() {
        return rn;
    }

    public void setRn(int rn) {
        this.rn = rn;
    }

    public int getTpRn() {
        return tpRn;
    }

    public void setTpRn(int tpRn) {
        this.tpRn = tpRn;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getFider() {
        return fider;
    }

    public void setFider(String fider) {
        this.fider = fider;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getuA() {
        return uA;
    }

    public void setuA(int uA) {
        this.uA = uA;
    }

    public int getuB() {
        return uB;
    }

    public void setuB(int uB) {
        this.uB = uB;
    }

    public int getuC() {
        return uC;
    }

    public void setuC(int uC) {
        this.uC = uC;
    }

    public int getiA() {
        return iA;
    }

    public void setiA(int iA) {
        this.iA = iA;
    }

    public int getiB() {
        return iB;
    }

    public void setiB(int iB) {
        this.iB = iB;
    }

    public int getiC() {
        return iC;
    }

    public void setiC(int iC) {
        this.iC = iC;
    }

    public int getiN() {
        return iN;
    }

    public void setiN(int iN) {
        this.iN = iN;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMonter() {
        return monter;
    }

    public void setMonter(String monter) {
        this.monter = monter;
    }

}
