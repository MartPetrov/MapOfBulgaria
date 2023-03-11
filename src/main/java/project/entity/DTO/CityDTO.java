package project.entity.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
public class CityDTO {
    @NotNull
    @Size(min = 2, max = 60)
    private String cityName;

    @NotNull
    private int population;
    private double coordinateX;
    private double coordinateY;


}

