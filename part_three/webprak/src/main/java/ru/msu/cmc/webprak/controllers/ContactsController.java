package ru.msu.cmc.webprak.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactsController {

    @GetMapping("/contacts")
    public String contacts(Model model) {
        // Если данные динамические (из БД), загружаем здесь
        // model.addAttribute("branches", branchDAO.findAll());
        return "contacts"; // contacts.html
    }
}