package ru.donenergo.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.donenergo.journal.dao.PodstationDAO;

@Controller
public class MainController {
    private final PodstationDAO podstationDAO;
    private String currentDate;
    private String currentPodstation = "10";
    private int currentIntPodstation = 10;

    @Autowired
    public MainController(PodstationDAO podstationDAO) {
        this.podstationDAO = podstationDAO;
    }

    @GetMapping("/")
    public String index(Model model) {
        if (currentDate == null) {
            currentDate = podstationDAO.getCurrentDate();
        }
        model.addAttribute("currentPodstation", currentPodstation);
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("periodList", podstationDAO.getPeriodList());
        model.addAttribute("podstations", podstationDAO.getPodstations(currentDate));
        model.addAttribute("sPodstation", podstationDAO.getPodstation(currentIntPodstation));
        return "index";
    }

    @PostMapping("/index")
    public String selectedDateAndPodstation(@RequestParam(value = "period", required = false) String periodRn,
                                            @RequestParam(value = "podstation", required = false) String podstationRn,
                                            Model model) {
        currentIntPodstation = Integer.valueOf(currentPodstation);
        currentPodstation = podstationRn;
        currentDate = periodRn;
        return "redirect:/";
    }

    @RequestMapping("/{podstationRn}")
    public String showPodstation(@PathVariable("podstationRn") int podstationRn, Model model){
        model.addAttribute("rn", podstationRn);
        return "/showPodstation";
    }
}