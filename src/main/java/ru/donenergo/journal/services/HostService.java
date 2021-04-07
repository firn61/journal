package ru.donenergo.journal.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.donenergo.journal.dao.HostDAO;
import ru.donenergo.journal.models.Host;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HostService {

    public HostDAO hostDAO;
    private Map<String, String> hosts;
    private Map<String, String> res = new HashMap<>();
    private List<Host> hostsList = new ArrayList<>();
    public Map<String, String> getRes() {
        return res;
    }
    public List<Host> getHostsList() {
        return hostsList;
    }
    public final String NO_RIGHTS_MESSAGE = "Недостаточно прав для внесения изменений";

    public void setHostsList(List<Host> hostsList) {
        this.hostsList = hostsList;
    }

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
        hostsList.clear();
    }

    public List<Host> populateHosts(){
        List<Host> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : hosts.entrySet()){
            result.add(new Host(entry.getKey(), entry.getValue()));
        }
        return result;
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
