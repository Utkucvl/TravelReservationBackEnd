package com.rezervation.TravelRezervation.service.impl;

import com.rezervation.TravelRezervation.dao.entity.User;
import com.rezervation.TravelRezervation.dao.UserRepository;
import com.rezervation.TravelRezervation.security.JWTUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        return JWTUserDetails.create(user);
    }

    public UserDetails loadUserById(Long id ){
        User user = userRepository.findById(id).get();
        return JWTUserDetails.create(user);
    }
    public static String extractUsernameFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        if ( authentication==null ) {
            return null;
        }
        username = authentication.getName();
        return username;
    }
}
