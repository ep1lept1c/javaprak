package ru.msu.cmc.webprak.controllers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.msu.cmc.webprak.DAO.CarsDAO;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.TechnicalSpecs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CarCatalogController {

    private final CarsDAO carsDAO;

    public CarCatalogController(CarsDAO carsDAO) {
        this.carsDAO = carsDAO;
    }

    @GetMapping("/catalog")
    public String catalog(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Cars.Status status,
            @RequestParam(required = false) Boolean hasAC,
            @RequestParam(required = false) String fuelType,
            @RequestParam(defaultValue = "priceDesc") String sort,
            Model model
    ) {
        // Фильтрация
        Collection<Cars> cars = carsDAO.findWithAdvancedSearch(
                brand, minPrice, maxPrice, status, hasAC, fuelType, null
        );


        // Обработка скидок
        List<CarViewDTO> carViews = cars.stream()
                .map(this::createCarViewDTO)
                .collect(Collectors.toList());

        // Сортировка
        if ("priceAsc".equals(sort)) {
            carViews.sort(Comparator.comparing(CarViewDTO::getDisplayPrice));
        } else {
            carViews.sort(Comparator.comparing(CarViewDTO::getDisplayPrice).reversed());
        }

        // Данные для фильтров
        model.addAttribute("cars", carViews);
        model.addAttribute("brands", carsDAO.getCarCountByBrand().keySet());
        model.addAttribute("statuses", Cars.Status.values());
        model.addAttribute("fuelTypes", TechnicalSpecs.FuelType.values());
        model.addAttribute("sort", sort);

        return "catalog";
    }

    private CarViewDTO createCarViewDTO(Cars car) {
        BigDecimal originalPrice = car.getPrice();
        BigDecimal discountedPrice = originalPrice;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        boolean hasDiscount = false;

        if (car.getPromotions() != null && !car.getPromotions().isEmpty()) {
            totalDiscount = car.getPromotions().stream()
                    .filter(p -> p.getIsActive() != null && p.getIsActive())
                    .map(p -> p.getDiscount() != null ? p.getDiscount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
                hasDiscount = true;
                BigDecimal discountMultiplier = BigDecimal.ONE
                        .subtract(totalDiscount.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                discountedPrice = originalPrice.multiply(discountMultiplier)
                        .setScale(2, RoundingMode.HALF_UP);
            }
        }

        return new CarViewDTO(
                car.getId(),
                car.getBrand(),
                car.getManufacturer(),
                car.getRegistrationNumber(),
                car.getStatus(),
                originalPrice,
                discountedPrice,
                totalDiscount,
                hasDiscount,
                hasDiscount ? discountedPrice : originalPrice // displayPrice
        );
    }

    @Getter
    @AllArgsConstructor
    private static class CarViewDTO {
        private final Long id;
        private final String brand;
        private final String manufacturer;
        private final String registrationNumber;
        private final Cars.Status status;
        private final BigDecimal originalPrice;
        private final BigDecimal discountedPrice;
        private final BigDecimal totalDiscount;
        private final boolean hasDiscount;
        private final BigDecimal displayPrice; // Для сортировки
    }
}
