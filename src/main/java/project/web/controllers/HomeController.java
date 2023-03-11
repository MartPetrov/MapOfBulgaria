package project.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import project.service.CityService;
import project.service.ForecastService;
import project.service.RoadService;

@Controller
public class HomeController extends BaseController {

    private final CityService cityService;
    private final ForecastService forecastService;
    private final RoadService roadService;

    @Autowired
    public HomeController(CityService cityService, ForecastService forecastService, RoadService roadService) {
        this.cityService = cityService;
        this.forecastService = forecastService;
        this.roadService = roadService;
    }


    @GetMapping("/")
    @ResponseBody
    public ModelAndView index() {
        boolean areImported = this.cityService.areImported() &&
                this.roadService.areImported() &&
                this.forecastService.areImported();
        return super.view("index", "areImported", areImported);
    }
}
