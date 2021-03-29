package ru.donenergo.journal.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.donenergo.journal.dao.HostDAO;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class HostService {
    private HostDAO hostDAO;
    Map<String, String> hosts;
    Map<String, String> res;

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
        res.put("ures", "4");
        res.put("rpvi1", "5");
        res.put("rges0", "6");
    }

    public boolean checkRights(String ip, String targetRes) {
        return res.get(hosts.get(ip)).equals(targetRes);
    }

}
