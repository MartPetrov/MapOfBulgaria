package project.service;

import project.entity.DTO.RoadDTO;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface RoadService {

    String readForecastsFromFile() throws IOException;


    String importRoads() throws IOException, JAXBException;

    String importRoad(RoadDTO road) throws IOException;

    boolean areImported();

    String exportRoads();
}

