package ru.donenergo.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.donenergo.journal.dao.PodstationDAO;
import ru.donenergo.journal.models.Podstation;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
public class MainController {
    private final PodstationDAO podstationDAO;
    private Mds mds;

    @Autowired
    public MainController(PodstationDAO podstationDAO, Mds mds) {
        this.podstationDAO = podstationDAO;
        this.mds = mds;
    }

    @PostConstruct
    private void invokeMdsValues() {
        mds.setCurrentDate(podstationDAO.getCurrentDate());
        mds.setPeriodList(podstationDAO.getPeriodList());
    }

    private Podstation refreshMdsValues() {
        List<Podstation> podstations = podstationDAO.getListPodstations(mds.getCurrentDate());
        mds.setPodstations(podstations);
        if (mds.getCurrentPodstation() == null) {
            mds.setCurrentPodstation(String.valueOf(podstations.get(0).getRn()));
        }
        mds.setPodstationNum(podstationDAO.getPodstationNumByRn(mds.getCurrentPodstation()));
        mds.setPodstTypes(podstationDAO.getPodstationTypes(mds.getCurrentDate()));
        Podstation sPodstation = podstationDAO.getPodstation(mds.getCurrentPodstation());
        mds.setPodstType(sPodstation.getPodstType());
        return sPodstation;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("sPodstation", refreshMdsValues());
        model.addAttribute(mds);
        return "index";
    }

    @PostMapping("/index")
    public String selectedDateAndPodstation(@RequestParam(value = "period", required = false) String periodRnFromSelect,
                                            @RequestParam(value = "podstation", required = false) String podstationRnFromSelect,
                                            @RequestParam(value = "podstationNum", required = false) String podstationRnFromInput,
                                            @RequestParam(value = "podstType", required = false) String podstTypeForm,
                                            Model model) {
        if (podstationRnFromInput.equals(podstationDAO.getPodstationNumByRn(mds.getCurrentPodstation()))
                && mds.getPodstType().equals(podstTypeForm)) {
            mds.setCurrentPodstation(podstationRnFromSelect);
        } else {
            mds.setPodstType(podstTypeForm);
            mds.setCurrentPodstation(podstationDAO.getPodstationRn(mds.getPodstType(), podstationRnFromInput, mds.getCurrentDate()));
        }
        mds.setCurrentDate(periodRnFromSelect);
        return "redirect:/";
    }

    @RequestMapping("/{podstationRn}")
    public String showPodstation(@PathVariable("podstationRn") String podstationRnFromPath,
                                 @RequestParam(value = "podstation", required = false) String podstationRnFromSelect,
                                 @RequestParam(value = "podstationNum", required = false) String podstationRnFromInput,
                                 @RequestParam(value = "podstType", required = false) String podstTypeForm,
                                 Model model) {
//        model.addAttribute("currentDate", currentDate);
//        model.addAttribute("currentPodstation", podstationRn);
//        model.addAttribute("podstations", podstationDAO.getListPodstations(currentDate));
//        model.addAttribute("sPodstation", podstationDAO.getPodstation(podstationRn));
//        model.addAttribute("periodList", podstationDAO.getPeriodList());
        return "podstation";
    }
}