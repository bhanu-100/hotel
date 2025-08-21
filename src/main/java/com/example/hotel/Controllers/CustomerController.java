package com.example.hotel.Controllers;


import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class CustomerController {


    @GetMapping("check")
    public String show(){
        return "Hey! I am Customer controller";
    }

}
