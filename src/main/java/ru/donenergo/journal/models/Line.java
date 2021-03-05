package ru.donenergo.journal.models;

public class Line {
    private int sectionNum;
    private int rn;
    private int trRn;
    private int num;
    private String name;
    private int iA;
    private int iB;
    private int iC;
    private int iO;
    private String kA;

    public Line() {
    }

    public Line(int rn, int trRn, int num, String name, int iA, int iB, int iC, int iO, String kA) {
        this.rn = rn;
        this.trRn = trRn;
        this.num = num;
        this.name = name;
        this.iA = iA;
        this.iB = iB;
        this.iC = iC;
        this.iO = iO;
        this.kA = kA;
    }

    public int getSectionNum() {
        return sectionNum;
    }

    public void setSectionNum(int sectionNum) {
        this.sectionNum = sectionNum;
    }

    public int getRn() {
        return rn;
    }

    public void setRn(int rn) {
        this.rn = rn;
    }

    public int getTrRn() {
        return trRn;
    }

    public void setTrRn(int trRn) {
        this.trRn = trRn;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getiO() {
        return iO;
    }

    public void setiO(int iO) {
        this.iO = iO;
    }

    public String getkA() {
        return kA;
    }

    public void setkA(String kA) {
        this.kA = kA;
    }
}
