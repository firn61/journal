package ru.donenergo.journal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.donenergo.journal.models.Line;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LineMapper implements RowMapper<Line> {

    @Override
    public Line mapRow(ResultSet resultSet, int i) throws SQLException {
        Line line = new Line();
        line.setRn(resultSet.getInt("RN"));
        line.setTrRn(resultSet.getInt("TR_RN"));
        line.setNum(resultSet.getInt("NUM"));
        line.setName(resultSet.getString("NAME"));
        line.setiA(resultSet.getInt("I_A"));
        line.setiB(resultSet.getInt("I_B"));
        line.setiC(resultSet.getInt("I_C"));
        line.setiA(resultSet.getInt("I_O"));
        line.setkA(resultSet.getString("KA"));
        return line;
    }
}
