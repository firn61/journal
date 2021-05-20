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

    public String addPodstation(String podstType, String num, String resNum, String dateRn, String address) {
        return jdbcTemplate.queryForObject("execute procedure PODST_INSERT(?, ?, ?, ?, ?, ?, ?)", new Object[]{podstType, Integer.valueOf(num), num, resNum, dateRn, 0, address}, String.class);
    }

    public Integer isPodstationExist(String type, String num, String dateRn) {
        return (Integer) jdbcTemplate.queryForObject("SELECT COUNT(RN) FROM PODSTATION WHERE PODST_TYPE = ? AND NUM = ? AND DATE_RN = ?", new Object[]{type, num, dateRn}, Integer.class);
    }

    private String createPostfix(boolean additional) {
        if (additional) {
            return "_P";
        } else {
            return "";
        }
    }

    public void deleteTransformator(String rn, boolean additional) {
        String postfix = createPostfix(additional);
        String sqlDeleteTrans = "DELETE FROM TRANSFORMATOR" + postfix + " WHERE RN = ?";
        String sqlDeleteLine = "DELETE FROM LINE" + postfix + " WHERE TR_RN = ?";
        jdbcTemplate.update(sqlDeleteTrans, new Object[]{rn});
        jdbcTemplate.update(sqlDeleteLine, new Object[]{rn});
    }

    public void updatePodstation(Podstation uPodstation) {
        jdbcTemplate.update("execute procedure PODST_UPDATE(?, ?)", new Object[]{uPodstation.getAddress(), uPodstation.getRn()});
        if (uPodstation.getTransformators() != null) {
            for (Transformator transformator : uPodstation.getTransformators()) {
                jdbcTemplate.update("execute procedure TRANS_UPDATE(?, ?, ?)",
                        new Object[]{transformator.getFider(), transformator.getPower(), transformator.getRn()});
                if (transformator.getListLines() != null) {
                    for (Line line : transformator.getListLines()) {
                        updateLine(line.getNum(), line.getName(), line.getRn());
                    }
                }
            }
        }
    }

    public String addTransformator(String podstationRn, int num, boolean additional) {
        String postfix = createPostfix(additional);
        String sqlAddTransformator = "execute procedure TRANS_INSERT" + postfix +"(?, ?, null, 0, 0, 0, 0, 0, 0, 0, 0, null, null)";
        return jdbcTemplate.queryForObject(sqlAddTransformator, new Object[]{podstationRn, num}, String.class);
    }

    public String addTransformatorToNewPeriod(String tpRn, Integer num, String fider, Integer power ) {
        String sqlAddTransformator = "execute procedure TRANS_INSERT(?, ?, ?, ?, 0, 0, 0, 0, 0, 0, 0, null, null)";
        return jdbcTemplate.queryForObject(sqlAddTransformator, new Object[]{tpRn, num, fider, power}, String.class);
    }

    public String addLine(String transformatorRn, String num, boolean additional) {
        String postfix = createPostfix(additional);
        return jdbcTemplate.queryForObject("execute procedure LINE_INSERT" + postfix +"(?, ?, ?, 0, 0, 0, 0, null)", new Object[]{transformatorRn, num, "Л-"}, String.class);
    }

    public void addLineToNewPeriod(String trRn, Integer num, String name){
        log.info(" trRn: " + trRn + "num: " + num + "name: " + name);
        jdbcTemplate.queryForObject("execute procedure LINE_INSERT(?, ?, ?, 0, 0, 0, 0, null)", new Object[]{trRn, num, name}, String.class);
    }

    public Line getLine(String rn, boolean additional) {
        String postfix = createPostfix(additional);
        return jdbcTemplate.queryForObject("SELECT RN, TR_RN, NUM, NAME, I_A, I_B, I_C, I_O, KA FROM LINE" + postfix +" WHERE RN = ?", new Object[]{rn}, new LineMapper());
    }

    public Line getLine(int trRn, int num) {
        try {
            return jdbcTemplate.queryForObject("SELECT RN, TR_RN, NUM, NAME, I_A, I_B, I_C, I_O, KA FROM LINE WHERE TR_RN =? AND NUM = ?", new Object[]{trRn, num}, new LineMapper());
        } catch (Exception e) {
            return null;
        }
    }

    public void moveLine(String lineRn, String direction) {
        int arg = 0;
        if (direction.equals("up")) {
            arg = -1;
        } else if (direction.equals("down")) {
            arg = 1;
        }
        Line currentLine = getLine(lineRn, false);
        Line swapped = getLine(currentLine.getTrRn(), currentLine.getNum() + arg);
        if (swapped != null) {
            updateLine(currentLine.getNum(), swapped.getName(), swapped.getRn());
            updateLine(swapped.getNum(), currentLine.getName(), currentLine.getRn());
        }
    }

    public void updateLine(int num, String name, int rn) {
        jdbcTemplate.update("execute procedure LINE_UPDATE(?, ?, ?)", new Object[]{num, name, rn});
    }

    public boolean isPTransformatorsExist(String rn) {
        Integer pTransformatorsCount = jdbcTemplate.queryForObject("SELECT count(RN) FROM TRANSFORMATOR_P WHERE TP_RN = ?", new Object[]{rn}, Integer.class);
        return (pTransformatorsCount > 0);
    }


    public void deleteLine(String lineRn, boolean additional) {
        String postfix = createPostfix(additional);
        Line currentLine = getLine(lineRn, additional);
        jdbcTemplate.update("execute procedure LINE_DELETE"+ postfix + "(?)", new Object[]{lineRn});
        String sqlTemplate = "SELECT RN, TR_RN, NUM, NAME, I_A, I_B, I_C, I_O, KA FROM LINE" + postfix + " WHERE TR_RN = ? AND NUM > ?";
        List<Line> tLines = jdbcTemplate.query(sqlTemplate, new Object[]{currentLine.getTrRn(), currentLine.getNum()}, new LineMapper());
        for (Line l : tLines) {
            int reducedNum = l.getNum() - 1;
            updateLine(reducedNum, l.getName(), l.getRn());
        }
    }

    public void updatePodstationValues(Podstation podstation) {
        for (int i = 0; i < podstation.getTransformators().size(); i++) {
            updateTransformatorValues(podstation.getTransformators().get(i), false);
        }
        if ((podstation.getpTransformators() != null) && (podstation.getpTransformators().size() > 0)){
            for (int i = 0; i < podstation.getpTransformators().size() ; i++) {
                updateTransformatorValues(podstation.getpTransformators().get(i), true);
            }
        }
    }

    public void updateTransformatorValues(Transformator transformator, boolean additional) {
        String postfix = createPostfix(additional);
        int sumiA = 0;
        int sumiB = 0;
        int sumiC = 0;
        int sumiO = 0;
        for (int i = 0; i < transformator.getListLines().size(); i++) {
            sumiA += transformator.getListLines().get(i).getiA();
            sumiB += transformator.getListLines().get(i).getiB();
            sumiC += transformator.getListLines().get(i).getiC();
            sumiO += transformator.getListLines().get(i).getiO();
            updateLineValues(transformator.getListLines().get(i), additional);
        }
        transformator.setiA(sumiA);
        transformator.setiB(sumiB);
        transformator.setiC(sumiC);
        transformator.setiN(sumiO);
        jdbcTemplate.update("execute procedure TRANS_VALUESUPDATE"+ postfix + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{transformator.getRn(), transformator.getuA(), transformator.getuB(), transformator.getuC(),
                        transformator.getiA(), transformator.getiB(), transformator.getiC(), transformator.getiN(),
                        transformator.getDateTimeToDAO(), transformator.getMonter()});
    }

    public void updateLineValues(Line line, boolean additional) {
        String postfix = createPostfix(additional);
        jdbcTemplate.update("execute procedure LINE_VALUESUPDATE" + postfix + "(?, ?, ?, ?, ?, ?)",
                new Object[]{line.getRn(), line.getiA(), line.getiB(), line.getiC(), line.getiO(), line.getkA()});
    }

    private String getCurrentDateFromDb() {
        return (String) jdbcTemplate.queryForObject("SELECT SVALUE FROM SYSTEM WHERE SPARAM = 'currentDate'", String.class);
    }

    private List<Period> setPeriodListFromDb(String currentDate) {
        return jdbcTemplate.query("SELECT RN, SDATE FROM DATES WHERE RN <= ?", new Object[]{currentDate}, new PeriodMapper());
    }

    private List<Podstation> getPodstationsListFromDb(String currentDate) {
        podstations = jdbcTemplate.query("SELECT RN, PODST_TYPE, NUM, NUM_STR, RES_NUM, DATE_RN, IS_ACTIVE, ADDRESS FROM PODSTATION WHERE DATE_RN=? ORDER BY PODST_TYPE, NUM",
                new Object[]{currentDate}, new PodstationMapper());
        log.info(" Podstation list refreshed");
        return podstations;
    }

    public void createIndeterminateMeasure(Podstation podstation){
        for (Transformator transformator : podstation.getTransformators()){
            String pTransformatorRn = addTransformator(String.valueOf(podstation.getRn()), transformator.getNum(), true);
            jdbcTemplate.update("execute procedure TRANS_UPDATE_P(?, ?, ?)",
                    new Object[]{transformator.getFider(), transformator.getPower(), pTransformatorRn});
            for (Line line : transformator.getListLines()){
                String pLineRn = addLine(pTransformatorRn, String.valueOf(line.getNum()), true);
                jdbcTemplate.update("execute procedure LINE_UPDATE_P(?, ?, ?)", new Object[]{line.getNum(), line.getName(), pLineRn});
            }
        }
    }

    public Podstation getPodstation(String rn) {
        log.info(" rn: " + rn + " Current date is: " + currentDate);
        String sqlTemplate = "SELECT RN, PODST_TYPE, NUM, NUM_STR, RES_NUM, DATE_RN, IS_ACTIVE, ADDRESS FROM PODSTATION WHERE RN =?";
        Podstation podstation = (Podstation) jdbcTemplate.queryForObject(sqlTemplate, new Object[]{rn}, new PodstationMapper());
        podstation.setTransformators(getTransformators(podstation.getRn(), false));
        podstation.setTrCount(podstation.getTransformators().size());
        for (int i = 0; i < podstation.getTransformators().size(); i++) {
            podstation.getTransformators().get(i).setListLines(getTransformatorLines(podstation.getTransformators().get(i).getNum(), podstation.getTransformators().get(i).getRn(), false));
            podstation.getTransformators().get(i).setLinesCount(podstation.getTransformators().get(i).getListLines().size());
        }
        if (isPTransformatorsExist(rn)){
            podstation.setpTransformators(getTransformators(podstation.getRn(), true));
            for (int i = 0; i < podstation.getpTransformators().size(); i++) {
                podstation.getpTransformators().get(i).setListLines(getTransformatorLines(podstation.getpTransformators().get(i).getNum(), podstation.getpTransformators().get(i).getRn(), true));
                podstation.getpTransformators().get(i).setLinesCount(podstation.getpTransformators().get(i).getListLines().size());
            }
        }
        return podstation;
    }

    public List<Transformator> getTransformators(int tpRn, boolean additional) {
        String postfix = createPostfix(additional);
        log.info(" tpRn: " + tpRn);
        return jdbcTemplate.query("SELECT RN, TP_RN, NUM, FIDER, POWER, U_A, U_B, U_C, I_A, I_B, I_C, I_N, DATETIME, MONTER FROM TRANSFORMATOR" + postfix + " WHERE TP_RN=? ORDER BY NUM",
                new Object[]{tpRn}, new TransformatorMapper());
    }

    public List<Line> getTransformatorLines(int trNum, int trRn, boolean additional) {
        String postfix = createPostfix(additional);
        log.info(" trRn: " + trNum + " trRn: " + trRn);
        String sqlTemplate = "SELECT RN, TR_RN, NUM, NAME, I_A, I_B, I_C, I_O, KA FROM LINE" + postfix + " WHERE TR_RN = ? ORDER BY NUM";
        List<Line> tLines = jdbcTemplate.query(sqlTemplate, new Object[]{trRn}, new LineMapper());
        for (Line line : tLines) {
            line.setSectionNum(trNum);
        }
        return tLines;
    }

    public String getPodstationNumByRn(String rn) {
        return (String) jdbcTemplate.queryForObject("SELECT NUM_STR FROM PODSTATION WHERE RN = ?", new Object[]{rn}, String.class);
    }

    public String getPodstationRn(String type, String num, String currentDate) {
        String sqlTemplate = "SELECT RN FROM PODSTATION WHERE PODST_TYPE = ? AND NUM_STR = ? AND DATE_RN = ?";
        return (String) jdbcTemplate.queryForObject(sqlTemplate, new Object[]{type, num, currentDate}, String.class);
    }

    public List<String> getPodstationTypes(String currentDate) {
        List<String> podstationTypes;
        podstationTypes = jdbcTemplate.query("SELECT DISTINCT PODST_TYPE FROM PODSTATION WHERE DATE_RN = ?", new Object[]{currentDate}, new StringMapper());
        return podstationTypes;
    }

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

    public String startNewPeriod(){
        String currentDate = getCurrentDateFromDb();
        String targetDate = String.valueOf(Integer.valueOf(currentDate) +1);
        List<Podstation> currentPodstations= getPodstationsListFromDb(currentDate);
        for (Podstation p : currentPodstations){
            String pRn = addPodstation(p.getPodstType(), p.getNumStr(), String.valueOf(p.getResNum()), targetDate, p.getAddress());
            List<Transformator> oldTransformators = getTransformators(p.getRn(), false);
            for (Transformator t: oldTransformators) {
                String tRn = addTransformatorToNewPeriod(pRn, t.getNum(), t.getFider(), t.getPower());
                List<Line> lines = getTransformatorLines(t.getNum(), t.getRn(), false);
                for (Line l : lines){
                    addLineToNewPeriod(tRn, l.getNum(), l.getName());
                }
            }
        }
        return targetDate;
    }

}
