package ru.donenergo.journal.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.stereotype.Component;
import ru.donenergo.journal.mappers.LineMapper;
import ru.donenergo.journal.mappers.TransformatorMapper;
import ru.donenergo.journal.models.Line;
import ru.donenergo.journal.models.Transformator;

import java.util.ArrayList;
import java.util.List;

@Component
public class LineDAO {
    private final JdbcTemplate jdbcTemplate;
    private List<Line> lines;

    public LineDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Line> getPodstationLines(List<Transformator> listTransformators){
        List<Line> allLines = new ArrayList<>();
        for (int transformatorNumber = 0; transformatorNumber < listTransformators.size(); transformatorNumber++) {
            allLines.addAll(getTransformatorLines(listTransformators.get(transformatorNumber).getNum(),
                    listTransformators.get(transformatorNumber).getRn()));
            System.out.println("Total lines Count for section " + transformatorNumber + ": " + allLines.size());
        }
       // System.out.println("All lines count: " + allLines.size());
        return allLines;
    }

    public List<Line> getTransformatorLines(int trNum, int trRn){
        List<Line> transformatorLines = jdbcTemplate.query("SELECT RN, TR_RN, NUM, NAME, I_A, I_B, I_C, I_O, KA FROM LINE WHERE TR_RN = ?;", new Object[]{trRn}, new LineMapper());
        for (Line line : transformatorLines){
            line.setSectionNum(trNum);
        }
        System.out.println("Transformator Lines count: " + transformatorLines.size() + " " + trRn);
        return transformatorLines;
    }
}
