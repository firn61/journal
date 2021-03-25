package ru.donenergo.journal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.donenergo.journal.mappers.StreetMapper;
import ru.donenergo.journal.models.Street;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class StreetDAO {
    private final JdbcTemplate jdbcTemplate;
    List<Street> streets;

    @Autowired
    public StreetDAO(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    @PostConstruct
    private void setInitialState(){
        streets = jdbcTemplate.query("SELECT RN, STREET_NAME, STREET_TYPE, POSTCODE FROM STREETS", new StreetMapper());
    }

    public List<Street> getStreets() {
        return streets;
    }
}
