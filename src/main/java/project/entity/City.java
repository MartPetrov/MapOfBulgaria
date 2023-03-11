package project.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "cities")
public class City implements Serializable , Comparable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_name", nullable = false, unique = true)
    public String name;

    @Column(name = "population")
    private int population;
    @Column(name = "coordinate_X")
    private double coordinateX;
    @Column(name = "coordinate_Y")
    private double coordinateY;

    @ManyToMany(mappedBy = "cities")
    private List<Road> roads;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("City with name: ")
                .append(this.name)
                .append(", with coordinates: X = ")
                .append(String.format("%.2f", this.coordinateX))
                .append(" , Y = ")
                .append(String.format("%.2f", this.coordinateY))
                .append(" and population: ")
                .append(this.population);
        return sb.toString();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof City) {
            City other = (City) obj;
            return Objects.equals(coordinateX, other.coordinateX) && Objects.equals(coordinateY, other.coordinateY) &&
                    Objects.equals(name, other.name);
        }
        return false;
    }


    @Override
    public int hashCode() {
        return Objects.hash(coordinateX, coordinateY);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
