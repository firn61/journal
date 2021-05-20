package ru.donenergo.journal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReportsDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReportsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String[]> getReportAllPodstations(String currentDate){
        List<String[]> result = jdbcTemplate.query("SELECT a.tp, r.NAME, a.adr, d.SDATE, t.i_a, t.i_b, t.i_c, t.i_n \n" +
                "FROM (SELECT RN, a.PODST_TYPE|| '-' || a.NUM AS tp, a.ADDRESS AS adr, RES_NUM AS res, DATE_RN AS daten\n" +
                "FROM PODSTATION a where a.DATE_RN = ?) AS a\n" +
                "left join (SELECT TP_RN, sum(I_A) as i_a, sum(I_B) as i_b, sum(I_C)as i_c, sum(I_N) as i_n FROM TRANSFORMATOR group by tp_rn) as t on t.TP_RN = a.RN\n" +
                "left join (select NAME, NUM from RES) as r on r.NUM=a.res \n" +
                "left join (select RN, SDATE from DATES) as d on d.RN=a.daten", new Object[]{currentDate}, new ResultSetExtractor<List>() {
            @Override
            public List extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<String[]> extractedList = new ArrayList<String[]>();
                while (rs.next()) {
                    String[] singlerow = new String[8];
                    singlerow[0] = rs.getString("TP");
                    singlerow[1] = rs.getString("NAME");
                    singlerow[2] = rs.getString("ADR");
                    singlerow[3] = rs.getString("SDATE");
                    singlerow[4] = rs.getString("I_A");
                    singlerow[5] = rs.getString("I_B");
                    singlerow[6] = rs.getString("I_C");
                    singlerow[7] = rs.getString("I_N");
                    extractedList.add(singlerow);
                }
                return extractedList;
            }
        });
        return result;
    }

    public List<String[]> getOverloadedPodstations(String currentDate) {
        List<String[]> overloadedPodstations = jdbcTemplate.query("SELECT p.RES_NUM, p.PODST_TYPE || '-' || p.NUM_STR,  r.NUM, r.FIDER, r.POWER, ROUND(r.POWER/0.692),  \n" +
                "ROUND(r.POWER/0.692)-r.I_A as dA, ROUND(r.POWER/0.692)-r.I_B as dB, ROUND(r.POWER/0.692)-r.I_C as dC, \n" +
                "r.I_A, r.I_B, r.I_C, r.I_N, r.U_A, r.U_B, r.U_C, r.DATETIME, r.MONTER FROM PODSTATION p \n" +
                "LEFT JOIN (SELECT TP_RN, RN, NUM, FIDER, POWER, POWER-I_A as dA, POWER-I_B as dB, \n" +
                "POWER-I_C as dC, I_A, I_B, I_C, I_N, U_A, U_B, U_C, DATETIME, MONTER FROM TRANSFORMATOR \n" +
                "WHERE ROUND(POWER/0.692)-I_A < 0 OR ROUND(POWER/0.692)-I_B <0 OR ROUND(POWER/0.692)-I_C <0) as r \n" +
                "ON r.TP_RN=p.RN WHERE r.TP_RN IS NOT NULL AND p.DATE_RN = ? ORDER BY p.NUM", new Object[]{currentDate}, new ResultSetExtractor<List>() {
            @Override
            public List extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<String[]> extractedList = new ArrayList<String[]>();
                while (rs.next()) {
                    String[] singlerow = new String[18];
                    singlerow[0] = rs.getString("RES_NUM");
                    singlerow[1] = rs.getString("CONCATENATION");
                    singlerow[2] = rs.getString("NUM");
                    singlerow[3] = rs.getString("FIDER");
                    singlerow[4] = rs.getString("POWER");
                    singlerow[5] = rs.getString("ROUND");
                    singlerow[6] = rs.getString("DA");
                    singlerow[7] = rs.getString("DB");
                    singlerow[8] = rs.getString("DC");
                    singlerow[9] = rs.getString("I_A");
                    singlerow[10] = rs.getString("I_B");
                    singlerow[11] = rs.getString("I_C");
                    singlerow[12] = rs.getString("I_N");
                    singlerow[13] = rs.getString("U_A");
                    singlerow[14] = rs.getString("U_B");
                    singlerow[15] = rs.getString("U_C");
                    singlerow[16] = rs.getString("DATETIME");
                    singlerow[17] = rs.getString("MONTER");

                    extractedList.add(singlerow);
                }
                return extractedList;
            }
        });
        return overloadedPodstations;
    }
}
