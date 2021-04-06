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
    private Map<String, String> hosts;
    private Map<String, String> res = new HashMap<>();

    public Map<String, String> getRes() {
        return res;
    }

    public final String NO_RIGHTS_MESSAGE = "Недостаточно прав для внесения изменений";

    @Autowired
    public HostService(HostDAO hostDAO) {
        this.hostDAO = hostDAO;
    }

    public Map<String, String> getHosts() {
        return hosts;
    }

    @PostConstruct
    private void setInitialState() {
        updateHosts();
        res = hostDAO.getUsr();
    }

    public void updateHosts() {
        hosts = hostDAO.getHosts();
    }

    public String getRightsMessage(String ip, int targetRes) {
        if (res.get(hosts.get(ip)).equals(String.valueOf(targetRes))) {
            return "Ok";
        } else {
            return NO_RIGHTS_MESSAGE;
        }
    }

    public boolean rightsExist(String ip) {
        if (hosts.get(ip) == null) {
            return false;
        } else {
            return true;
        }
    }

    public void addRO(String ip) {
        hostDAO.addRO(ip);
    }

    public String getResNumByIp(String ip) {
        return res.get(hosts.get(ip));
    }

    public boolean checkRights(String ip, int targetRes) {
        System.out.println(res.get(hosts.get(ip)).equals(String.valueOf(targetRes)));
        return res.get(hosts.get(ip)).equals(String.valueOf(targetRes));
    }

}
