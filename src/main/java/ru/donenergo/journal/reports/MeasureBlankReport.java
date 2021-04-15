package ru.donenergo.journal.reports;

import ru.donenergo.journal.models.Podstation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeasureBlankReport {
    public static List<String[]> getBlankReportLines(Podstation podstation) {
        List<String[]> result = new ArrayList<>();
        int trNum = podstation.getTrList().size();
        int maxRowCount = 0;
        if (trNum == 0) {
            return result;
        } else {
            List<Integer> rowCounts = new ArrayList<>();
            for (int i = 0; i < trNum; i++) {
                rowCounts.add(podstation.getTrList().get(i).getListLines().size());
            }
            maxRowCount = Collections.max(rowCounts);
            for (int i = 0; i < maxRowCount; i++) {
                result.add(new String[trNum]);
            }
            for (int i = 0; i < trNum; i++) {
                for (int j = 0; j < podstation.getTrList().get(i).getListLines().size(); j++) {
                    result.get(j)[i] = podstation.getTrList().get(i).getListLines().get(j).getName();
                }
            }
        }
        return result;
    }
}
