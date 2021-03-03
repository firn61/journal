package ru.donenergo.journal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.donenergo.journal.models.Podstation;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PodstationMapper implements RowMapper<Podstation> {
    @Override
    public Podstation mapRow(ResultSet resultSet, int i) throws SQLException {
        Podstation podstation = new Podstation();
        podstation.setRn(resultSet.getInt("RN"));
        podstation.setPodstType(resultSet.getString("PODST_TYPE"));
        podstation.setNum(resultSet.getInt("NUM"));
        podstation.setNumStr(resultSet.getString("NUM_STR"));
        podstation.setResNum(resultSet.getInt("RES_NUM"));
        podstation.setDateRn(resultSet.getInt("DATE_RN"));
        podstation.setIsActive(resultSet.getInt("IS_ACTIVE"));
        podstation.setAddress(resultSet.getString("ADDRESS"));
        return podstation;
    }
}
