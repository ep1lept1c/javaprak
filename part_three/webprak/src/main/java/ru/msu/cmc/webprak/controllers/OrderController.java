package ru.msu.cmc.webprak.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.CarsDAO;
import ru.msu.cmc.webprak.DAO.OrdersDAO;
import ru.msu.cmc.webprak.DAO.TestDrivesDAO;
import ru.msu.cmc.webprak.models.Cars;
import ru.msu.cmc.webprak.models.Orders;
import ru.msu.cmc.webprak.models.TestDrives;
import ru.msu.cmc.webprak.models.Users;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final CarsDAO carsDAO;
    private final OrdersDAO ordersDAO;
    private final TestDrivesDAO testDrivesDAO;

    @Autowired
    public OrderController(CarsDAO carsDAO,
                           OrdersDAO ordersDAO, TestDrivesDAO testDrivesDAO) {
        this.carsDAO = carsDAO;
        this.ordersDAO = ordersDAO;
        this.testDrivesDAO = testDrivesDAO;
    }

    @GetMapping("/{carId}")
    public String orderForm(@PathVariable Long carId,
                            @RequestParam(required = false) Integer step,
                            HttpSession session,
                            Model model) {


        Cars car = carsDAO.getById(carId);
        if (car == null || car.getStatus() != Cars.Status.available) {
            return "redirect:/catalog?error=car_not_available";
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
        model.addAttribute("originalPrice", originalPrice);
        model.addAttribute("discountedPrice", discountedPrice);
        model.addAttribute("totalDiscount", totalDiscount);
        model.addAttribute("hasDiscount", hasDiscount);
        model.addAttribute("car", car);
        model.addAttribute("currentStep", (step != null ? step : 1));
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login?redirectTo=/order/" + carId;
        }

        model.addAttribute("user", user);

        return "order-form";
    }

    @PostMapping("/submit")
    public String submitOrder(
            @RequestParam Long carId,
            @RequestParam int currentStep,
            @RequestParam(required = false) String testDriveTime,
            @RequestParam(required = false) String salon,
            HttpSession session,
            Model model) {

        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login?redirectTo=/order/" + carId + "?step=" + currentStep;
        }

        Cars car = carsDAO.getById(carId);
        if (car == null) {
            return "redirect:/catalog";
        }


        boolean requiresTestDrive = testDriveTime != null && !testDriveTime.isEmpty();

        try {
            Orders order = ordersDAO.createOrder(user.getId(), car.getId(), requiresTestDrive);

            if (requiresTestDrive) {
                LocalDateTime driveTime = LocalDateTime.parse(testDriveTime);

                if (driveTime.isBefore(LocalDateTime.now().plusHours(24))) {
                    model.addAttribute("error", "Тест-драйв можно запланировать минимум за 24 часа");
                    model.addAttribute("car", car);
                    model.addAttribute("currentStep", 2);
                    model.addAttribute("user", user);
                    return "order-form";
                }

                if (!testDrivesDAO.isCarAvailableForTestDrive(car, driveTime)) {
                    model.addAttribute("error", "Это время уже занято");
                    model.addAttribute("car", car);
                    model.addAttribute("currentStep", 2);
                    model.addAttribute("user", user);
                    return "order-form";
                }

                TestDrives testDrive = new TestDrives();
                testDrive.setUser(user);
                testDrive.setCar(car);
                testDrive.setScheduledTime(driveTime);
                testDrive.setStatus(TestDrives.Status.pending);
                testDrivesDAO.save(testDrive);
            }

            return "redirect:/order/confirmation/" + order.getId();

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при оформлении заказа");
            model.addAttribute("car", car);
            model.addAttribute("currentStep", 1);
            model.addAttribute("user", user);
            return "order-form";
        }
    }
    @GetMapping("/confirmation/{orderId}")
    public String confirmation(@PathVariable Long orderId, Model model) {
        Orders order = ordersDAO.getById(orderId);
        if (order == null) {
            return "redirect:/catalog";
        }
        model.addAttribute("order", order);
        return "confirmation";
    }

}
