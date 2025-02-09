package com.telejaca.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.telejaca.model.User;
import com.telejaca.model.UserException;
import com.telejaca.service.UserService;

@Controller
public class GeneralController {
	@Autowired
	UserService userService;
	
	
	@GetMapping("/error")
	public String listUsers() {
		return "error";
	}
	
	@GetMapping({"/", "/index", "/home", "/inicio"})
	public String showIndex(Model model) {
		
		try {
			List<User> users = userService.getAllActiveUsers();
			model.addAttribute("users", users);
		}catch(UserException ue) {
			model.addAttribute("noUsersMsg", ue.getMessage());
		}
//		Map<Integer, Integer> age = new LinkedHashMap<Integer, Integer>();
		
//		users.stream().forEach(); 
				
		return "index";
	}
		
}


