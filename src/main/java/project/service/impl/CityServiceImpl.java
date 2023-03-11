package project.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import project.entity.City;
import project.entity.DTO.CityDTO;
import project.repositories.CityRepository;
import project.service.CityService;
import project.util.ValidationUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static project.constans.Messages.INVALID_CITY;
import static project.constans.Messages.VALID_CITY_FORMAT;
import static project.constans.Paths.JSON_CITIES_FILE;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    private final ValidationUtils validationUtils;
    private final Gson gson;
    private final ModelMapper modelMapper;


    public CityServiceImpl(CityRepository cityRepository,
                           ValidationUtils validationUtils,
                           Gson gson, ModelMapper modelMapper) {
        this.cityRepository = cityRepository;
        this.validationUtils = validationUtils;
        this.gson = gson;
        this.modelMapper = modelMapper;
    }

    @Override
    public String readCitiesFileContent() throws IOException {
        return Files.readString(JSON_CITIES_FILE);
    }

    @Override
    public String importCities() throws IOException {
        String json = this.readCitiesFileContent();
        CityDTO[] importCitiesDTOS = this.gson.fromJson(json, CityDTO[].class);
        return Arrays.stream(importCitiesDTOS).map(this::importCity).collect(Collectors.joining("\n"
        ));
    }

    @Override
    public String importCity(CityDTO importCityDTO) {
        boolean isValid = this.validationUtils.isValid(importCityDTO);
        return checkAndImportCity(importCityDTO, isValid);
    }

    private String checkAndImportCity(CityDTO importCityDTO, boolean isValid) {
        String result = "";
        if (!isValid) {
            result = INVALID_CITY;
        } else {
            City city = this.modelMapper.map(importCityDTO, City.class);
            this.cityRepository.save(city);
            result = String.format(VALID_CITY_FORMAT, city.getName(), city.getPopulation());
        }
        System.out.println(result);
        return result;
    }

    @Override
    public String exportCity() {
        List<City> all = this.cityRepository.findAll();
        StringBuilder sb = new StringBuilder();
        for (City city : all) {
            sb.append(city.toString()).append(System.lineSeparator());
        }
        return sb.toString().trim();
    }


    @Override
    public boolean areImported() {
        return this.cityRepository.count() > 0;
    }

}
