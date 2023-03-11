package project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.entity.*;
import project.repositories.CityRepository;
import project.repositories.ForecastRepository;
import project.repositories.RoadRepository;
import project.repositories.SubRoadRepository;
import project.service.PathFinderService;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PathFinderServiceImpl implements PathFinderService {
    private final ForecastRepository forecastRepository;
    private final CityRepository cityRepository;
    private final RoadRepository roadRepository;

    private final SubRoadRepository subRoadRepository;

    private final List<City> visitedCity;


    @Autowired
    public PathFinderServiceImpl(
            ForecastRepository forecastRepository,
            CityRepository cityRepository,
            RoadRepository roadRepository, SubRoadRepository subRoadRepository) {
        this.forecastRepository = forecastRepository;
        this.cityRepository = cityRepository;
        this.roadRepository = roadRepository;
        this.subRoadRepository = subRoadRepository;
        this.visitedCity = new LinkedList<>();
    }


    @Override
    public String findBestRoadOnKmParam(String startCity, String endCity) {

        checkIsStartCityAndEndCityAreTheSame(startCity, endCity);


        Optional<City> startCityFromRepo = this.cityRepository.findByName(startCity);
        Optional<City> endCityFromRepo = this.cityRepository.findByName(endCity);


        if (startCityFromRepo.isPresent() && endCityFromRepo.isPresent()) {
            if (visitedCity.isEmpty()) {
                visitedCity.add(startCityFromRepo.get());
            }
            City startCityEntity = startCityFromRepo.get();
            City endCityEntity = endCityFromRepo.get();
            Road directRoad = checkIsStartCityAndEndCityAreInTheSameRoad(startCityEntity, endCityEntity);
            if (directRoad != null) {
                findAllCitiesFromDirectRoad(startCityEntity, endCityEntity, directRoad);
            } else {
                findBestSubRoadWhenDirectRoadIsNull(endCity, startCityEntity, endCityEntity);
            }
        } else {
            throw new IllegalArgumentException("One of two cities is not present in the base");
        }

        StringBuilder sb = new StringBuilder();
        return printAllVisitedCities(sb);

    }

    private void findBestSubRoadWhenDirectRoadIsNull(String endCity, City startCityEntity, City endCityEntity) {
        List<SubRoad> subRoadFromStartCity = this.subRoadRepository.findByFirstCity(startCityEntity.getName());
        SubRoad nextRoad = null;
        Double minDistance = Double.MAX_VALUE;
        for (SubRoad currentSubRoad : subRoadFromStartCity) {
            Double airDistanceBetweenTwoCities = findAirDistanceBetweenTwoCities(this.cityRepository.findByName(currentSubRoad.getSecondCity()).get(), endCityEntity);
            if (airDistanceBetweenTwoCities < minDistance) {
                minDistance = airDistanceBetweenTwoCities;
                nextRoad = currentSubRoad;
            }
        }
        Optional<City> byName = this.cityRepository.findByName(nextRoad.getSecondCity());
        if (byName.isPresent()) {
            City nextCity = byName.get();
            visitedCity.add(nextCity);

            if (!nextCity.getName().equals(endCity)) {
                findBestRoadOnKmParam(nextCity.getName(), endCity);
            }
        }
    }

    private void findAllCitiesFromDirectRoad(City startCityEntity, City endCityEntity, Road road1) {
        Optional<SubRoad> directSubRoad = this.subRoadRepository.findSubRoadsByFirstCityAndSecondCityContains(startCityEntity.getName(), endCityEntity.getName());
        if (directSubRoad.isPresent()) {
            visitedCity.add(endCityEntity);
            return;
        }
        int indexOfMin = Math.min(road1.getCities().indexOf(startCityEntity), road1.getCities().indexOf(endCityEntity));
        int indexOfMax = Math.max(road1.getCities().indexOf(startCityEntity), road1.getCities().indexOf(endCityEntity));
        List<City> cities = road1.getCities().subList(indexOfMin + 1, indexOfMax + 1);
        if (indexOfMax < indexOfMin) {
            Collections.reverse(cities);
            visitedCity.addAll(cities);
        } else {
            visitedCity.addAll(cities);
        }

        if (!visitedCity.contains(startCityEntity)) {
            visitedCity.add(startCityEntity);
        }
    }

    private Road checkIsStartCityAndEndCityAreInTheSameRoad(City startCity, City endCity) {
        Comparator<Road> bySpeed = (o1, o2) -> (o1.getSpeed() >= o2.getSpeed()) ? -1 : 1;
        List<Road> collect = this.roadRepository.findAll().stream()
                .filter(road -> new HashSet<>(road.getCities())
                        .containsAll(List.of(startCity, endCity)))
                .sorted(bySpeed).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return null;
        }
        return collect.get(0);
    }

    private String printAllVisitedCities(StringBuilder sb, List<Node> visitedCity) {
        for (int i = 0; i < visitedCity.size(); i++) {
            Node current = visitedCity.get(i);
            Optional<Forecast> forecast = this.forecastRepository.getFirstByCityAndDayOfWeek(current.getCurrentCity(), DayOfWeek.SUNDAY);
            if (forecast.isPresent()) {
                Forecast currentForecasts = forecast.get();
                if (i == visitedCity.size() - 1) {
                    sb.append(current).append(System.lineSeparator()).append(" With weather: ").append(System.lineSeparator());
                    sb.append(currentForecasts).append(System.lineSeparator());
                } else {
                    sb.append(current).append(System.lineSeparator()).append(" With weather: ").append(System.lineSeparator());
                    sb.append(currentForecasts)
                            .append(System.lineSeparator())
                            .append(" ---> ");
                }
            } else {
                sb.append("For City ").append(current).append(" and this day ").append("don't have any forecasts");
            }
        }
        return sb.toString();
    }

    private String printAllVisitedCities(StringBuilder sb) {
        for (int i = 0; i < visitedCity.size(); i++) {
            City current = visitedCity.get(i);
            Optional<Forecast> forecast = this.forecastRepository.getFirstByCityAndDayOfWeek(current, DayOfWeek.SUNDAY);
            if (forecast.isPresent()) {
                Forecast currentForecasts = forecast.get();
                if (i == visitedCity.size() - 1) {
                    sb.append(current).append(System.lineSeparator()).append(" With weather: ").append(System.lineSeparator());
                    sb.append(currentForecasts).append(System.lineSeparator());
                } else {
                    sb.append(current).append(System.lineSeparator()).append(" With weather: ").append(System.lineSeparator());
                    sb.append(currentForecasts)
                            .append(System.lineSeparator())
                            .append(" ---> ");
                }
            } else {
                sb.append("For City ").append(current).append(" and this day ").append("don't have any forecasts");
            }
        }
        return sb.toString();
    }

    private static void checkIsStartCityAndEndCityAreTheSame(String startCity, String endCity) {
        if (startCity.equals(endCity)) {
            throw new IllegalArgumentException("Start and end city are same");
        }
    }

    @Override
    public Double findAirDistanceBetweenTwoCities(City startcity, City endCity) {
        Optional<City> startCityFromRepo = this.cityRepository.findByName(startcity.getName());
        Optional<City> endCityFromRepo = this.cityRepository.findByName(endCity.getName());
        if (startCityFromRepo.isPresent() && endCityFromRepo.isPresent()) {
            double deltaX = startCityFromRepo.get().getCoordinateX() - endCityFromRepo.get().getCoordinateX();
            double deltaY = startCityFromRepo.get().getCoordinateY() - endCityFromRepo.get().getCoordinateY();
            double result = (Math.sqrt((Math.pow(deltaX, 2) + Math.pow(deltaY, 2)))) / 1000;

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            String format = nf.format(result).replace(",", ".");
            return Double.parseDouble(format);
        }
        return null;
    }

    @Override
    public String findBestRoadWithA_StarAlgorithm(String startCity, String endCity) {
        checkIsStartCityAndEndCityAreTheSame(startCity, endCity);

        Optional<City> startCityFromRepo = this.cityRepository.findByName(startCity);
        Optional<City> endCityFromRepo = this.cityRepository.findByName(endCity);

        if (startCityFromRepo.isEmpty() || endCityFromRepo.isEmpty()) {
            throw new IllegalArgumentException("Wrong City");
        }
        Comparator<Node> comparator = new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                if (node2 == null) return 1;
                if (node1 == null) return -1;

                return (int) ((node1.getCost() + node1.getEstimateToGoal()) - (node2.getCost() + node2.getEstimateToGoal()));
            }
        };
        PriorityQueue<Node> front = new PriorityQueue<>(comparator);
        HashSet<Node> reviewed = new HashSet<>();
        Node root = new Node(startCityFromRepo.get(), 0, null, endCityFromRepo.get(), this.subRoadRepository, this.cityRepository);
        front.offer(root);

        while (!front.isEmpty()) {
            Node current = front.poll();
            this.visitedCity.add(current.getCurrentCity());
            if (reviewed.contains(current)) {
                continue;
            }
            reviewed.add(current);

            if (current.isGoal()) {
                StringBuilder sb = new StringBuilder();
                return printAllVisitedCities(sb,writeDown(current));
            }
            List<Node> children = current.getChildren();
            for (Node child : children) {
                front.offer(child);
            }
        }
        return null;
    }

    private List<Node> writeDown(Node solved) {
        List<Node> solution = new ArrayList<>();

        Node current = solved;
        while (current != null) {
            solution.add(0, current);
            current = current.getParent();
        }

        return solution;
    }

}
