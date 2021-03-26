package ru.donenergo.journal.controllers;

import org.springframework.stereotype.Component;
import ru.donenergo.journal.models.Period;
import ru.donenergo.journal.models.Podstation;

import java.util.List;

@Component
public class Mds {
    private String currentPodstation;
    private String podstationNum;
    private String currentDate;
    private String podstType;
    private List<String> podstTypes;
    private List<Period> periodList;
    private List<Podstation> podstations;
    private Podstation sPodstation;

    public Podstation getsPodstation() {
        return sPodstation;
    }

    public void setsPodstation(Podstation sPodstation) {
        this.sPodstation = sPodstation;
    }

    public String getPodstationNum() {
        return podstationNum;
    }

    public void setPodstationNum(String podstationNum) {
        this.podstationNum = podstationNum;
    }

    public String getPodstType() {
        return podstType;
    }

    public void setPodstType(String podstType) {
        this.podstType = podstType;
    }

    public Mds() {
    }

    public String getCurrentPodstation() {
        return currentPodstation;
    }

    public void setCurrentPodstation(String currentPodstation) {
        this.currentPodstation = currentPodstation;
    }


    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public List<String> getPodstTypes() {
        return podstTypes;
    }

    public void setPodstTypes(List<String> podstTypes) {
        this.podstTypes = podstTypes;
    }

    public List<Period> getPeriodList() {
        return periodList;
    }

    public void setPeriodList(List<Period> periodList) {
        this.periodList = periodList;
    }

    public List<Podstation> getPodstations() {
        return podstations;
    }

    public void setPodstations(List<Podstation> podstations) {
        this.podstations = podstations;
    }


}
