package ru.donenergo.journal.reports;

import ru.donenergo.journal.models.Podstation;
import ru.donenergo.journal.models.Transformator;

import java.util.ArrayList;
import java.util.List;

public class MeasureTableReport {

    public static List<String[]> getTable(Podstation p) {
        int[] rowPrefix = new int[p.getTrCount()];
        rowPrefix[0] = 0;
        for (int i = 1; i < p.getTrCount(); i++) {
            if (p.getTransformators().get(i).getLinesCount() <= 9) {
                rowPrefix[i] = 9;
            } else {
                rowPrefix[i] = p.getTransformators().get(i).getLinesCount();
            }
        }
        List<String[]> result = new ArrayList<>();
        for (int i = 0; i < p.getTransformators().size(); i++) {
            if (p.getTransformators().get(i).getLinesCount() <= 9) {
                for (int j = 0; j < 9; j++) {
                    result.add(new String[11]);
                }
            } else {
                for (int j = 0; j < p.getTransformators().get(i).getLinesCount(); j++) {
                    result.add(new String[11]);
                }
            }
            result.get(0+rowPrefix[i])[10] = p.getTransformators().get(i).getMonter();
            result.get(0+rowPrefix[i])[0] = "T-" + p.getTransformators().get(i).getNum() + ", " + p.getTransformators().get(i).getPower() + " " + p.getTransformators().get(i).getFider();
            result.get(0+rowPrefix[i])[1] = "A";
            result.get(0+rowPrefix[i])[2] = p.getTransformators().get(i).getuA() + "";
            result.get(0+rowPrefix[i])[3] = p.getTransformators().get(i).getiA() + "";
            result.get(2+rowPrefix[i])[1] = "B";
            result.get(2+rowPrefix[i])[2] = p.getTransformators().get(i).getuB() + "";
            result.get(2+rowPrefix[i])[3] = p.getTransformators().get(i).getiB() + "";
            result.get(4+rowPrefix[i])[1] = "C";
            result.get(4+rowPrefix[i])[2] = p.getTransformators().get(i).getuC() + "";
            result.get(4+rowPrefix[i])[3] = p.getTransformators().get(i).getiC() + "";
            result.get(6+rowPrefix[i])[1] = "N";
            for (int j = 0; j < p.getTransformators().get(i).getLinesCount(); j++) {
                result.get(j+rowPrefix[i])[4] = p.getTransformators().get(i).getListLines().get(j).getName();
                result.get(j+rowPrefix[i])[5] = p.getTransformators().get(i).getListLines().get(j).getkA();
                result.get(j+rowPrefix[i])[6] = p.getTransformators().get(i).getListLines().get(j).getiA() + "";
                result.get(j+rowPrefix[i])[7] = p.getTransformators().get(i).getListLines().get(j).getiB() + "";
                result.get(j+rowPrefix[i])[8] = p.getTransformators().get(i).getListLines().get(j).getiC() + "";
                result.get(j+rowPrefix[i])[9] = p.getTransformators().get(i).getListLines().get(j).getiO() + "";
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

