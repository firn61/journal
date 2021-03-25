package ru.donenergo.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.donenergo.journal.dao.PodstationDAO;
import ru.donenergo.journal.dao.StreetDAO;
import ru.donenergo.journal.models.Podstation;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Controller
public class MainController {
    private final PodstationDAO podstationDAO;
    private final StreetDAO streetDAO;
    private Mds mds;

    @Autowired
    public MainController(PodstationDAO podstationDAO, StreetDAO streetDAO, Mds mds) {
        this.podstationDAO = podstationDAO;
        this.streetDAO = streetDAO;
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
            for (Podstation p : podstations) {
                if (p.getNumStr().equals(rn) && p.getPodstType().equals(type)) {
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

    @PostMapping("/editvalues")
    public String editPodstationValues(@ModelAttribute("sPodstation") Podstation sPodstation,
                                       Model model) {
        podstationDAO.updatePodstationValues(sPodstation);
        model.addAttribute(mds);
        model.addAttribute("sPodstation", sPodstation);
        return "showpodstation";
    }

    @PostMapping("/edit")
    public String editPodstation(@ModelAttribute("sPodstation") Podstation sPodstation,
                                 @RequestParam(value = "action") String action,
                                 Model model) {
        if (action.equals("save")) {
            podstationDAO.updatePodstation(sPodstation);
        } else {
            String[] targetValues = action.split("&");
            if (targetValues[0].equals("trans")) {
                if (targetValues[1].equals("add")) {
                    podstationDAO.addTransformator(targetValues[2], sPodstation.getTrCount() + 1);
                }
                if (targetValues[1].equals("del")) {
                    podstationDAO.deleteTrans(targetValues[2]);
                }
            }
            if (targetValues[0].equals("line")) {
                if (targetValues[1].equals("add")) {
                    String[] addLineParams = targetValues[2].split("-");
                    int linesCount = Integer.valueOf(addLineParams[1]);
                    addLineParams[1] = String.valueOf(Integer.valueOf(linesCount + 1));
                    podstationDAO.addLine(addLineParams[0], addLineParams[1]);
                }
                if (targetValues[1].equals("del")) {
                    podstationDAO.deleteLine(targetValues[2]);
                }
                if (targetValues[1].equals("up")) {
                    podstationDAO.moveLine(targetValues[2], "up");
                }
                if (targetValues[1].equals("down")) {
                    podstationDAO.moveLine(targetValues[2], "down");
                }
            }
        }
        model.addAttribute(mds);
        model.addAttribute("sPodstation", podstationDAO.getPodstation(String.valueOf(sPodstation.getRn())));
        return "editpodstation";
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
                for (Podstation p : mds.getPodstations()) {
                    if (String.valueOf(p.getRn()).equals(mds.getCurrentPodstation())) {
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
        }
        if (action.equals("editvalues")) {
            model.addAttribute(mds);
            model.addAttribute("sPodstation", podstationDAO.getPodstation(mds.getCurrentPodstation()));
            return "editpodstationvalues";
        }
        if (action.equals("editpodstation")) {
            model.addAttribute(mds);
            model.addAttribute("sPodstation", podstationDAO.getPodstation(mds.getCurrentPodstation()));
            return "editpodstation";
        }
        if (action.equals("streets")){
            model.addAttribute("streets", streetDAO.getStreets());
            model.addAttribute(mds);
            model.addAttribute("sPodstation", podstationDAO.getPodstation(mds.getCurrentPodstation()));
            return "streets";
        }
        else {
            return "error";
        }
    }
}