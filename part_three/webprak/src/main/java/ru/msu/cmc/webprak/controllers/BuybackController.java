package ru.msu.cmc.webprak.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.msu.cmc.webprak.DAO.BuybacksDAO;
import ru.msu.cmc.webprak.DAO.UsersDAO;
import ru.msu.cmc.webprak.models.Buybacks;
import ru.msu.cmc.webprak.models.Users;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/buyback")
public class BuybackController {

    private final BuybacksDAO buybacksDAO;
    private final UsersDAO usersDAO;

    public BuybackController(BuybacksDAO buybacksDAO, UsersDAO usersDAO) {
        this.buybacksDAO = buybacksDAO;
        this.usersDAO = usersDAO;
    }

    @GetMapping
    public String form(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login?redirectTo=/buyback";
        }
        model.addAttribute("brands", List.of(
                "Audi", "BMW", "Ford", "Hyundai", "Kia",
                "Lada", "Lexus", "Mercedes", "Nissan", "Toyota"
        ));
        return "buyback-form";
    }

    @PostMapping
    public String submit(
            @RequestParam String carBrand,
            @RequestParam String carModel,
            @RequestParam Integer carYear,
            @RequestParam Integer mileage,
            @RequestParam(required = false) String vin,
            @RequestParam String condition,
            @RequestParam MultipartFile[] photos,
            HttpSession session,
            Model model
    ) {
        // Валидация
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login?redirectTo=/buyback-form";
        }

        if (carYear < 1990 || carYear > LocalDateTime.now().getYear() + 1) {
            model.addAttribute("error", "Некорректный год выпуска");
            return form(model,session);
        }

        try {
            // Сохранение фото (примерная реализация)
            String photosJson = processUploadedPhotos(photos);
            Buybacks buyback = buybacksDAO.createBuyback(
                    user.getUserId(),
                    carBrand + " " + carModel,
                    carYear,
                    mileage,
                    photosJson
            );

            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при создании заявки");
            return form(model, session);
        }
    }

    private String processUploadedPhotos(MultipartFile[] photos) {
        // Здесь должна быть логика сохранения файлов и возврата путей
        // Пример для теста:
        return "[\"uploads/photo1.jpg\", \"uploads/photo2.jpg\"]";
    }
}