package ru.donenergo.journal.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.donenergo.journal.dao.HostDAO;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class HostService {
    private HostDAO hostDAO;
    Map<String, String> hosts;
    Map<String, String> res = new HashMap<>();
    public final String NO_RIGHTS_MESSAGE = "Недостаточно прав для внесения изменений";

    @Autowired
    public HostService(HostDAO hostDAO) {
        this.hostDAO = hostDAO;
    }

    @PostConstruct
    private void setInitialState() {
        hosts = hostDAO.getHosts();
        res.put("vres1", "1");
        res.put("zres1", "2");
        res.put("sres1", "3");
        res.put("ures1", "4");
        res.put("rpvi1", "5");
        res.put("rges0", "6");
    }

    public String getRightsMessage(String ip, int targetRes) {
        if (res.get(hosts.get(ip)).equals(String.valueOf(targetRes))){
            return "Ok";
        } else {
            return NO_RIGHTS_MESSAGE;
        }
    }

    public boolean rightsExist(String ip){
        if(hosts.get(ip) == null) {
            return false;
        } else {
            return true;
        }
    }

    public String getResNumByIp(String ip){
        return res.get(hosts.get(ip));
    }

    public boolean checkRights(String ip, int targetRes) {
        System.out.println(res.get(hosts.get(ip)).equals(String.valueOf(targetRes)));
        return res.get(hosts.get(ip)).equals(String.valueOf(targetRes));
    }

}
