package controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequestMapping("/registration/")
public class RegistrationController {


    @GetMapping("/tiktok")
    public void tiktok(){

    }

    @GetMapping("/facebook")
    public void facebook(){

    }

    @GetMapping("/instagram")
    public void instagram(){

    }

    @GetMapping("/youtube")
    public void youtube(){

    }

}
