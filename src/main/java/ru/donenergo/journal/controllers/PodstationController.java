package ru.donenergo.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.donenergo.journal.dao.PodstationDAO;
import ru.donenergo.journal.dao.TransformatorDAO;
import ru.donenergo.journal.models.Period;
import ru.donenergo.journal.models.Podstation;
import ru.donenergo.journal.models.Transformator;

@Controller
public class PodstationController {
    private final PodstationDAO podstationDAO;
    private final TransformatorDAO transformatorDAO;
    private String currentDate;
    private String currentPodstation = "10";

    @Autowired
    public PodstationController(PodstationDAO podstationDAO, TransformatorDAO transformatorDAO) {
        this.podstationDAO = podstationDAO;
        this.transformatorDAO = transformatorDAO;
    }

    @GetMapping("/")
    public String index(Model model) {
        if (currentDate == null) {
            currentDate = podstationDAO.getCurrentDate();
            System.out.println("CurrentDate is null, set to: " + currentDate);
        }
        model.addAttribute("currentPodstation", currentPodstation);
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("periodList", podstationDAO.getPeriodList());
        model.addAttribute("podstations", podstationDAO.getPodstations());
        model.addAttribute("transformators", transformatorDAO.getTransformators(currentPodstation));
        return "index";
    }

    @PostMapping("/index")
    public String selectedDateAndPodstation(@RequestParam(value = "period", required = false) String periodRn,
                                            @RequestParam(value = "podstation", required = false) String podstationRn,
                                            @RequestParam(value = "transformator", required = false) String trans,
                                            Model model) {
        if (trans != null) {
            System.out.println("trans not null");
        } else {
            System.out.println("trans is null");
        }

        currentPodstation = podstationRn;
        currentDate = periodRn;
        System.out.println("periodRn is :" + periodRn);
        System.out.println("currentDate is: " + currentDate);
        System.out.println("hello");
        return "redirect:/";
    }
}
