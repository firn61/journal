package ru.donenergo.journal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.donenergo.journal.models.Period;
import ru.donenergo.journal.models.Podstation;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class PodstationDAO {

    private final JdbcTemplate jdbcTemplate;

    private String currentDate;
    private List<Period> periodList = new ArrayList<>();

    public String getCurrentDate() {
        return currentDate;
    }

    public List<Period> getPeriodList() {
        return periodList;
    }

    @Autowired
    public PodstationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    private void getCurrentDateFromDb(){
        currentDate=(String)jdbcTemplate.queryForObject("SELECT SVALUE FROM SYSTEM WHERE SPARAM = 'currentDate'", String.class);
    }

    @PostConstruct
    private void setPeriodListFill(){
        periodList = jdbcTemplate.query("SELECT * FROM DATES", new PeriodMapper());
    }

    public List<Podstation> index(){
        return jdbcTemplate.query("SELECT * FROM PODSTATION WHERE DATE_RN=?", new Object[]{currentDate}, new PodstationMapper());
    }
}
