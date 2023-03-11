package project.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.entity.City;
import project.entity.DTO.ImportRoadRoot;
import project.entity.DTO.RoadDTO;
import project.entity.Road;
import project.repositories.CityRepository;
import project.repositories.RoadRepository;
import project.service.RoadService;
import project.service.SubRoadService;
import project.util.ValidationUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static project.constans.Messages.INVALID_ROAD;
import static project.constans.Messages.VALID_ROAD_FORMAT;
import static project.constans.Paths.XML_ROADS_FILE;

@Service
public class RoadServiceImpl implements RoadService {

    private final ModelMapper modelMapper;

    private final RoadRepository roadRepository;

    private final CityRepository cityRepository;

    private final Unmarshaller unmarshaller;

    private final ValidationUtils validationUtils;

    private final SubRoadService subRoadService;


    @Autowired
    public RoadServiceImpl(
            RoadRepository roadRepository,
            ValidationUtils validationUtils,
            ModelMapper modelMapper,
            CityRepository cityRepository,SubRoadService subRoadService) throws JAXBException {
        this.roadRepository = roadRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
this.subRoadService = subRoadService;

        JAXBContext context = JAXBContext.newInstance(ImportRoadRoot.class);
        this.unmarshaller = context.createUnmarshaller();
        this.cityRepository = cityRepository;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString(XML_ROADS_FILE);

    }

    @Override
    public String importRoads() throws IOException, JAXBException {
        if (this.roadRepository.findAll().isEmpty()) {
            ImportRoadRoot roadsDTOs = (ImportRoadRoot) this.unmarshaller.unmarshal(new FileReader(XML_ROADS_FILE.toFile()));
            return roadsDTOs.getRoads().stream().map(this::importRoad).collect(Collectors.joining("\n"));
        }
        return "Roads are imported";
    }

    @Override
    public String importRoad(RoadDTO importRoad) {

        boolean isValid = this.validationUtils.isValid(importRoad);
        String result = "";
        if (!isValid) {
            result = INVALID_ROAD;
        } else {
            Optional<Road> optRoad = this.roadRepository.findByNumber(importRoad.getNumber());
            result = splitAndImportData(importRoad, optRoad);
        }
        return result;
    }

    private String splitAndImportData(RoadDTO importRoad, Optional<Road> optRoad) {
        String result;
        if (optRoad.isEmpty()) {
            Road road = this.modelMapper.map(importRoad, Road.class);
            List<String> citiesAsString = Arrays.stream(importRoad.getCities().split(",")).collect(Collectors.toList());
            List<City> cities = new LinkedList<>();
            for (String city : citiesAsString) {
                String[] splitCity = city.split(";");
                Optional<City> currentCity = this.cityRepository.findByName(splitCity[0]);
                if (currentCity.isEmpty()) {
                    City currentCityFromRepo = currentCity.get();
                    cities.add(currentCityFromRepo);
                } else {
                    cities.add(currentCity.get());
                }
                String distanceFromPrevCity = splitCity[1];
                if (road.getDistanceFromPrevCity() == null) {
                    road.setDistanceFromPrevCity(distanceFromPrevCity);
                } else {
                    road.setDistanceFromPrevCity(road.getDistanceFromPrevCity() + "," + distanceFromPrevCity);
                }
            }
            road.setCities(cities);
            this.roadRepository.save(road);
            result = String.format(VALID_ROAD_FORMAT, road.getNumber());
            this.subRoadService.importSubRoad(road);
        } else {
            result = INVALID_ROAD;
        }
        return result;
    }

    @Override
    public boolean areImported() {
        return this.roadRepository.count() > 0;
    }

    @Override
    public String exportRoads() {
        List<Road> all = this.roadRepository.findAll();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < all.size(); i++) {
            sb.append(all.get(i).toString()).append(System.lineSeparator());
        }
        return sb.toString().trim();
    }
}
