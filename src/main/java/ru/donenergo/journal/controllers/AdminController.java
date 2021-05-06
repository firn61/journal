package ru.donenergo.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.donenergo.journal.dao.PodstationDAO;
import ru.donenergo.journal.models.Host;
import ru.donenergo.journal.models.Hosts;
import ru.donenergo.journal.services.AdminService;
import org.springframework.ui.Model;
import ru.donenergo.journal.services.HostService;

@Controller
public class AdminController {
    private final AdminService adminService;
    private final PodstationDAO podstationDAO;
    private HostService hostService;
    private Hosts hosts;

    @Autowired
    public AdminController(AdminService adminService, HostService hostService, PodstationDAO podstationDAO) {
        this.adminService = adminService;
        this.hostService = hostService;
        this.podstationDAO = podstationDAO;
    }

    @GetMapping("/admin")
    public String getAdminPage(Model model) {
        model.addAttribute(adminService);
        hosts = new Hosts(hostService.populateHosts());
        model.addAttribute("hosts", hosts);
        return "admin";
    }

    @PostMapping("/admin")
    public String saveSystemParameters(@RequestParam(value = "currentDate") String currentDate,
                                       @RequestParam(value = "filesDir1") String filesDir1,
                                       @RequestParam(value = "filesDir2") String filesDir2,
                                       Model model) {
        adminService.updateSystemParams(filesDir1, filesDir2, currentDate);
        model.addAttribute("statusMessage", "Системные настройки обновлены");
        model.addAttribute(adminService);
        return "admin";
    }

    @PostMapping("/hostsave")
    public String saveHost(@ModelAttribute("hosts") Hosts rHosts,
                           Model model) {
        adminService.updateChangedHosts(hosts, rHosts);
        hostService.updateHosts();
        hosts = new Hosts(hostService.populateHosts());
        model.addAttribute("hosts", hosts);
        model.addAttribute(adminService);
        return "admin";
    }

    @PostMapping("/newperiod")
    public String newPeriod(Model model){
        model.addAttribute("successMessage", "Данные скопированы!");
        String newPeriod = podstationDAO.startNewPeriod();
        adminService.updateCurrentDate(newPeriod);
        model.addAttribute(adminService);
        hosts = new Hosts(hostService.populateHosts());
        model.addAttribute("hosts", hosts);
        return "admin";
    }
}
