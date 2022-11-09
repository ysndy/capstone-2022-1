package controller;

import org.springframework.web.bind.annotation.GetMapping;

public class SampleController {

    @GetMapping("/hello")
    public String[] hello(){
        return new String[]{"Hello", "world"};
    }

}
