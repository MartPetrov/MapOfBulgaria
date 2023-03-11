package project.service.impl;

import org.springframework.stereotype.Service;
import project.entity.City;
import project.entity.Road;
import project.entity.SubRoad;
import project.repositories.SubRoadRepository;
import project.service.SubRoadService;

import java.util.List;

@Service
public class SubRoadServiceImpl implements SubRoadService {

    private final SubRoadRepository subRoadRepository;

    public SubRoadServiceImpl(SubRoadRepository subRoadRepository) {
        this.subRoadRepository = subRoadRepository;
    }


    @Override
    public String importSubRoad(Road road) {
        List<City> cities = road.getCities();
        String[] distanceFromPrevCity = road.getDistanceFromPrevCity().split(",");
        StringBuilder sb = new StringBuilder();
        return getAllSubRoadFromRoadAndImportInDataBase(road, cities, distanceFromPrevCity, sb);
    }

    private String getAllSubRoadFromRoadAndImportInDataBase(Road road, List<City> cities, String[] distanceFromPrevCity, StringBuilder sb) {
        if (cities.size() > 2) {
            for (int i = 1; i <= cities.size() - 1; i++) {
                City firstCity = cities.get(i - 1);
                City secondCity = cities.get(i);
                SubRoad subRoad = new SubRoad();
                subRoad.setFirstCity(firstCity.name);
                subRoad.setSecondCity(secondCity.name);
                subRoad.setSpeed(road.getSpeed());
                subRoad.setNumber(road.getNumber());
                subRoad.setDistance(Double.parseDouble(distanceFromPrevCity[i]));
                this.subRoadRepository.save(subRoad);
                SubRoad subRoadMirror = new SubRoad();
                subRoadMirror.setFirstCity(secondCity.name);
                subRoadMirror.setSecondCity(firstCity.name);
                subRoadMirror.setNumber(road.getNumber());
                subRoadMirror.setSpeed(road.getSpeed());
                subRoadMirror.setDistance(Double.parseDouble(distanceFromPrevCity[i]));
                this.subRoadRepository.save(subRoadMirror);
            }
        } else {
            City firstCity = cities.get(0);
            City secondCity = cities.get(1);
            SubRoad subRoad = new SubRoad();
            subRoad.setFirstCity(firstCity.name);
            subRoad.setSecondCity(secondCity.name);
            subRoad.setSpeed(road.getSpeed());
            subRoad.setNumber(road.getNumber());
            subRoad.setDistance(Double.parseDouble(distanceFromPrevCity[1]));
            this.subRoadRepository.save(subRoad);
            SubRoad subRoadMirror = new SubRoad();
            subRoadMirror.setFirstCity(secondCity.name);
            subRoadMirror.setSecondCity(firstCity.name);
            subRoadMirror.setNumber(road.getNumber());
            subRoadMirror.setSpeed(road.getSpeed());
            subRoadMirror.setDistance(Double.parseDouble(distanceFromPrevCity[1]));
            this.subRoadRepository.save(subRoadMirror);
        }
        return "Successfully imported SubRoad";
    }
}
