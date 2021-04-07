package ru.donenergo.journal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.donenergo.journal.models.Street;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StreetMapper implements RowMapper<Street> {

    @Override
    public Street mapRow(ResultSet resultSet, int i) throws SQLException {
        Street street = new Street();
        street.setRn(resultSet.getInt("RN"));
        street.setStreetName(resultSet.getString("STREET_NAME"));
        street.setStreetType(resultSet.getString("STREET_TYPE"));
        street.setPostCode(resultSet.getString("POSTCODE"));
        return street;
    }
}