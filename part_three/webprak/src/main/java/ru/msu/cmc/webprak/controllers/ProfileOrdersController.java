package ru.msu.cmc.webprak.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.CarsDAO;
import ru.msu.cmc.webprak.DAO.OrdersDAO;
import ru.msu.cmc.webprak.DAO.TestDrivesDAO;
import ru.msu.cmc.webprak.DAO.UsersDAO;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.Orders;
import ru.msu.cmc.webprak.models.TestDrives;
import ru.msu.cmc.webprak.models.Users;

import java.time.LocalDateTime;
import java.util.Collection;

@Controller
@RequestMapping("/profile/orders")
public class ProfileOrdersController {

    private final OrdersDAO ordersDAO;

    @Autowired
    public ProfileOrdersController(OrdersDAO ordersDAO) {
        this.ordersDAO = ordersDAO;
    }

    @GetMapping
    public String showUserOrders(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login?redirectTo=/profile/orders";
        }

        Collection<Orders> orders = ordersDAO.findByUser(user);
        model.addAttribute("orders", orders);
        return "profile";
    }

    @GetMapping("/{orderId}")
    public String showOrderDetails(@PathVariable Long orderId,
                                   HttpSession session,
                                   Model model) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login?redirectTo=/profile/orders/" + orderId;
        }

        Orders order = ordersDAO.getById(orderId);
        if (order == null || !order.getUser().getId().equals(user.getId())) {
            return "redirect:/profile/orders?error=order_not_found";
        }

        model.addAttribute("order", order);
        return "order-details";
    }
}