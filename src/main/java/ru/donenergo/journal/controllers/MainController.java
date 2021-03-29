package ru.donenergo.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.donenergo.journal.dao.HostDAO;
import ru.donenergo.journal.dao.PodstationDAO;
import ru.donenergo.journal.dao.StreetDAO;
import ru.donenergo.journal.models.HouseSegment;
import ru.donenergo.journal.models.Podstation;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
public class MainController {
    private final PodstationDAO podstationDAO;
    private final StreetDAO streetDAO;
    private final HostDAO hostDAO;
    private Mds mds;

    @Autowired
    public MainController(PodstationDAO podstationDAO, StreetDAO streetDAO, HostDAO hostDAO, Mds mds) {
        this.podstationDAO = podstationDAO;
        this.streetDAO = streetDAO;
        this.hostDAO = hostDAO;
        this.mds = mds;
    }

    @PostConstruct
    private void invokeMdsValues() {
        mds.setCurrentDate(podstationDAO.getCurrentDate());
        mds.setPeriodList(podstationDAO.getPeriodList());
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("sPodstation", mds.refreshMdsValues("norn", "notype"));
        mds.setCurrentActivity("show");
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
                             HttpServletRequest request,
                             Model model) {
        System.out.println(request.getRemoteAddr());
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
                                 @RequestParam(value = "currentActivity", required = false) String currentActivity,
                                 Model model) {
        //если подстанция выбрана из списка, или выбран другой период или введен номер и нажата кнопка просмотр
        System.out.println(action);
        if ((action == null) || (action.equals("find")) || (action.equals("view"))) {
            //если изменился период
            if (!period.equals(mds.getCurrentDate())) {
                mds.setCurrentDate(period);
                mds.setsPodstation(mds.refreshMdsValues(podstationNumFromInput, podstTypeForm));
                model.addAttribute(mds);
                model.addAttribute("sPodstation", mds.getsPodstation());
                return mds.getActivityView(currentActivity);
            }
            //если выбрана подстанция из списка
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
                return mds.getActivityView(currentActivity);
            }
            //если нажата кнопка просмотр
            if (action.equals("view")) {
                currentActivity="show";
                mds.setCurrentActivity(currentActivity);
            }
            //если номер подстанции введен вручную
            mds.setPodstationNum(podstationNumFromInput);
            mds.setPodstType(podstTypeForm);
            String podstationRn = podstationDAO.getPodstationRn(mds.getPodstType(), podstationNumFromInput, mds.getCurrentDate());
            mds.setCurrentPodstation(podstationRn);
            mds.setsPodstation(podstationDAO.getPodstation(mds.getCurrentPodstation()));
            model.addAttribute(mds);
            model.addAttribute("sPodstation", mds.getsPodstation());
            return mds.getActivityView(currentActivity);
        }
        if (action.equals("editvalues")) {
            mds.setCurrentActivity("values");
            mds.setsPodstation(podstationDAO.getPodstation(mds.getCurrentPodstation()));
            model.addAttribute(mds);
            model.addAttribute("sPodstation", mds.getsPodstation());
            return "editpodstationvalues";
        }
        if (action.equals("editpodstation")) {
            mds.setCurrentActivity("edit");
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