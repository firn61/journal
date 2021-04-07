package ru.donenergo.journal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.donenergo.journal.dao.SystemDAO;
import ru.donenergo.journal.models.Host;
import ru.donenergo.journal.models.Hosts;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;


@Component
public class AdminService {

    private SystemDAO systemDAO;
    private HostService hostService;
    private String filesDir1;
    private String filesDir2;
    private String currentDate;

    public HostService getHostService() {
        return hostService;
    }

    public String getFilesDir1() {
        return filesDir1;
    }

    public void setFilesDir1(String filesDir1) {
        this.filesDir1 = filesDir1;
    }

    public String getFilesDir2() {
        return filesDir2;
    }

    public void setFilesDir2(String filesDir2) {
        this.filesDir2 = filesDir2;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    @Autowired
    public AdminService(SystemDAO systemDAO, HostService hostService) {
        this.systemDAO = systemDAO;
        this.hostService = hostService;
    }

    public void updateChangedHosts(Hosts oldHosts, Hosts newHosts){
        for (Host oldHost : oldHosts.getHosts()) {
            for (Host newHost : newHosts.getHosts()){
                if(oldHost.getIp().equals(newHost.getIp())){
                    if(!oldHost.getRights().equals(newHost.getRights())){
                        hostService.hostDAO.updateHost(newHost.getIp(), newHost.getRights());
                    }
                }
            }
        }
    }


    public void updateSystemParams(String vFilesDir, String vFilesDir2, String vCurrentDate){
        systemDAO.updateSystemValue(vFilesDir, "filesdir");
        systemDAO.updateSystemValue(vFilesDir2, "filesdir2");
        systemDAO.updateSystemValue(vCurrentDate, "currentDate");
        initValues();
    }

    @PostConstruct
    void initValues() {
        setFilesDir1(systemDAO.getSystemValue("filesdir"));
        setFilesDir2(systemDAO.getSystemValue("filesdir2"));
        setCurrentDate(systemDAO.getSystemValue("currentDate"));
    }
}
