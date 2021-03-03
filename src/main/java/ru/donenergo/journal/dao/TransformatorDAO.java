package ru.donenergo.journal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.donenergo.journal.models.Transformator;

import java.util.List;

@Component
public class TransformatorDAO {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public TransformatorDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Transformator> getTransformators(String tpRn){
        List<Transformator> lt = jdbcTemplate.query("SELECT * FROM TRANSFORMATOR WHERE TP_RN=?", new Object[]{tpRn}, new TransformatorMapper());
        System.out.println("lt size is: " + lt.size());
        return lt;
    }
}
