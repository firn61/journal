package ru.donenergo.journal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.donenergo.journal.models.Transformator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TransformatorMapper implements RowMapper<Transformator> {
    @Override
    public Transformator mapRow(ResultSet resultSet, int i) throws SQLException {
        Transformator transformator = new Transformator();
        transformator.setRn(resultSet.getInt("RN"));
        transformator.setTpRn(resultSet.getInt("TP_RN"));
        transformator.setNum(resultSet.getInt("NUM"));
        transformator.setFider(resultSet.getString("FIDER"));
        transformator.setPower(resultSet.getInt("POWER"));
        transformator.setuA(resultSet.getInt("U_A"));
        transformator.setuB(resultSet.getInt("U_B"));
        transformator.setuC(resultSet.getInt("U_C"));
        transformator.setiA(resultSet.getInt("I_A"));
        transformator.setiB(resultSet.getInt("I_B"));
        transformator.setiC(resultSet.getInt("I_C"));
        transformator.setiN(resultSet.getInt("I_N"));
        transformator.setDateTime(resultSet.getString("DATETIME"));
        transformator.setMonter(resultSet.getString("MONTER"));
        return transformator;
    }
}
