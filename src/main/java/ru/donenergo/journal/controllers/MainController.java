package ru.donenergo.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.donenergo.journal.dao.LineDAO;
import ru.donenergo.journal.dao.PodstationDAO;
import ru.donenergo.journal.dao.TransformatorDAO;
import ru.donenergo.journal.models.Period;
import ru.donenergo.journal.models.Podstation;
import ru.donenergo.journal.models.Transformator;

import java.util.List;

@Controller
public class MainController {
    private final PodstationDAO podstationDAO;
    private final TransformatorDAO transformatorDAO;
    private final LineDAO lineDAO;
    private String currentDate;
    private String currentPodstation = "10";

    @Autowired
    public MainController(PodstationDAO podstationDAO, TransformatorDAO transformatorDAO, LineDAO lineDAO) {
        this.podstationDAO = podstationDAO;
        this.transformatorDAO = transformatorDAO;
        this.lineDAO = lineDAO;
    }

    @GetMapping("/")
    public String index(Model model) {
        if (currentDate == null) {
            currentDate = podstationDAO.getCurrentDate();
        }
        List<Transformator> transformators= transformatorDAO.getTransformators(currentPodstation);
        model.addAttribute("currentPodstation", currentPodstation);
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("periodList", podstationDAO.getPeriodList());
        model.addAttribute("podstations", podstationDAO.getPodstations());
        model.addAttribute("transformators", transformators);
        model.addAttribute("lines", lineDAO.getPodstationLines(transformators));

        return "index";
    }

    @PostMapping("/index")
    public String selectedDateAndPodstation(@RequestParam(value = "period", required = false) String periodRn,
                                            @RequestParam(value = "podstation", required = false) String podstationRn,
                                            @RequestParam(value = "transformator", required = false) String trans,
                                            Model model) {
        currentPodstation = podstationRn;
        currentDate = periodRn;
        return "redirect:/";
    }
}
