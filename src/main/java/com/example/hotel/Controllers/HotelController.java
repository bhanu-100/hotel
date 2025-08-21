package com.example.hotel.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hotel")
public class HotelController {
    @GetMapping("check")
    public String show(){
        return "Hey! I am Hotel controller";
    }
}
