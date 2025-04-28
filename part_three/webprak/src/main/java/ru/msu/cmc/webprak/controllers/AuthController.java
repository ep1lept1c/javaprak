package ru.msu.cmc.webprak.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.UsersDAO;
import ru.msu.cmc.webprak.models.Users;

@Controller
public class AuthController {

    private final UsersDAO usersDAO;

    public AuthController(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    // DTO для формы регистрации
    @Setter
    @Getter
    public static class RegistrationForm {
        // геттеры и сеттеры
        private String fullName;
        private String email;
        private String password;

    }

    // Страница регистрации - GET
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userForm", new RegistrationForm());
        return "registration";
    }

    // Обработка регистрации - POST
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userForm") RegistrationForm form, Model model, HttpSession session) {
        // Проверяем, нет ли уже пользователя с таким email
        Users existingUser = usersDAO.findByEmail(form.getEmail());
        if (existingUser != null) {
            model.addAttribute("error", "Пользователь с таким email уже существует");
            return "registration";
        }
        // Создаём нового пользователя
        Users user = new Users();
        user.setFullName(form.getFullName());
        user.setEmail(form.getEmail());
        user.setPasswordHash(form.getPassword()); // хеширование пока не используется
        user.setRole(Users.Role.client);

        usersDAO.save(user);

        session.setAttribute("user", user);

        return "redirect:/profile";
    }

    // Страница входа - GET
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "redirectTo", required = false) String redirectTo,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверный email или пароль");
        }
        if (redirectTo != null) {
            model.addAttribute("redirectTo", redirectTo);
        }
        return "login";
    }

    // Обработка входа - POST
    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(required = false) String redirectTo,
                               Model model,
                               HttpSession session) {

        Users user = usersDAO.findByEmail(email);
        if (user == null || !user.getPasswordHash().equals(password)) {
            model.addAttribute("error", "Неверный email или пароль");
            return "login";
        }

        session.setAttribute("user", user);
        if (redirectTo != null && !redirectTo.isBlank()) {
            return "redirect:" + redirectTo;
        }

        return "redirect:/profile";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}