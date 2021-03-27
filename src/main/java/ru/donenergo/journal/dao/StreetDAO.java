package ru.donenergo.journal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.donenergo.journal.mappers.HouseSegmentMapper;
import ru.donenergo.journal.mappers.StreetMapper;
import ru.donenergo.journal.models.HouseSegment;
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

    public List<HouseSegment> getHouseSegmentsTp(String tpName){
        return jdbcTemplate.query("SELECT RN, STR_PODSTATION, TR_NUM, FIDER, STREET_RN, STREET_NAME, STREET_TYPE, HOUSE1, HOUSE1BUILDING, HOUSE2, HOUSE2BUILDING FROM HOUSE_SEGMENT WHERE STR_PODSTATION = ? ", new Object[]{tpName}, new HouseSegmentMapper());
    }

    public List<HouseSegment> getHouseSegmentsRp(String rpName){
        return jdbcTemplate.query("SELECT RN, STR_PODSTATION, TR_NUM, FIDER, STREET_RN, STREET_NAME, STREET_TYPE, HOUSE1, HOUSE1BUILDING, HOUSE2, HOUSE2BUILDING FROM HOUSE_SEGMENT2 WHERE STR_PODSTATION = ? ", new Object[]{rpName}, new HouseSegmentMapper());
    }

    public List<Street> getStreets() {
        return streets;
    }
}
