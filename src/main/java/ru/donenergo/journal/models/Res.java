package ru.donenergo.journal.models;

import java.util.HashMap;
import java.util.Map;

public class Res {
    public static Map<Integer, String> resMap = new HashMap<Integer, String>(){{
        put(1, "ВРЭС");
        put(2, "ЗРЭС");
        put(3, "СРЭС");
        put(4, "ЮРЭС");
        put(5, "РПВИ");
        put(6, "РГЭС");
    }};
}
