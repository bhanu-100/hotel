package com.example.hotel.Models;

import com.example.hotel.Enums.UserRoles;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserModel {
    private String userName;
    private String email;
    private String password;
    private List<UserRoles> userRoles = new ArrayList<>();
}
