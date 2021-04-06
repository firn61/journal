package ru.donenergo.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.donenergo.journal.services.AdminService;
import org.springframework.ui.Model;
import ru.donenergo.journal.services.HostService;

@Controller
public class AdminController {
    private final AdminService adminService;
    private HostService hostService;

    @Autowired
    public AdminController(AdminService adminService, HostService hostService) {
        this.adminService = adminService;
        this.hostService = hostService;
    }

    @GetMapping("/admin")
    public String getAdminPage(Model model){
        model.addAttribute(adminService);
        return "admin";
    }

    @PostMapping("/admin")
    public String saveSystemParameters(){
        return null;
    }

    @PostMapping("/hostsave")
    public String saveHost(){
        return null;
    }
}
