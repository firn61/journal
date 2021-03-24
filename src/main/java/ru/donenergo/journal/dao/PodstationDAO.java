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
        periodList = setPeriodListFromDb(currentDate);
        getPodstationsListFromDb(currentDate);
    }

    public void addTransformator(String podstationRn, int num){
        jdbcTemplate.queryForObject("execute procedure TRANS_INSERT(?, ?, null, 0, 0, 0, 0, 0, 0, 0, 0, null, null)", new Object[]{podstationRn, num}, String.class);
    }

    public void addLine(String transformatorRn, String num){
        jdbcTemplate.queryForObject("execute procedure LINE_INSERT(?, ?, ?, 0, 0, 0, 0, null)", new Object[]{transformatorRn, num, "Л-"}, String.class);
    }

    public void updatePodstationValues(Podstation podstation){
        for (int i = 0; i <podstation.getTrList().size() ; i++) {
            updateTransformatorValues(podstation.getTrList().get(i));
        }
    }

    public void updateTransformatorValues(Transformator transformator){
        int sumiA=0;
        int sumiB=0;
        int sumiC=0;
        int sumiO=0;
        for (int i = 0; i <transformator.getListLines().size(); i++) {
            sumiA +=transformator.getListLines().get(i).getiA();
            sumiB +=transformator.getListLines().get(i).getiB();
            sumiC +=transformator.getListLines().get(i).getiC();
            sumiO +=transformator.getListLines().get(i).getiO();
            updateLineValues(transformator.getListLines().get(i));
        }
        transformator.setiA(sumiA);
        transformator.setiB(sumiB);
        transformator.setiC(sumiC);
        transformator.setiN(sumiO);
        jdbcTemplate.update("execute procedure TRANS_VALUESUPDATE(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{transformator.getRn(), transformator.getuA(), transformator.getuB(), transformator.getuC(),
                transformator.getiA(), transformator.getiB(), transformator.getiC(), transformator.getiN(),
                transformator.getDateTime(), transformator.getMonter()});
    }

    public void updateLineValues(Line line){
        System.out.println(line);
        jdbcTemplate.update("execute procedure LINE_VALUESUPDATE(?, ?, ?, ?, ?, ?)",
                new Object[]{line.getRn(), line.getiA(), line.getiB(), line.getiC(), line.getiO(), line.getkA()});
    }

    //returns current period DATE_RN from database, used in @PostConstruct
    private String getCurrentDateFromDb() {
        return (String) jdbcTemplate.queryForObject("SELECT SVALUE FROM SYSTEM WHERE SPARAM = 'currentDate'", String.class);
    }

    //returns all periods from database, used in @PostConstruct
    private List<Period> setPeriodListFromDb(String currentDate) {
        return jdbcTemplate.query("SELECT RN, SDATE FROM DATES WHERE RN <= ?", new Object[]{currentDate}, new PeriodMapper());
    }

    //returns all podstations on current period, used in @PostConstruct
    private List<Podstation> getPodstationsListFromDb(String currentDate) {
        podstations = jdbcTemplate.query("SELECT RN, PODST_TYPE, NUM, NUM_STR, RES_NUM, DATE_RN, IS_ACTIVE, ADDRESS FROM PODSTATION WHERE DATE_RN=?",
                new Object[]{currentDate}, new PodstationMapper());
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
            podstation.getTrList().get(i).setLinesCount(podstation.getTrList().get(i).getListLines().size());
        }
        return podstation;
    }

    //Get podstation transformators by TP_RN
    public List<Transformator> getTransformators(int tpRn) {
        log.info(" tpRn: " + tpRn);
        return jdbcTemplate.query("SELECT RN, TP_RN, NUM, FIDER, POWER, U_A, U_B, U_C, I_A, I_B, I_C, I_N, DATETIME, MONTER FROM TRANSFORMATOR WHERE TP_RN=?",
                new Object[]{tpRn}, new TransformatorMapper());
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
