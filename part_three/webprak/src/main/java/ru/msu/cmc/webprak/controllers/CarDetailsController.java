package ru.msu.cmc.webprak.controllers;
import ru.msu.cmc.webprak.DAO.CarsDAO;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.TechnicalSpecs;
import ru.msu.cmc.webprak.models.ConsumerSpecs;
import ru.msu.cmc.webprak.models.DynamicSpecs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
public class CarDetailsController {

    private final CarsDAO carsDAO;

    public CarDetailsController(CarsDAO carsDAO) {
        this.carsDAO = carsDAO;
    }

    @GetMapping("/cars/{id}")
    public String carDetails(@PathVariable Long id, Model model) {
        Cars car = carsDAO.getById(id);
        if (car == null) {
            return "redirect:/catalog"; // Если авто не найдено
        }

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
        TechnicalSpecs techSpecs = carsDAO.getTechnicalSpecs(id);
        ConsumerSpecs consumerSpecs = carsDAO.getConsumerSpecs(id);
        DynamicSpecs dynamicSpecs = carsDAO.getDynamicSpecs(id);

        model.addAttribute("car", car);
        model.addAttribute("techSpecs", techSpecs);
        model.addAttribute("consumerSpecs", consumerSpecs);
        model.addAttribute("dynamicSpecs", dynamicSpecs);
        model.addAttribute("originalPrice", originalPrice);
        model.addAttribute("discountedPrice", discountedPrice);
        model.addAttribute("totalDiscount", totalDiscount);
        model.addAttribute("hasDiscount", hasDiscount);
        return "car-details";
    }
}