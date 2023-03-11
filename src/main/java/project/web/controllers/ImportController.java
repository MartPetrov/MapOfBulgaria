package project.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import project.service.CityService;
import project.service.ForecastService;
import project.service.RoadService;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@Controller
@RequestMapping("/import")
public class ImportController extends BaseController {
    private final CityService cityService;
    private final ForecastService forecastService;
    private final RoadService roadService;

    @Autowired
    public ImportController(CityService cityService, ForecastService forecastService, RoadService roadService) {
        this.cityService = cityService;
        this.forecastService = forecastService;
        this.roadService = roadService;
    }


    @GetMapping("json")
    public ModelAndView importJson() {
        boolean[] areImported = new boolean[]{
                this.cityService.areImported()
        };
        return super.view("json/import-json", "areImported", areImported);
    }

    @GetMapping("/xml")
    public ModelAndView importXml() {
        boolean[] areImported = new boolean[]{
                this.roadService.areImported(),
                this.forecastService.areImported()
        };
        return super.view("xml/import-xml", "areImported", areImported);
    }
    @GetMapping("/cities")
    public ModelAndView importCities() throws IOException {
        String fileContent = this.cityService.readCitiesFileContent();
        return super.view("json/import-cities", "cities", fileContent);
    }
    @PostMapping("/cities")
    public ModelAndView importCitiesConfirm() throws IOException, JAXBException {
        System.out.println(this.cityService.importCities());
        return super.redirect("/import/json");
    }

    @GetMapping("/forecasts")
    public ModelAndView importForecasts() throws IOException, JAXBException {
        String apartmentsXmlFileContent = this.forecastService.readForecastsFromFile();
        return super.view("xml/import-forecasts", "forecasts", apartmentsXmlFileContent);
    }

    @PostMapping("/forecasts")
    public ModelAndView importForecastsConfirm() throws JAXBException, IOException {
        System.out.println(this.forecastService.importForecasts());
        return super.redirect("/import/xml");
    }


    @GetMapping("/roads")
    public ModelAndView importRoads() throws IOException {
        String apartmentsXmlFileContent = this.roadService.readForecastsFromFile();
        return super.view("xml/import-roads", "roads", apartmentsXmlFileContent);
    }

    @PostMapping("/roads")
    public ModelAndView importRoadsConfirm() throws IOException, JAXBException {
        System.out.println(this.roadService.importRoads());
        return super.redirect("/import/xml");
    }

}
