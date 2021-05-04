package ru.donenergo.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.donenergo.journal.dao.PodstationDAO;
import ru.donenergo.journal.dao.ReportsDAO;
import ru.donenergo.journal.dao.StreetDAO;
import ru.donenergo.journal.models.HouseSegment;
import ru.donenergo.journal.models.Podstation;
import ru.donenergo.journal.reports.MeasureBlankReport;
import ru.donenergo.journal.services.HostService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {
    private final PodstationDAO podstationDAO;
    private final StreetDAO streetDAO;
    private final ReportsDAO reportsDAO;
    private final HostService hostService;
    private Mds mds;


    @Autowired
    public MainController(PodstationDAO podstationDAO, StreetDAO streetDAO, ReportsDAO reportsDAO, HostService hostService, Mds mds) {
        this.podstationDAO = podstationDAO;
        this.streetDAO = streetDAO;
        this.reportsDAO = reportsDAO;
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
        String ipAddress = request.getRemoteAddr();
        mds.setResName(hostService.getUserResName(ipAddress));
        if (!hostService.rightsExist(ipAddress)) {
            hostService.addRO(ipAddress);
            hostService.updateHosts();
        }
        model.addAttribute("sPodstation", mds.refreshMdsValues("norn", "notype"));
        mds.setCurrentActivity("show");
        model.addAttribute(mds);
        return "show";
    }

    ///v2
    @GetMapping("/savepodstation")
    public String savePodstation(@RequestParam(value = "podsttype") String podstType,
                                 @RequestParam(value = "num") String num,
                                 @RequestParam(value = "address") String address,
                                 HttpServletRequest request,
                                 Model model) {
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
        return "editpodstation";
    }

    ///v2
    @GetMapping("/v2streetsedit")
    public String getStreetsEdit(@RequestParam(value = "street", required = false) String street,
                                 @RequestParam(value = "action", required = false) String action,
                                 @RequestParam(value = "trans", required = false) Integer trans,
                                 @RequestParam(value = "delete", required = false) String delete,
                                 @RequestParam(value = "housenum1", required = false) Integer housenum1,
                                 @RequestParam(value = "housenum2", required = false) Integer housenum2,
                                 @RequestParam(value = "letter1", required = false) String letter1,
                                 @RequestParam(value = "letter2", required = false) String letter2,
                                 HttpServletRequest request,
                                 Model model) {
        String success = new String();
        if (action != null) {
            if (housenum2 == null) {
                housenum2 = housenum1;
                success = "Адрес добавлен";
            }
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
                if (success.length() == 0) {
                    success = "Сегмент добавлен";
                }
                model.addAttribute("success", success);
            } else {
                model.addAttribute("error", "Ошибка добавления сегмента. Заполните обязательные поля.");
            }
        }
        if (delete != null) {
            streetDAO.deleteHouseSegment(delete);
        }
        model.addAttribute("rightsMessage", hostService.getRightsMessage(request.getRemoteAddr(), mds.getsPodstation().getResNum()));
        model.addAttribute(mds);
        model.addAttribute("sPodstation", mds.getsPodstation());
        model.addAttribute("houseSegments", streetDAO.getHouseSegmentsByTr(mds.getsPodstation().getPodstType() + mds.getsPodstation().getNumStr(), Integer.valueOf(trans)));
        model.addAttribute("selectedTransformator", trans);
        model.addAttribute("streets", streetDAO.getStreets());
        return "streetsedit";
    }

    ///v2
    @GetMapping("/v2streets")
    public String getStreetsv2(@RequestParam(value = "street") String street,
                               @RequestParam(value = "housenum") String houseNum,
                               @RequestParam(value = "letter") String letter,
                               Model model) {
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
        model.addAttribute("sPodstation", mds.getsPodstation());
        return "streetsshow";
    }

    ///v2
    @PostMapping("/editvalues")
    public String editPodstationValues(@ModelAttribute("sPodstation") Podstation sPodstation,
                                       Model model,
                                       HttpServletRequest request) {
        String ipAddr = request.getRemoteAddr();
        if (hostService.checkRights(ipAddr, mds.getsPodstation().getResNum())) {
            podstationDAO.updatePodstationValues(sPodstation, false);
            model.addAttribute("rightsMessage", hostService.getRightsMessage(ipAddr, mds.getsPodstation().getResNum()));
            model.addAttribute("successMessage", "Подстанция сохранена.");
        } else {
            model.addAttribute("rightsMessage", hostService.getRightsMessage(ipAddr, mds.getsPodstation().getResNum()) + ". Данные не сохранены.");
        }
        mds.setsPodstation(podstationDAO.getPodstation(String.valueOf(sPodstation.getRn())));
        model.addAttribute(mds);
        model.addAttribute("sPodstation", mds.getsPodstation());
        return "editvalues";
    }

    @GetMapping("/reportall")
    public String getReportAll(Model model) {
        model.addAttribute("allReportList", reportsDAO.getReportAllPodstations(mds.getCurrentDate()));
        return "reportall";
    }

    ///v2
    @GetMapping("/measureblank")
    public String getMeasureBlank(Model model) {
        model.addAttribute(mds);
        model.addAttribute("mbr", MeasureBlankReport.getBlankReportLines(mds.getsPodstation()));
        model.addAttribute("sPodstation", mds.getsPodstation());
        if (mds.getsPodstation().getTrList().size() == 2) {
            return "measureblankdouble";
        } else if (mds.getsPodstation().getTrList().size() == 1) {
            return "measureblanksingle";
        } else {
            return null;
        }
    }

    ///v2
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
                    mds.setCurrentActivity("streetsedit");
                    model.addAttribute("houseSegments", streetDAO.getHouseSegmentsByTr(sPodstation.getPodstType() + sPodstation.getNumStr(), sPodstation.getTrList().get(0).getNum()));
                    model.addAttribute("selectedTransformator", sPodstation.getTrList().get(0).getNum());
                }
                model.addAttribute(mds);
                model.addAttribute("sPodstation", sPodstation);
                model.addAttribute("streets", streetDAO.getStreets());
                return "streetsedit";
            } else {
                String[] targetValues = action.split("&");
                if (targetValues[0].equals("trans")) {
                    if (targetValues[1].equals("add")) {
                        podstationDAO.addTransformator(targetValues[2], sPodstation.getTrCount() + 1, false);
                    }
                    if (targetValues[1].equals("del")) {
                        podstationDAO.deleteTrans(targetValues[2], false);
                    }
                }
                if (targetValues[0].equals("line")) {
                    if (targetValues[1].equals("add")) {
                        String[] addLineParams = targetValues[2].split("-");
                        int linesCount = Integer.valueOf(addLineParams[1]);
                        addLineParams[1] = String.valueOf(Integer.valueOf(linesCount + 1));
                        podstationDAO.addLine(addLineParams[0], addLineParams[1], false);
                    }
                    if (targetValues[1].equals("del")) {
                        podstationDAO.deleteLine(targetValues[2], false);
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

    ///v2
    @GetMapping("/switchstreetsshow")
    public String switchStreets(Model model) {
        model.addAttribute("streets", streetDAO.getStreets());
        mds.setCurrentActivity("streetsshow");
        model.addAttribute("houseSegmentList", streetDAO.getHouseSegmentsTp(mds.getsPodstation().getPodstType() + mds.getsPodstation().getNumStr()));
        model.addAttribute(mds);
        model.addAttribute("sPodstation", mds.getsPodstation());
        return "streetsshow";
    }

    ///v2
    @GetMapping("/switchshow")
    public String switchShow(Model model) {
        mds.setCurrentActivity("show");
        model.addAttribute(mds);
        model.addAttribute("sPodstation", mds.getsPodstation());
        return "show";
    }

    @GetMapping("/overload")
    public String overloadPodstations(Model model) {
        model.addAttribute("overloadtable", reportsDAO.getOverloadedPodstations(mds.getCurrentDate()));
        return "overload";
    }

    ///v2
    @GetMapping("/switcheditvalues")
    public String switchEditValues(Model model,
                                   HttpServletRequest request) {
        mds.setCurrentActivity("values");
        model.addAttribute(mds);
        model.addAttribute("rightsMessage", hostService.getRightsMessage(request.getRemoteAddr(), mds.getsPodstation().getResNum()));
        model.addAttribute("sPodstation", mds.getsPodstation());
        return "editvalues";
    }

    ///v2
    @GetMapping("/switcheditpodstation")
    public String switchEditPodstation(Model model,
                                       HttpServletRequest request) {
        mds.setCurrentActivity("edit");
        model.addAttribute(mds);
        model.addAttribute("rightsMessage", hostService.getRightsMessage(request.getRemoteAddr(), mds.getsPodstation().getResNum()));
        model.addAttribute("sPodstation", mds.getsPodstation());
        return "editpodstation";
    }

    ///v2
    @GetMapping("/v2show")
    public String showPodstation2(@RequestParam(value = "period", required = false) String period,
                                  @RequestParam(value = "podstation", required = false) String podstationRnFromSelect,
                                  @RequestParam(value = "podstationNum", required = false) String podstationNumFromInput,
                                  @RequestParam(value = "podstType", required = false) String podstTypeForm,
                                  @RequestParam(value = "action", required = false) String action,
                                  @RequestParam(value = "currentActivity", required = false) String currentActivity,
                                  HttpServletRequest request,
                                  Model model) {
        System.out.println("period: " + period + " psRnSel: " + podstationRnFromSelect + " pNum: " + podstationNumFromInput + " pType: " + podstTypeForm +
                " cAct: " + currentActivity + " action: " + action);
        String ipAdr = request.getRemoteAddr();
        boolean actionPerformed = false;
        //если изменился период
        if (!period.equals(mds.getCurrentDate()) && !actionPerformed) {
            mds.setCurrentDate(period);
            mds.setsPodstation(mds.refreshMdsValues(mds.getsPodstation().getNumStr(), mds.getsPodstation().getPodstType()));
            actionPerformed = true;
        }
        //если выбрана подстанция из списка
        if (!podstationRnFromSelect.equals(mds.getCurrentPodstation()) && !actionPerformed) {
            mds.setCurrentPodstation(podstationRnFromSelect);
            for (Podstation p : mds.getPodstations()) {
                if (String.valueOf(p.getRn()).equals(mds.getCurrentPodstation())) {
                    mds.setPodstationNum(p.getNumStr());
                }
            }
            mds.setsPodstation(podstationDAO.getPodstation(mds.getCurrentPodstation()));
            podstationNumFromInput = mds.getsPodstation().getNumStr();
            podstTypeForm = mds.getsPodstation().getPodstType();
            actionPerformed = true;
        }
        //если номер подстанции введен вручную
        if (podstationNumFromInput.length() > 0) {
            if ((!mds.getsPodstation().getNumStr().equals(podstationNumFromInput) ||
                    !mds.getsPodstation().getPodstType().equals(podstTypeForm)) && !actionPerformed) {
                if (podstationDAO.isPodstationExist(podstTypeForm, podstationNumFromInput, mds.getCurrentDate()) != 0) {
                    mds.setPodstationNum(podstationNumFromInput);
                    mds.setPodstType(podstTypeForm);
                    String podstationRn = podstationDAO.getPodstationRn(mds.getPodstType(), podstationNumFromInput, mds.getCurrentDate());
                    mds.setCurrentPodstation(podstationRn);
                    mds.setsPodstation(podstationDAO.getPodstation(mds.getCurrentPodstation()));
                } else {
                    model.addAttribute("error", "Подстанция " + podstTypeForm + "-" + podstationNumFromInput + " не найдена");
                }
                actionPerformed = true;
            }
        }
        if (currentActivity.equals("streetsshow")) {
            model.addAttribute("streets", streetDAO.getStreets());
            model.addAttribute("houseSegmentList", streetDAO.getHouseSegmentsTp(mds.getsPodstation().getPodstType() + mds.getsPodstation().getNumStr()));
        }
        if (currentActivity.equals("streetsedit")) {
            model.addAttribute("houseSegments", streetDAO.getHouseSegmentsByTr(mds.getsPodstation().getPodstType() + mds.getsPodstation().getNumStr(), mds.getsPodstation().getTrList().get(0).getNum()));
            model.addAttribute("selectedTransformator", mds.getsPodstation().getTrList().get(0).getNum());
        }
        model.addAttribute("rightsMessage", hostService.getRightsMessage(ipAdr, mds.getsPodstation().getResNum()));
        model.addAttribute(mds);
        model.addAttribute("sPodstation", mds.getsPodstation());
        System.out.println("rn: " + mds.getCurrentPodstation() + " num: " + mds.getsPodstation().getNum());
        return mds.getActivityView(currentActivity);
    }
}