package ru.msu.cmc.webprak.controllers;

import org.springframework.transaction.annotation.Transactional;
import ru.msu.cmc.webprak.DAO.CarsDAO;
import ru.msu.cmc.webprak.DAO.PromotionsDAO;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.Promotions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final PromotionsDAO promotionsDAO;
    private final CarsDAO carsDAO;

    public HomeController(PromotionsDAO promotionsDAO, CarsDAO carsDAO) {
        this.promotionsDAO = promotionsDAO;
        this.carsDAO = carsDAO;
    }
    @Transactional
    @GetMapping("/")
    public String home(Model model) {
        // Получаем активные акции, сортируем по скидке (DESC) и берём топ-5
        List<Promotions> promotions = promotionsDAO.getAllWithCars();
//        List<Promotions> topPromotions = promotionsDAO.findByIsActiveTrue().stream()
//                .sorted(Comparator.comparing(Promotions::getDiscount).reversed())
//                .limit(5)
//                .collect(Collectors.toList());
        List<Promotions> topPromotions = promotions.stream()
                .filter(Promotions::getIsActive)  // или .filter(p -> p.getIsActive() == true)
                .sorted(Comparator.comparing(Promotions::getDiscount).reversed())
                .limit(5)
                .collect(Collectors.toList());
        // Получаем доступные авто, сортируем по цене (DESC) и берём топ-5
        List<Cars> topCars = carsDAO.findByStatus(Cars.Status.available).stream()
                .sorted(Comparator.comparing(Cars::getPrice).reversed())
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("promotions", topPromotions);
        model.addAttribute("cars", topCars);

        return "index"; // Возвращаем шаблон index.html
    }
}