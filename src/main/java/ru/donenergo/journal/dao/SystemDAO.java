package ru.donenergo.journal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SystemDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SystemDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getSystemValue(String sparam) {
        return (String) jdbcTemplate.queryForObject("SELECT SVALUE FROM SYSTEM WHERE SPARAM = ?", new Object[]{sparam}, String.class);
    }

    public void updateSystemValue(String sValue, String sParam){
        jdbcTemplate.update("UPDATE SYSTEM SET SVALUE = ? WHERE SPARAM = ?", new Object[]{sValue, sParam});
    }

}
