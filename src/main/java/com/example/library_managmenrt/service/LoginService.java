package com.example.library_managmenrt.service;

import com.example.library_managmenrt.dao.AdminDao;
import com.example.library_managmenrt.model.Admin;

public class LoginService {
    private AdminDao adminDao = new AdminDao();

    public boolean authenticate(String userName, String password){
        Admin admin = adminDao.getAdminByUserName(userName);
        if (admin != null) {
            return admin.getPassword().equals(password);  // Replace with hashed password check
        }
        return false;
    }
}
