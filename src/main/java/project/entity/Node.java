package project.entity;


import lombok.Getter;
import lombok.Setter;
import project.repositories.CityRepository;
import project.repositories.SubRoadRepository;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class Node {
    public final City endCity;

    private City currentCity;

    private double cost;
    private final Node parent;

    private List<Node> children;

    private final SubRoadRepository subRoadRepository;

    private final CityRepository cityRepository;

    public Node(City state, double cost, Node parent, City endCity, SubRoadRepository subRoadRepository, CityRepository cityRepository) {
        this.currentCity = state;
        this.children = null;
        this.endCity = endCity;
        this.cost = cost;
        this.parent = parent;
        this.subRoadRepository = subRoadRepository;
        this.cityRepository = cityRepository;
    }


    public boolean isGoal() {
        return endCity.equals(this.currentCity);
    }


    public List<Node> getChildren() {
        if (this.children == null) {
            List<Node> result = new ArrayList<>();
            List<SubRoad> byFirstCity = this.subRoadRepository.findByFirstCity(this.currentCity.getName());
            for (int i = 0; i < byFirstCity.size(); i++) {
                SubRoad subRoad = byFirstCity.get(i);
                Node node = new Node(this.cityRepository.findByName(subRoad.getSecondCity()).get(), this.cost + subRoad.getDistance()
                        , this, endCity, this.subRoadRepository, this.cityRepository);
                result.add(node);
            }

            this.children = result;
        }
        return this.children;
    }

    @Override
    public String toString() {
        return this.currentCity.getName();
    }

    @Override
    public int hashCode() {
        return this.currentCity.hashCode();
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Node)) {
            return false;
        }

        Node otherNode = (Node) other;
        return this.currentCity.equals(otherNode.currentCity);
    }


    public double getEstimateToGoal() {
        Optional<City> currentCityFromRepo = this.cityRepository.findByName(currentCity.getName());
        Optional<City> endCityFromRepo = this.cityRepository.findByName(endCity.getName());
        if (currentCityFromRepo.isPresent() && endCityFromRepo.isPresent()) {
            double deltaX = currentCityFromRepo.get().getCoordinateX() - endCityFromRepo.get().getCoordinateX();
            double deltaY = currentCityFromRepo.get().getCoordinateY() - endCityFromRepo.get().getCoordinateY();
            double result = (Math.sqrt((Math.pow(deltaX, 2) + Math.pow(deltaY, 2)))) / 1000;

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            String format = nf.format(result).replace(",", ".");
            return Double.parseDouble(format);
        }
        return 0;
    }
}

