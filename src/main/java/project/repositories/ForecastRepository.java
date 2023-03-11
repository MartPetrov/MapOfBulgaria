package project.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.entity.City;
import project.entity.DayOfWeek;
import project.entity.Forecast;

import java.util.Optional;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Long> {

    Optional<Forecast> getFirstByCityAndDayOfWeek(City city_name, DayOfWeek dayOfWeek);
}
