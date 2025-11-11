package com.home.monitor.app.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private static final String AUTH_ATTR = "authenticated";

    @Value("${app.access-code:SHUBHSUBIRMANJU}")
    private String accessCode;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam("code") String code, HttpSession session, Model model) {
        if (accessCode.equals(code)) {
            session.setAttribute(AUTH_ATTR, Boolean.TRUE);
            return "redirect:/main";
        } else {
            model.addAttribute("error", "Invalid access code");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}