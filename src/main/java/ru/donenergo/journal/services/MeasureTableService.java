package ru.donenergo.journal.services;

import ru.donenergo.journal.models.Podstation;
import ru.donenergo.journal.models.Transformator;

import java.util.ArrayList;
import java.util.List;

public class MeasureTableService {

    public static List<String[]> getTable(Podstation p) {
        int[] rowPrefix = new int[p.getTrCount()];
        rowPrefix[0] = 0;
        for (int i = 1; i < p.getTrCount(); i++) {
            if (p.getTrList().get(i).getLinesCount() <= 9) {
                rowPrefix[i] = 9;
            } else {
                rowPrefix[i] = p.getTrList().get(i).getLinesCount();
            }
        }
        List<String[]> result = new ArrayList<>();
        for (int i = 0; i < p.getTrList().size(); i++) {
            if (p.getTrList().get(i).getLinesCount() <= 9) {
                for (int j = 0; j < 9; j++) {
                    result.add(new String[11]);
                }
            } else {
                for (int j = 0; j < p.getTrList().get(i).getLinesCount(); j++) {
                    result.add(new String[11]);
                }
            }
            result.get(0+rowPrefix[i])[10] = p.getTrList().get(i).getMonter();
            result.get(0+rowPrefix[i])[0] = "T-" + p.getTrList().get(i).getNum() + ", " + p.getTrList().get(i).getPower() + " " + p.getTrList().get(i).getFider();
            result.get(0+rowPrefix[i])[1] = "A";
            result.get(0+rowPrefix[i])[2] = p.getTrList().get(i).getuA() + "";
            result.get(0+rowPrefix[i])[3] = p.getTrList().get(i).getiA() + "";
            result.get(2+rowPrefix[i])[1] = "B";
            result.get(2+rowPrefix[i])[2] = p.getTrList().get(i).getuB() + "";
            result.get(2+rowPrefix[i])[3] = p.getTrList().get(i).getiB() + "";
            result.get(4+rowPrefix[i])[1] = "C";
            result.get(4+rowPrefix[i])[2] = p.getTrList().get(i).getuC() + "";
            result.get(4+rowPrefix[i])[3] = p.getTrList().get(i).getiC() + "";
            result.get(6+rowPrefix[i])[1] = "N";
            for (int j = 0; j < p.getTrList().get(i).getLinesCount(); j++) {
                result.get(j+rowPrefix[i])[4] = p.getTrList().get(i).getListLines().get(j).getName();
                result.get(j+rowPrefix[i])[5] = p.getTrList().get(i).getListLines().get(j).getkA();
                result.get(j+rowPrefix[i])[6] = p.getTrList().get(i).getListLines().get(j).getiA() + "";
                result.get(j+rowPrefix[i])[7] = p.getTrList().get(i).getListLines().get(j).getiB() + "";
                result.get(j+rowPrefix[i])[8] = p.getTrList().get(i).getListLines().get(j).getiC() + "";
                result.get(j+rowPrefix[i])[9] = p.getTrList().get(i).getListLines().get(j).getiO() + "";
            }
        }
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i)[0] + " " +
                    result.get(i)[1] + " " +
                    result.get(i)[2] + " " +
                    result.get(i)[3] + " " +
                    result.get(i)[4] + " " +
                    result.get(i)[5] + " " +
                    result.get(i)[6] + " " +
                    result.get(i)[7] + " " +
                    result.get(i)[8] + " " +
                    result.get(i)[9] + " " +
                    result.get(i)[10]
            );
        }
        return result;
    }
}

