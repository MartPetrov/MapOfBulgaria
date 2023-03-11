package project.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import project.service.CityService;
import project.service.RoadService;


@Controller
@RequestMapping("/export")
public class ExportController extends BaseController {

    private final CityService cityService;
    private final RoadService roadService;

    @Autowired
    public ExportController(CityService cityService, RoadService roadService) {
        this.cityService = cityService;
        this.roadService = roadService;
    }


    @GetMapping("/cities")
    public ModelAndView exportCities() {
        String cities = this.cityService.exportCity();
        return super.view("export/cities.html", "cities", cities);
    }

    @GetMapping("/roads")
    public ModelAndView exportRoads() {
        String roads = this.roadService.exportRoads();
        return super.view("export/roads.html", "roads", roads);
    }
}
