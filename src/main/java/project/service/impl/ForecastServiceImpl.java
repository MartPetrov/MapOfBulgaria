package project.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.entity.City;
import project.entity.DTO.ImportForecastDTO;
import project.entity.DTO.ImportForecastRoot;
import project.entity.Forecast;
import project.repositories.CityRepository;
import project.repositories.ForecastRepository;
import project.service.ForecastService;
import project.util.ValidationUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.stream.Collectors;

import static project.constans.Messages.INVALID_FORECAST;
import static project.constans.Messages.VALID_FORECAST_FORMAT;
import static project.constans.Paths.XML_FORECAST;


@Service
public class ForecastServiceImpl implements ForecastService {

    private final ForecastRepository forecastRepository;
    private final CityRepository cityRepository;
    private final Unmarshaller unmarshaller;
    private final project.util.ValidationUtils validationUtils;
    private final ModelMapper modelMapper;


    @Autowired
    public ForecastServiceImpl(
            ForecastRepository forecastRepository,
            CityRepository cityRepository,
            ValidationUtils validationUtils,
            ModelMapper modelMapper) throws JAXBException {
        this.forecastRepository = forecastRepository;
        this.validationUtils = validationUtils;
        this.cityRepository = cityRepository;
        this.modelMapper = modelMapper;


        JAXBContext context = JAXBContext.newInstance(ImportForecastRoot.class);
        this.unmarshaller = context.createUnmarshaller();
    }

    @Override
    public boolean areImported() {
        return this.forecastRepository.count() > 0;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString(XML_FORECAST);
    }

    @Override
    public String importForecasts() throws IOException, JAXBException {
        ImportForecastRoot forecastDTOs = (ImportForecastRoot) this.unmarshaller.unmarshal(new FileReader(XML_FORECAST.toFile()));
        return forecastDTOs.getForecasts().stream().map(this::importForecast).collect(Collectors.joining("\n"));
    }

    private String importForecast(ImportForecastDTO importForecastDTO) {
        boolean isValid = this.validationUtils.isValid(importForecastDTO);
        String result = "";
        result = checkAndImportForecast(importForecastDTO, isValid);
        System.out.println(result);
        return result;
    }

    private String checkAndImportForecast(ImportForecastDTO importForecastDTO, boolean isValid) {
        String result;
        if (!isValid) {
            result = INVALID_FORECAST;
        } else {
            Forecast forecast = this.modelMapper.map(importForecastDTO, Forecast.class);
            Optional<City> byName = this.cityRepository.findByName(importForecastDTO.getCity());
            if (byName.isPresent()) {
                City currentCityFromRepo = byName.get();
                forecast.setCity(currentCityFromRepo);
            }
            this.forecastRepository.save(forecast);
            result = String.format(VALID_FORECAST_FORMAT,forecast.getCity().name, forecast.getDayOfWeek(), forecast.getMaxTemperature());
        }
        return result;
    }

}
