package ru.donenergo.journal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class HostDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public HostDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, String> getHosts() {
        Map<String, String> hostsMap = jdbcTemplate.query("SELECT IP, RIGHTS FROM HOSTS", new ResultSetExtractor<Map>() {
            @Override
            public Map extractData(ResultSet rs) throws SQLException, DataAccessException {
                HashMap<String, String> extractedHosts = new HashMap<>();
                while (rs.next()) {
                    extractedHosts.put(rs.getString("IP"), rs.getString("RIGHTS"));
                }
                return extractedHosts;
            }
        });
        return hostsMap;
    }

    public void addRO(String ip){
        jdbcTemplate.update("INSERT INTO HOSTS VALUES(?, ?)", new Object[]{ip, "rges0"});
    }

}