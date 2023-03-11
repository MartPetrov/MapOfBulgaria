package project.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "roads")
public class Road implements Comparable<Road> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "road_number", nullable = false, unique = true)
    public String number;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roads_cities",
            joinColumns = @JoinColumn(name = "road_id"),
            inverseJoinColumns = @JoinColumn(name = "city_id")
    )
    private List<City> cities;

    @Column
    private String distanceFromPrevCity;

    @Column
    private double distance;


    @Column
    private double speed;


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            if (i == cities.size() - 1) {
                sb.append(city.getName());
            } else {
            sb.append(city.getName()).append(", ");
            }
        }

        return "-Road with number: " + this.number
                + System.lineSeparator() + "--Cities on the road are: " + sb
                + System.lineSeparator() + "---Distance: " + distance + " km"
                + System.lineSeparator() +"----Max speed: " + speed + " km/h";
    }

    @Override
    public int compareTo(Road o) {
        if (this.getSpeed() - o.getSpeed() > 0) {
            return -1;
        }
        return 1;
    }
}
