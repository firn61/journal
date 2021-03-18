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

    private Podstation refreshMdsValues(String rn, String type) {
        List<Podstation> podstations = podstationDAO.getListPodstations(mds.getCurrentDate());
        mds.setPodstations(podstations);
        if (rn.equals("norn")) {
            mds.setCurrentPodstation(String.valueOf(podstations.get(0).getRn()));
        } else {
            for (Podstation p : podstations){
                if(p.getNumStr().equals(rn) && p.getPodstType().equals(type)){
                    mds.setCurrentPodstation(String.valueOf(p.getRn()));
                }
            }
        }
        mds.setPodstationNum(podstationDAO.getPodstationNumByRn(mds.getCurrentPodstation()));
        mds.setPodstTypes(podstationDAO.getPodstationTypes(mds.getCurrentDate()));
        Podstation sPodstation = podstationDAO.getPodstation(mds.getCurrentPodstation());
        mds.setPodstType(sPodstation.getPodstType());
        return sPodstation;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("sPodstation", refreshMdsValues("norn", "notype"));
        model.addAttribute(mds);
        return "index";
    }

//    @PostMapping("/index")
//    public String selectedDateAndPodstation(@RequestParam(value = "period", required = false) String periodRnFromSelect,
//                                            @RequestParam(value = "podstation", required = false) String podstationRnFromSelect,
//                                            @RequestParam(value = "podstationNum", required = false) String podstationRnFromInput,
//                                            @RequestParam(value = "podstType", required = false) String podstTypeForm,
//                                            Model model) {
//        if (podstationRnFromInput.equals(podstationDAO.getPodstationNumByRn(mds.getCurrentPodstation()))
//                && mds.getPodstType().equals(podstTypeForm)) {
//            mds.setCurrentPodstation(podstationRnFromSelect);
//        } else {
//            mds.setPodstType(podstTypeForm);
//            mds.setCurrentPodstation(podstationDAO.getPodstationRn(mds.getPodstType(), podstationRnFromInput, mds.getCurrentDate()));
//        }
//        mds.setCurrentDate(periodRnFromSelect);
//        return "redirect:/";
//    }

    @PostMapping("/show")
    public String editMeasures(@ModelAttribute("sPodstation") Podstation sPodstation){
        System.out.println(sPodstation.getAddress());
        return "edit";
    }

    @RequestMapping("/show")
    public String showPodstation(@RequestParam(value = "period", required = false) String period,
                                 @RequestParam(value = "podstation", required = false) String podstationRnFromSelect,
                                 @RequestParam(value = "podstationNum", required = false) String podstationNumFromInput,
                                 @RequestParam(value = "podstType", required = false) String podstTypeForm,
                                 @RequestParam(value = "action", required = false) String action,
                                 Model model) {
        if ((action == null) || (action.equals("find"))) {
            if (!period.equals(mds.getCurrentDate())) {
                mds.setCurrentDate(period);
                model.addAttribute("sPodstation", refreshMdsValues(podstationNumFromInput, podstTypeForm));
                model.addAttribute(mds);
                return "showpodstation";
            }
            if (!podstationRnFromSelect.equals(mds.getCurrentPodstation())) {
                mds.setCurrentPodstation(podstationRnFromSelect);
                for (Podstation p : mds.getPodstations()){
                    if(String.valueOf(p.getRn()).equals(mds.getCurrentPodstation())){
                        mds.setPodstationNum(p.getNumStr());
                    }
                }
                model.addAttribute(mds);
                model.addAttribute("sPodstation", podstationDAO.getPodstation(mds.getCurrentPodstation()));
                return "showpodstation";
            }
            mds.setPodstationNum(podstationNumFromInput);
            mds.setPodstType(podstTypeForm);
            String podstationRn = podstationDAO.getPodstationRn(mds.getPodstType(), podstationNumFromInput, mds.getCurrentDate());
            mds.setCurrentPodstation(podstationRn);
            model.addAttribute(mds);
            model.addAttribute("sPodstation", podstationDAO.getPodstation(mds.getCurrentPodstation()));
            return "showpodstation";

        } else {
            model.addAttribute(mds);
            model.addAttribute("sPodstation", podstationDAO.getPodstation(mds.getCurrentPodstation()));
            return "editpodstation";
        }
    }
}