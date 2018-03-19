package fr.cnam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String indexV0() {
        return "formulaireV0";
    }

    @GetMapping("/moteur")
    public String index() {
        return "formulaire";
    }
}