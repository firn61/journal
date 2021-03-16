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
        model.addAttribute("podstationNum", podstationDAO.getPodstationNumByRn(currentPodstation));
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("podstTypes", podstationDAO.getPodstationTypes(currentDate));
        model.addAttribute("periodList", podstationDAO.getPeriodList());
        model.addAttribute("podstations", podstationDAO.getListPodstations(currentDate));
        model.addAttribute("sPodstation", podstationDAO.getPodstation(currentIntPodstation));
        return "index";
    }

    @PostMapping("/index")
    public String selectedDateAndPodstation(@RequestParam(value = "period", required = false) String periodRn,
                                            @RequestParam(value = "podstation", required = false) String podstationRnFromList,
                                            @RequestParam(value = "podstationNum", required = false) String podstationRnFromInput,
                                            @RequestParam(value = "podstType", required = false) String podstType,
                                            Model model) {
        if (podstationRnFromInput.equals(podstationDAO.getPodstationNumByRn(currentPodstation))) {
            currentPodstation = podstationRnFromList;
        } else {
            currentPodstation = podstationDAO.getPodstationRn("ТП", podstationRnFromInput, currentDate);
        }
        currentIntPodstation = Integer.valueOf(currentPodstation);
        currentDate = periodRn;
        return "redirect:/";
    }

    @RequestMapping("/{podstationRn}")
    public String showPodstation(@PathVariable("podstationRn") int podstationRn, Model model) {
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("currentPodstation", podstationRn);
        model.addAttribute("podstations", podstationDAO.getListPodstations(currentDate));
        model.addAttribute("sPodstation", podstationDAO.getPodstation(podstationRn));
        model.addAttribute("periodList", podstationDAO.getPeriodList());
        return "/showPodstation";
    }
}