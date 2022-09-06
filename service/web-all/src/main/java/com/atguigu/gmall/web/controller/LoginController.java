package com.atguigu.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author quxiaolei
 * @date 2022/9/6 - 18:52
 */
@Controller
public class LoginController {
    @GetMapping("/login.html")
    public String loginPage(@RequestParam("originUrl") String originUrl, Model model) {
        model.addAttribute("originUrl", originUrl);
        return "login";
    }
}
