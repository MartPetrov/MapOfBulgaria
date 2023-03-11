package project.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "subRoads")
public class SubRoad implements Comparable<SubRoad>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "road_number", nullable = false)
    public String number;


    @Column
    private double distance;


    @Column
    private double speed;

    @Column
    private String firstCity;

    @Column
    private String secondCity;

    @Override
    public int compareTo(SubRoad o) {
        if (this.getDistance() - o.getDistance() > 0) {
            return -1;
        }
        return 1;
    }
}
