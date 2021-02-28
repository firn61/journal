package ru.donenergo.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.donenergo.journal.dao.PodstationDAO;
import ru.donenergo.journal.dao.TransformatorDAO;
import ru.donenergo.journal.models.Transformator;

@Controller
public class PodstationController {
    private final PodstationDAO podstationDAO;
    private final TransformatorDAO transformatorDAO;
    @Autowired
    public PodstationController(PodstationDAO podstationDAO, TransformatorDAO transformatorDAO) {
        this.podstationDAO = podstationDAO;
        this.transformatorDAO = transformatorDAO;
    }
    @GetMapping ("/")
    public String index(Model model){
        model.addAttribute("currentDate", podstationDAO.getCurrentDate());
        model.addAttribute("periodList", podstationDAO.getPeriodList());
        model.addAttribute("podstations", podstationDAO.index());
        model.addAttribute("transformators", transformatorDAO.getTransformators(1));
        return "index";
    }
}
