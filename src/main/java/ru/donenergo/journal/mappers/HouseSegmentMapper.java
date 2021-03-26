package ru.donenergo.journal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.donenergo.journal.models.HouseSegment;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HouseSegmentMapper implements RowMapper<HouseSegment> {
    @Override
    public HouseSegment mapRow(ResultSet resultSet, int i) throws SQLException {
        HouseSegment houseSegment = new HouseSegment();
        houseSegment.setRn(resultSet.getInt("RN"));
        houseSegment.setStrPodstation(resultSet.getString("STR_PODSTATION"));
        houseSegment.setTrNum(resultSet.getInt("TR_NUM"));
        houseSegment.setFider(resultSet.getString("FIDER"));
        houseSegment.setStreetRn(resultSet.getInt("STREET_RN"));
        houseSegment.setStreetName(resultSet.getString("STREET_NAME"));
        houseSegment.setStreetType(resultSet.getString("STREET_TYPE"));
        houseSegment.setHouse1(resultSet.getInt("HOUSE1"));
        houseSegment.setHouse1Building(resultSet.getString("HOUSE1BUILDING"));
        houseSegment.setHouse2(resultSet.getInt("HOUSE2"));
        houseSegment.setHouse2Building(resultSet.getString("HOUSE2BUILDING"));
        return houseSegment;
    }
}
