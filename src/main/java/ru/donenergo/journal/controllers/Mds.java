package ru.donenergo.journal.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.donenergo.journal.dao.PodstationDAO;
import ru.donenergo.journal.models.Period;
import ru.donenergo.journal.models.Podstation;

import java.util.List;

@Component
public class Mds {

    @Autowired
    private PodstationDAO podstationDAO;
    private String resName;
    private String currentDate;
    private String currentPodstation;
    private String podstationNum;
    private String podstType;
    private List<String> podstTypes;
    private List<Period> periodList;
    private List<Podstation> podstations;
    private Podstation sPodstation;
    private String currentActivity;
    static Logger log = Logger.getLogger(Mds.class.getName());

    public void addNewPodstationToList(Podstation podstation) {
        podstations.add(podstation);
    }

    public String getActivityView(String currentActivity) {
        if (currentActivity.equals("edit")) {
            return "editpodstation";
        }
        if (currentActivity.equals("streetsshow")) {
            return "streetsshow";
        }
        if (currentActivity.equals("values")) {
            return "editvalues";
        }
        if (currentActivity.equals("streetsedit")) {
            return "streetsedit";
        } else {
            return "show";
        }
    }


    public Podstation refreshMdsValues(String rn, String type) {
        List<Podstation> podstations = podstationDAO.getListPodstations(currentDate);
        setPodstations(podstations);
        if (rn.equals("norn")) {
            setCurrentPodstation(String.valueOf(podstations.get(0).getRn()));
        } else {
            for (Podstation p : podstations) {
                if (p.getNumStr().equals(rn) && p.getPodstType().equals(type)) {
                    setCurrentPodstation(String.valueOf(p.getRn()));
                    System.out.println("Current podstation set to: " + p.getRn());
                }
            }
        }
        setPodstTypes(podstationDAO.getPodstationTypes(getCurrentDate()));
        Podstation sPodstation = podstationDAO.getPodstation(getCurrentPodstation());
        setsPodstation(sPodstation);
        setPodstationNum(sPodstation.getNumStr());
        setPodstType(sPodstation.getPodstType());
        System.out.println("rn: " + rn + " sPodstationNum: " + sPodstation.getNum() + " pNum " + podstationNum + " pType " + podstType);
        return sPodstation;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(String currentActivity) {
        this.currentActivity = currentActivity;
    }

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
