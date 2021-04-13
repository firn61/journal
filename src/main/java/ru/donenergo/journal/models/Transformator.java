package ru.donenergo.journal.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Transformator {
    DateTimeFormatter dateTimeFormatterBase = DateTimeFormatter.ofPattern("dd.MM.yy' 'HH:mm");
    DateTimeFormatter dateTimeFormatterForm = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
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
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateTime;
    private String monter;
    private int linesCount;
    private List<Line> listLines;

    public Transformator() {
    }

    @Override
    public String toString() {
        return "Transformator{" +
                "rn=" + rn +
                ", tpRn=" + tpRn +
                ", num=" + num +
                ", fider='" + fider + '\'' +
                ", power=" + power +
                ", uA=" + uA +
                ", uB=" + uB +
                ", uC=" + uC +
                ", iA=" + iA +
                ", iB=" + iB +
                ", iC=" + iC +
                ", iN=" + iN +
                ", dateTime=" + dateTime +
                ", monter='" + monter + '\'' +
                ", linesCount=" + linesCount +
                '}';
    }

    public Transformator(int rn, int tpRn, int num, String fider, int power, int uA, int uB, int uC, int iA, int iB, int iC, int iN, LocalDateTime dateTime, String monter) {
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDateTimeToDAO(){
        if(dateTime == null) {
            dateTime = LocalDateTime.now();
        }
        String day = String.valueOf(dateTime.getDayOfMonth());
        if (day.length() == 1) {
            day = "0" + day;
        }
        String month = String.valueOf(dateTime.getMonthValue());
        if (month.length() == 1) {
            month = "0" + month;
        }
        String hour = String.valueOf(dateTime.getHour());
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        String minute = String.valueOf(dateTime.getMinute());
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        return day + "." + month + "." + String.valueOf(dateTime.getYear()).substring(2) + " " + hour + ":" + minute;
    }

    public void setDateTime(String dateTime) {
        if ((dateTime == null) || (dateTime.length() == 0)) {
            this.dateTime = LocalDateTime.now();
        } else {
            try {
                this.dateTime = LocalDateTime.parse(dateTime, dateTimeFormatterForm);
            } catch (DateTimeException e) {
                this.dateTime = LocalDateTime.now();
            }
        }
    }

    public void setDateTimeFromDAO(String dateTime) {
        if ((dateTime == null) || (dateTime.length() == 0)) {
            this.dateTime = null;
        } else {
            try {
                this.dateTime = LocalDateTime.parse(dateTime, dateTimeFormatterBase);
            } catch (DateTimeException e) {
                this.dateTime = null;
            }
        }
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getMonter() {
        return monter;
    }

    public void setMonter(String monter) {
        this.monter = monter;
    }

}
