package project.entity.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "roads")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@NoArgsConstructor
public class ImportRoadRoot {
    @XmlElement(name = "road")
    private List<RoadDTO> roads;
}
