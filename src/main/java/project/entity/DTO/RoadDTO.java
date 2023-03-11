package project.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoadDTO {

    @NotNull
    @XmlElement(name = "number")
    private String number;

    @NotNull
    @XmlElement(name = "start")
    private String startCity;

    @NotNull
    @XmlElement(name = "end")
    private String endCity;

    @NotNull
    @XmlElement(name = "cities")
    private String cities;

    @NotNull
    @XmlElement(name = "distance")
    private double distance;

    @NotNull
    @XmlElement(name = "speed")
    private double speed;
}
