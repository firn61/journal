package ru.donenergo.journal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.donenergo.journal.mappers.LineMapper;
import ru.donenergo.journal.mappers.PeriodMapper;
import ru.donenergo.journal.mappers.PodstationMapper;
import ru.donenergo.journal.mappers.TransformatorMapper;
import ru.donenergo.journal.models.Line;
import ru.donenergo.journal.models.Period;
import ru.donenergo.journal.models.Podstation;
import ru.donenergo.journal.models.Transformator;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class PodstationDAO {
    private final JdbcTemplate jdbcTemplate;

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

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
        System.out.println("Podstation refreshed");
        return podstations;
    }

    //Get one podstation with transformators and lines by RN
    public Podstation getPodstation(int rn) {
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
        String sqlTemplate = "SELECT RN, TR_RN, NUM, NAME, I_A, I_B, I_C, I_O, KA FROM LINE WHERE TR_RN = ?";
        List<Line> tLines = jdbcTemplate.query(sqlTemplate, new Object[]{trRn}, new LineMapper());
        for (Line line : tLines) {
            line.setSectionNum(trNum);
        }
        return tLines;
    }

    //get podstations to model
    public List<Podstation> getPodstations(String requestDate) {
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
}
