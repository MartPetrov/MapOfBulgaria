package project.web.restControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import project.service.PathFinderService;

@RestController
public class FindBestRoadController {

    private PathFinderService pathFinderService;

    @Autowired
    public FindBestRoadController(PathFinderService pathFinderService) {
        this.pathFinderService = pathFinderService;
    }

    @RequestMapping(value = "/findBestRoad",params = {"startCity","endCity"})
    public @ResponseBody String findBestRoad(@RequestParam(value = "startCity") String startCity, @RequestParam(value = "endCity") String endCity) {
        return this.pathFinderService.findBestRoadOnKmParam(startCity, endCity);
    }
    //           http://localhost:8080/findBestRoad?startCity=Sofia&endCity=Burgas


    @RequestMapping(value = "/findBestRoadWithAStar",params = {"startCity","endCity"})
    public @ResponseBody String findBestRoadWithAStar(@RequestParam(value = "startCity") String startCity, @RequestParam(value = "endCity") String endCity) {
        return this.pathFinderService.findBestRoadWithA_StarAlgorithm(startCity, endCity);
    }
    //           http://localhost:8080/findBestRoadWithAStar?startCity=Sofia&endCity=Burgas

}
