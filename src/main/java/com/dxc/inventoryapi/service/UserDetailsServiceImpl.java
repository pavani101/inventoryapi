package com.dxc.inventoryapi.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dxc.inventoryapi.entity.InventoryUser;
import com.dxc.inventoryapi.repository.InventoryUserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private InventoryUserRepository userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		
		if(!userRepo.existsById(userName)) {
			throw new UsernameNotFoundException("No user found");
		}
		
		InventoryUser user = userRepo.findById(userName).orElse(null);
		
		return new User(user.getUserName(), user.getEncodedPassword(), new ArrayList<>());		
	}
	
	//To register a user for the 1st time	
	public InventoryUser save(InventoryUser user) {
		user.setEncodedPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.saveAndFlush(user);
		
	}

}
