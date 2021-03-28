package ru.donenergo.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.donenergo.journal.dao.PodstationDAO;
import ru.donenergo.journal.dao.StreetDAO;
import ru.donenergo.journal.models.HouseSegment;
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
        mds.setsPodstation(sPodstation);
        mds.setPodstType(sPodstation.getPodstType());
        return sPodstation;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("sPodstation", refreshMdsValues("norn", "notype"));
        model.addAttribute(mds);
        return "index";
    }

    @GetMapping("/streets")
    public String getStreets(@RequestParam(value = "street") String street,
                             @RequestParam(value = "housenum") String houseNum,
                             @RequestParam(value = "letter") String letter,
                             @RequestParam(value = "action") String action,
                             @RequestParam(value = "podstNum") String podstNum,
                             @RequestParam(value = "podstType") String podstType,
                             Model model) {
        System.out.println(street + " " + houseNum + " " + letter + " " + action + ", " + podstType + ", " + podstNum);
        if (action.equals("searchByNum")) {
            if (!podstNum.equals(mds.getPodstationNum())) {
                String newPodstationRn = podstationDAO.getPodstationRn(podstType, podstNum, mds.getCurrentDate());
                mds.setsPodstation(podstationDAO.getPodstation(newPodstationRn));
            }
            List<HouseSegment> houseSegmentList;
            if (podstType.equals("ТП")) {
                houseSegmentList = streetDAO.getHouseSegmentsTp(mds.getsPodstation().getPodstType() + mds.getsPodstation().getNumStr());
            } else {
                houseSegmentList = streetDAO.getHouseSegmentsRp(mds.getsPodstation().getPodstType() + mds.getsPodstation().getNumStr());
            }
            model.addAttribute("streets", streetDAO.getStreets());
            model.addAttribute(houseSegmentList);
            model.addAttribute(mds);
        } else {
            String[] streetParams = street.split(", ");
            System.out.println(streetParams[0] + " " + streetParams[1]);
            List<HouseSegment> houseSegmentList;
            if ((houseNum.length() == 0) || (houseNum.equals("0"))) {
                houseSegmentList = streetDAO.getHouseSegmentByStreet(streetParams[0], streetParams[1]);
            } else {
                houseSegmentList = streetDAO.getHouseSegmentByStreetAndNum(streetParams[0], streetParams[1], houseNum);
            }
            model.addAttribute("selectedStreet", streetParams[0]);
            model.addAttribute("streets", streetDAO.getStreets());
            model.addAttribute(houseSegmentList);
            model.addAttribute(mds);
        }
        return "streets";
    }

    @PostMapping("/editvalues")
    public String editPodstationValues(@ModelAttribute("sPodstation") Podstation sPodstation,
                                       Model model) {
        podstationDAO.updatePodstationValues(sPodstation);
        model.addAttribute(mds);
        mds.setsPodstation(sPodstation);
        model.addAttribute("sPodstation", mds.getsPodstation());
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
        mds.setsPodstation(podstationDAO.getPodstation(String.valueOf(sPodstation.getRn())));
        model.addAttribute("sPodstation", mds.getsPodstation());
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
                mds.setsPodstation(refreshMdsValues(podstationNumFromInput, podstTypeForm));
                model.addAttribute(mds);
                model.addAttribute("sPodstation", mds.getsPodstation());
                return "showpodstation";
            }
            if (!podstationRnFromSelect.equals(mds.getCurrentPodstation())) {
                mds.setCurrentPodstation(podstationRnFromSelect);
                for (Podstation p : mds.getPodstations()) {
                    if (String.valueOf(p.getRn()).equals(mds.getCurrentPodstation())) {
                        mds.setPodstationNum(p.getNumStr());
                    }
                }
                mds.setsPodstation(podstationDAO.getPodstation(mds.getCurrentPodstation()));
                model.addAttribute(mds);
                model.addAttribute("sPodstation", mds.getsPodstation());
                return "showpodstation";
            }
            mds.setPodstationNum(podstationNumFromInput);
            mds.setPodstType(podstTypeForm);
            String podstationRn = podstationDAO.getPodstationRn(mds.getPodstType(), podstationNumFromInput, mds.getCurrentDate());
            mds.setCurrentPodstation(podstationRn);
            mds.setsPodstation(podstationDAO.getPodstation(mds.getCurrentPodstation()));
            model.addAttribute(mds);
            model.addAttribute("sPodstation", mds.getsPodstation());
            return "showpodstation";
        }
        if (action.equals("editvalues")) {
            mds.setsPodstation(podstationDAO.getPodstation(mds.getCurrentPodstation()));
            model.addAttribute(mds);
            model.addAttribute("sPodstation", mds.getsPodstation());
            return "editpodstationvalues";
        }
        if (action.equals("editpodstation")) {
            mds.setsPodstation(podstationDAO.getPodstation(mds.getCurrentPodstation()));
            model.addAttribute(mds);
            model.addAttribute("sPodstation", mds.getsPodstation());
            return "editpodstation";
        }
        if (action.equals("streets")) {
            model.addAttribute("streets", streetDAO.getStreets());
            mds.setsPodstation(podstationDAO.getPodstation(mds.getCurrentPodstation()));
            model.addAttribute(mds);
            List<HouseSegment> houseSegmentList;
            if (mds.getsPodstation().getPodstType().equals("ТП")) {
                houseSegmentList = streetDAO.getHouseSegmentsTp(mds.getsPodstation().getPodstType() + mds.getsPodstation().getNumStr());
            } else {
                houseSegmentList = streetDAO.getHouseSegmentsRp(mds.getsPodstation().getPodstType() + mds.getsPodstation().getNumStr());
            }
            model.addAttribute("selectedStreet", streetDAO.getStreets().get(0).getStreetName());
            model.addAttribute("streets", streetDAO.getStreets());
            model.addAttribute(houseSegmentList);
            model.addAttribute("sPodstation", mds.getsPodstation());
            return "streets";
        }
        if (action.equals("backfromstreets")) {
            model.addAttribute(mds);
            model.addAttribute("sPodstation", mds.getsPodstation());
            return "showpodstation";
        } else {
            return "error";
        }
    }
}