package ru.donenergo.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.donenergo.journal.dao.PodstationDAO;
import ru.donenergo.journal.models.Podstation;

import java.util.List;

@Controller
public class MainController {
    private final PodstationDAO podstationDAO;
    private String currentDate;
    private String currentPodstation;
    private String podstType;

    @Autowired
    public MainController(PodstationDAO podstationDAO) {
        this.podstationDAO = podstationDAO;
    }

    @GetMapping("/")
    public String index(Model model) {
        if (currentDate == null) {
            currentDate = podstationDAO.getCurrentDate();
        }
        List<Podstation> podstations = podstationDAO.getListPodstations(currentDate);
        model.addAttribute("podstations", podstations);
        if (currentPodstation == null) {
            currentPodstation = String.valueOf(podstations.get(0).getRn());
        }
        model.addAttribute("currentPodstation", currentPodstation);
        model.addAttribute("podstationNum", podstationDAO.getPodstationNumByRn(currentPodstation));
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("podstTypes", podstationDAO.getPodstationTypes(currentDate));
        model.addAttribute("periodList", podstationDAO.getPeriodList());
        Podstation sPodstation = podstationDAO.getPodstation(currentPodstation);
        podstType = sPodstation.getPodstType();
        model.addAttribute("sPodstation", sPodstation);
        return "index";
    }

    @PostMapping("/index")
    public String selectedDateAndPodstation(@RequestParam(value = "period", required = false) String periodRn,
                                            @RequestParam(value = "podstation", required = false) String podstationRnFromList,
                                            @RequestParam(value = "podstationNum", required = false) String podstationRnFromInput,
                                            @RequestParam(value = "podstType", required = false) String podstTypeForm,
                                            Model model) {

        if (podstationRnFromInput.equals(podstationDAO.getPodstationNumByRn(currentPodstation))
                && podstType.equals(podstTypeForm)) {
            System.out.println("HERE");
            currentPodstation = podstationRnFromList;
        } else {
            podstType = podstTypeForm;
            currentPodstation = podstationDAO.getPodstationRn(podstType, podstationRnFromInput, currentDate);
        }
        currentDate = periodRn;
        return "redirect:/";
    }

    @RequestMapping("/{podstationRn}")
    public String showPodstation(@PathVariable("podstationRn") String podstationRn, Model model) {
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("currentPodstation", podstationRn);
        model.addAttribute("podstations", podstationDAO.getListPodstations(currentDate));
        model.addAttribute("sPodstation", podstationDAO.getPodstation(podstationRn));
        model.addAttribute("periodList", podstationDAO.getPeriodList());
        return "/showPodstation";
    }
}