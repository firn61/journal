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
import ru.donenergo.journal.services.HostService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class MainController {
    private final PodstationDAO podstationDAO;
    private final StreetDAO streetDAO;
    private final HostService hostService;
    private Mds mds;

    @Autowired
    public MainController(PodstationDAO podstationDAO, StreetDAO streetDAO, HostService hostService, Mds mds) {
        this.podstationDAO = podstationDAO;
        this.streetDAO = streetDAO;
        this.hostService = hostService;
        this.mds = mds;
    }

    @PostConstruct
    private void invokeMdsValues() {
        mds.setCurrentDate(podstationDAO.getCurrentDate());
        mds.setPeriodList(podstationDAO.getPeriodList());
    }

    @GetMapping("/")
    public String index(Model model,
                        HttpServletRequest request) {
        if (!hostService.rightsExist(request.getRemoteAddr())) {
            hostService.addRO(request.getRemoteAddr());
            hostService.updateHosts();
        }
        model.addAttribute("sPodstation", mds.refreshMdsValues("norn", "notype"));
        mds.setCurrentActivity("show");
        model.addAttribute(mds);
        return "index";
    }

    @GetMapping("/addpodstation")
    public String addPodstation(Model model,
                                HttpServletRequest request) {
        if (hostService.rightsExist(request.getRemoteAddr())) {
            return "addpodstation";
        } else {
            model.addAttribute(mds);
            model.addAttribute("rightsMessage", hostService.getRightsMessage(request.getRemoteAddr(), mds.getsPodstation().getResNum()) + "Невозможно добавить подстанцию");
            model.addAttribute("sPodstation", mds.getsPodstation());
            return "editpodstation";
        }
    }

    @GetMapping("/savepodstation")
    public String savePodstation(@RequestParam(value = "action") String action,
                                 @RequestParam(value = "podsttype") String podstType,
                                 @RequestParam(value = "num") String num,
                                 @RequestParam(value = "address") String address,
                                 HttpServletRequest request,
                                 Model model) {
        if (action.equals("cancel")) {
            model.addAttribute(mds);
            model.addAttribute("rightsMessage", hostService.getRightsMessage(request.getRemoteAddr(), mds.getsPodstation().getResNum()));
            model.addAttribute("sPodstation", mds.getsPodstation());
        } else {
            if ((podstationDAO.isPodstationExist(podstType, num, mds.getCurrentDate()) == 0) && (!hostService.getResNumByIp(request.getRemoteAddr()).equals("6"))) {
                String newPodstationRn = podstationDAO.addPodstation(podstType, num, hostService.getResNumByIp(request.getRemoteAddr()), mds.getCurrentDate(), address);
                mds.setsPodstation(podstationDAO.getPodstation(newPodstationRn));
                mds.addNewPodstationToList(mds.getsPodstation());
                mds.setCurrentPodstation(String.valueOf(mds.getsPodstation().getRn()));
                model.addAttribute(mds);
                model.addAttribute("rightsMessage", hostService.getRightsMessage(request.getRemoteAddr(), mds.getsPodstation().getResNum()));
                model.addAttribute("sPodstation", mds.getsPodstation());

            } else {
                model.addAttribute(mds);
                model.addAttribute("rightsMessage", "Ошибка при добавлении подстанции. Уже существует или недостачно прав.");
                model.addAttribute("sPodstation", mds.getsPodstation());
            }
        }
        return "editpodstation";
    }

    @GetMapping("/streetsedit")
    public String streetsEdit(@RequestParam(value = "street") String street,
                              @RequestParam(value = "action", required = false) String action,
                              @RequestParam(value = "trans", required = false) Integer trans,
                              @RequestParam(value = "delete", required = false) String delete,
                              @RequestParam(value = "housenum1", required = false) Integer housenum1,
                              @RequestParam(value = "housenum2", required = false) Integer housenum2,
                              @RequestParam(value = "letter1", required = false) String letter1,
                              @RequestParam(value = "letter2", required = false) String letter2,
                              HttpServletRequest request,
                              Model model) {
        model.addAttribute("sPodstation", mds.getsPodstation());

        if (action != null) {
            if (action.equals("backfromstreetsedit")) {
                model.addAttribute("rightsMessage", hostService.getRightsMessage(request.getRemoteAddr(), mds.getsPodstation().getResNum()));
                model.addAttribute(mds);
                return "editpodstation";
            }
            if (action.equals("addHouse")) {
                housenum2 = housenum1;
            }
            if ((action.equals("addInterval")) || action.equals("addHouse")) {
                if ((housenum1 != null) && (housenum2 != null) && (street != null)) {
                    String[] streetParams = street.split(", ");
                    streetDAO.addSegment(mds.getsPodstation().getPodstType() + mds.getsPodstation().getNumStr(),
                            trans,
                            mds.getsPodstation().getTrList().get(trans - 1).getFider(),
                            streetDAO.getStreetRnByName(streetParams[0], streetParams[1]),
                            streetParams[0],
                            streetParams[1],
                            housenum1,
                            letter1,
                            housenum2,
                            letter2);
                }
            }
        } else if ((trans != null) || (delete != null)) {
            if (delete != null) {
                streetDAO.deleteHouseSegment(delete);
            }
        }
        model.addAttribute("houseSegments", streetDAO.getHouseSegmentsByTr(mds.getsPodstation().getPodstType() + mds.getsPodstation().getNumStr(), Integer.valueOf(trans)));
        model.addAttribute("selectedTransformator", trans);
        model.addAttribute("streets", streetDAO.getStreets());
        return "streetsedit";
    }

    @PostMapping("/streetsedit")
    public String setStreets(@ModelAttribute("sPodstation") Podstation sPodstation,
                             @ModelAttribute(value = "action") String action,
                             @RequestParam(value = "trans", required = false) String transNum,
                             HttpServletRequest request,
                             Model model) {
        if (action.equals("backfromstreetsedit")) {
            model.addAttribute("rightsMessage", hostService.getRightsMessage(request.getRemoteAddr(), mds.getsPodstation().getResNum()));
            model.addAttribute(mds);
            model.addAttribute("sPodstation", mds.getsPodstation());
            return "editpodstation";
        }
        System.out.println(transNum);
        return null;
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
            List<HouseSegment> houseSegmentList = new ArrayList<>();
            if (street.length() > 0) {
                String[] streetParams = street.split(", ");
                if ((houseNum.length() == 0) || (houseNum.equals("0"))) {
                    houseSegmentList = streetDAO.getHouseSegmentByStreet(streetParams[0], streetParams[1]);
                } else {
                    houseSegmentList = streetDAO.getHouseSegmentByStreetAndNum(streetParams[0], streetParams[1], houseNum);
                    model.addAttribute("selectedStreet", streetParams[0]);
                }
            }
            model.addAttribute("streets", streetDAO.getStreets());
            model.addAttribute(houseSegmentList);
            model.addAttribute(mds);
        }
        return "streets";
    }

    @PostMapping("/editvalues")
    public String editPodstationValues(@ModelAttribute("sPodstation") Podstation sPodstation,
                                       Model model,
                                       HttpServletRequest request) {
        String ipAddr = request.getRemoteAddr();
        if (hostService.checkRights(ipAddr, mds.getsPodstation().getResNum())) {
            podstationDAO.updatePodstationValues(sPodstation);
            model.addAttribute("rightsMessage", hostService.getRightsMessage(ipAddr, mds.getsPodstation().getResNum()));
        } else {
            model.addAttribute("rightsMessage", hostService.getRightsMessage(ipAddr, mds.getsPodstation().getResNum()) + ". Данные не сохранены.");

        }
        mds.setsPodstation(podstationDAO.getPodstation(String.valueOf(sPodstation.getRn())));
        model.addAttribute(mds);
        model.addAttribute("sPodstation", mds.getsPodstation());
        return "editpodstationvalues";
    }

    @PostMapping("/edit")
    public String editPodstation(@ModelAttribute("sPodstation") Podstation sPodstation,
                                 @RequestParam(value = "action") String action,
                                 HttpServletRequest request,
                                 Model model) {
        if (hostService.checkRights(request.getRemoteAddr(), mds.getsPodstation().getResNum())) {
            model.addAttribute("rightsMessage", hostService.getRightsMessage(request.getRemoteAddr(), mds.getsPodstation().getResNum()));
            if (action.equals("save")) {
                podstationDAO.updatePodstation(sPodstation);
            } else if (action.equals("streetsedit")) {
                if (sPodstation.getTrCount() > 0) {
                    model.addAttribute("houseSegments", streetDAO.getHouseSegmentsByTr(sPodstation.getPodstType() + sPodstation.getNumStr(), sPodstation.getTrList().get(0).getNum()));
                    model.addAttribute("selectedTransformator", sPodstation.getTrList().get(0).getNum());
                }
                model.addAttribute("sPodstation", sPodstation);
                model.addAttribute("streets", streetDAO.getStreets());
                return "streetsedit";
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
        } else {
            model.addAttribute("rightsMessage", hostService.getRightsMessage(request.getRemoteAddr(), mds.getsPodstation().getResNum()) + ". Действие запрещено.");
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
                                 HttpServletRequest request,
                                 Model model) {
        String ipAdr = request.getRemoteAddr();
        //если подстанция выбрана из списка, или выбран другой период или введен номер и нажата кнопка просмотр
        if ((action == null) || (action.equals("find")) || (action.equals("view"))) {
            //если изменился период
            if (!period.equals(mds.getCurrentDate())) {
                mds.setCurrentDate(period);
                mds.setsPodstation(mds.refreshMdsValues(podstationNumFromInput, podstTypeForm));
                model.addAttribute(mds);
                model.addAttribute("rightsMessage", hostService.getRightsMessage(ipAdr, mds.getsPodstation().getResNum()));
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
                model.addAttribute("rightsMessage", hostService.getRightsMessage(ipAdr, mds.getsPodstation().getResNum()));
                model.addAttribute("sPodstation", mds.getsPodstation());
                return mds.getActivityView(currentActivity);
            }
            //если нажата кнопка просмотр
            if (action.equals("view")) {
                currentActivity = "show";
                mds.setCurrentActivity(currentActivity);
            }
            //если номер подстанции введен вручную
            if (podstationDAO.isPodstationExist(podstTypeForm, podstationNumFromInput, mds.getCurrentDate()) != 0) {
                mds.setPodstationNum(podstationNumFromInput);
                mds.setPodstType(podstTypeForm);
                String podstationRn = podstationDAO.getPodstationRn(mds.getPodstType(), podstationNumFromInput, mds.getCurrentDate());
                mds.setCurrentPodstation(podstationRn);
                mds.setsPodstation(podstationDAO.getPodstation(mds.getCurrentPodstation()));

            } else {
                model.addAttribute("error", "Подстанция " + podstTypeForm + "-" + podstationNumFromInput + " не найдена");
            }
            model.addAttribute("rightsMessage", hostService.getRightsMessage(ipAdr, mds.getsPodstation().getResNum()));
            model.addAttribute(mds);
            model.addAttribute("sPodstation", mds.getsPodstation());
            return mds.getActivityView(currentActivity);
        }
        if (action.equals("editvalues")) {
            mds.setCurrentActivity("values");
            mds.setsPodstation(podstationDAO.getPodstation(mds.getCurrentPodstation()));
            model.addAttribute(mds);
            model.addAttribute("rightsMessage", hostService.getRightsMessage(ipAdr, mds.getsPodstation().getResNum()));
            model.addAttribute("sPodstation", mds.getsPodstation());
            return "editpodstationvalues";
        }
        if (action.equals("editpodstation")) {
            mds.setCurrentActivity("edit");
            mds.setsPodstation(podstationDAO.getPodstation(mds.getCurrentPodstation()));
            model.addAttribute(mds);
            model.addAttribute("rightsMessage", hostService.getRightsMessage(ipAdr, mds.getsPodstation().getResNum()));
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