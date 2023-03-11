package project.service;

import project.entity.City;

public interface PathFinderService {
    String findBestRoadOnKmParam(String startcity, String endCity);


    Double findAirDistanceBetweenTwoCities(City startcity, City endCity);


    String findBestRoadWithA_StarAlgorithm(String startCity, String endCity);
}
