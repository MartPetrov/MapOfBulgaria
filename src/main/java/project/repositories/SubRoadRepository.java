package project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.entity.SubRoad;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubRoadRepository extends JpaRepository<SubRoad,Long> {

    List<SubRoad> findByFirstCity(String city);

    Optional<SubRoad> findSubRoadsByFirstCityAndSecondCityContains(String firstCity,String secondCity);

}
