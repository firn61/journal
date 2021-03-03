package ru.donenergo.journal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.donenergo.journal.models.Period;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PeriodMapper implements RowMapper<Period> {
    @Override
    public Period mapRow(ResultSet resultSet, int i) throws SQLException {
        Period period = new Period();
        period.setRn(resultSet.getInt("RN"));
        period.setsDate(resultSet.getString("SDATE"));
        return period;
    }
}
