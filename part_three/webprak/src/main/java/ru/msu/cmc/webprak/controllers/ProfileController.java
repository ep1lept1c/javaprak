package ru.msu.cmc.webprak.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.msu.cmc.webprak.DAO.UsersDAO;
import ru.msu.cmc.webprak.models.Buybacks;
import ru.msu.cmc.webprak.models.Orders;
import ru.msu.cmc.webprak.models.TestDrives;
import ru.msu.cmc.webprak.models.Users;

import java.util.Collection;

@Controller
public class ProfileController {

    private final UsersDAO usersDAO;

    public ProfileController(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Получаем заказы, тест-драйвы и заявки на выкуп через DAO пользователей
        Long userId = user.getUserId();

        Collection<Orders> orders = usersDAO.getOrdersByUser(userId);
        Collection<TestDrives> testDrives = usersDAO.getTestDrivesByUser(userId);
        Collection<Buybacks> buybacks = usersDAO.getBuybacksByUser(userId);

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("testDrives", testDrives);
        model.addAttribute("buybacks", buybacks);

        return "profile";
    }
}