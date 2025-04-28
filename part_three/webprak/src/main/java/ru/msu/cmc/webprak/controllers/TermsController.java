package ru.msu.cmc.webprak.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TermsController {

    @GetMapping("/terms")
    public String termsPage() {
        return "terms"; // Это вернет terms.html
    }
}