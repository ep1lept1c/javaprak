package ru.msu.cmc.webprak.controllers;


import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.UsersDAO;
import ru.msu.cmc.webprak.models.Users;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")

public class UserController {

    private final UsersDAO usersDAO;

    public UserController(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    @PostMapping("/update-phone")
    public ResponseEntity<Map<String, Object>> updatePhone(@RequestBody Map<String, String> payload,
                                                           HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("success", false);
            response.put("message", "Пользователь не авторизован");
            return ResponseEntity.status(401).body(response);
        }

        String phone = payload.get("phone");
        if (phone == null || phone.replaceAll("\\D", "").length() < 10) {
            response.put("success", false);
            response.put("message", "Некорректный номер телефона");
            return ResponseEntity.badRequest().body(response);
        }

        // Обновляем номер телефона в объекте и БД
        user.setPhone(phone);
        usersDAO.update(user);

        // Обновляем сессию с новым пользователем
        session.removeAttribute("user");
        session.setAttribute("user", user);

        response.put("success", true);
        response.put("phone", phone);
        return ResponseEntity.ok(response);
    }
}