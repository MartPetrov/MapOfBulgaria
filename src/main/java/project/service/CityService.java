package project.service;

import project.entity.DTO.CityDTO;

import java.io.IOException;

public interface CityService {

    boolean areImported();
    String readCitiesFileContent() throws IOException;

    String importCities() throws IOException;

    String importCity(CityDTO importCityDTO);

    String exportCity();

}

