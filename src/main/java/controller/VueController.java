package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vue")
public class VueController {

    @GetMapping("/")
    public String vue(){
        return "index";
    }

}
