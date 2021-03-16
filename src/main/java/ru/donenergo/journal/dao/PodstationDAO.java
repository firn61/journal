package ru.donenergo.journal.dao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.donenergo.journal.mappers.*;
import ru.donenergo.journal.models.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class PodstationDAO {
    private final JdbcTemplate jdbcTemplate;
    static Logger log = Logger.getLogger(PodstationDAO.class.getName());
    private String currentDate;
    private List<Period> periodList = new ArrayList<>();
    private List<Podstation> podstations;

    @Autowired
    public PodstationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    private void setInitialState() {
        currentDate = getCurrentDateFromDb();
        periodList = setPeriodListFromDb();
        getPodstationsListFromDb(currentDate);
    }

    //returns current period DATE_RN from database, used in @PostConstruct
    private String getCurrentDateFromDb() {
        return (String) jdbcTemplate.queryForObject("SELECT SVALUE FROM SYSTEM WHERE SPARAM = 'currentDate'", String.class);
    }

    //returns all periods from database, used in @PostConstruct
    private List<Period> setPeriodListFromDb() {
        return jdbcTemplate.query("SELECT * FROM DATES", new PeriodMapper());
    }

    //returns all podstations on current period, used in @PostConstruct
    private List<Podstation> getPodstationsListFromDb(String currentDate) {
        podstations = jdbcTemplate.query("SELECT * FROM PODSTATION WHERE DATE_RN=?", new Object[]{currentDate}, new PodstationMapper());
        log.info(" Podstation list refreshed");
        return podstations;
    }

    //Get one podstation with transformators and lines by RN
    public Podstation getPodstation(String rn) {
        log.info(" rn: " + rn +" Current date is: " + currentDate);
        String sqlTemplate = "SELECT RN, PODST_TYPE, NUM, NUM_STR, RES_NUM, DATE_RN, IS_ACTIVE, ADDRESS FROM PODSTATION WHERE RN =?";
        Podstation podstation = (Podstation) jdbcTemplate.queryForObject(sqlTemplate, new Object[]{rn}, new PodstationMapper());
        podstation.setTrList(getTransformators(podstation.getRn()));
        podstation.setTrCount(podstation.getTrList().size());
        for (int i = 0; i < podstation.getTrList().size(); i++) {
            podstation.getTrList().get(i).setListLines(getTransformatorLines(podstation.getTrList().get(i).getNum(), podstation.getTrList().get(i).getRn()));
        }
        return podstation;
    }

    //Get podstation transformators by TP_RN
    public List<Transformator> getTransformators(int tpRn) {
        log.info(" tpRn: " + tpRn);
        return jdbcTemplate.query("SELECT * FROM TRANSFORMATOR WHERE TP_RN=?", new Object[]{tpRn}, new TransformatorMapper());
    }

    //Not used
    public List<Line> getAllPodstationLines(List<Transformator> tList) {
        List<Line> allLines = new ArrayList<>();
        for (int tNum = 0; tNum < tList.size(); tNum++) {
            allLines.addAll(getTransformatorLines(tList.get(tNum).getNum(),
                    tList.get(tNum).getRn()));
        }
        return allLines;
    }

    public List<Line> getTransformatorLines(int trNum, int trRn) {
        log.info(" trRn: " + trNum + " trRn: " + trRn);
        String sqlTemplate = "SELECT RN, TR_RN, NUM, NAME, I_A, I_B, I_C, I_O, KA FROM LINE WHERE TR_RN = ?";
        List<Line> tLines = jdbcTemplate.query(sqlTemplate, new Object[]{trRn}, new LineMapper());
        for (Line line : tLines) {
            line.setSectionNum(trNum);
        }
        return tLines;
    }

    public String getPodstationNumByRn(String rn){
        return (String) jdbcTemplate.queryForObject("SELECT NUM_STR FROM PODSTATION WHERE RN = ?", new Object[]{rn}, String.class);
    }

    public String getPodstationRn(String type, String num, String currentDate){
        System.out.println("type form dao is: " + type);
        String sqlTemplate = "SELECT RN FROM PODSTATION WHERE PODST_TYPE = ? AND NUM_STR = ? AND DATE_RN = ?";
        return (String) jdbcTemplate.queryForObject(sqlTemplate, new Object[]{type, num, currentDate}, String.class);
    }

    public List<String> getPodstationTypes(String currentDate){
        List<String> podstationTypes;
        podstationTypes = jdbcTemplate.query("SELECT DISTINCT PODST_TYPE FROM PODSTATION WHERE DATE_RN = ?", new Object[]{currentDate}, new StringMapper());
        return podstationTypes;
    }

    //get podstations to model
    public List<Podstation> getListPodstations(String requestDate) {
        log.info(" requestDate: " + requestDate);
        if (requestDate.equals(currentDate)) {
            return podstations;
        } else {
            currentDate = requestDate;
            return getPodstationsListFromDb(requestDate);
        }
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public List<Period> getPeriodList() {
        return periodList;
    }
    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
