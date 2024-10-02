package com.rezervation.TravelRezervation.controller;

import com.rezervation.TravelRezervation.dao.entity.User;
import com.rezervation.TravelRezervation.dto.AuthDto;
import com.rezervation.TravelRezervation.dto.UserLoginDto;
import com.rezervation.TravelRezervation.dto.UserRegisterDto;
import com.rezervation.TravelRezervation.security.JwtTokenProvider;
import com.rezervation.TravelRezervation.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserService userService;

    private AuthenticationManager authenticationManager;

    private JwtTokenProvider jwtTokenProvider;

    private PasswordEncoder passwordEncoder;

    AuthController(UserService userService,AuthenticationManager authenticationManager,
                   PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider=jwtTokenProvider;
    }
    @PostMapping("/login")
    public ResponseEntity<AuthDto> login (@RequestBody UserLoginDto userRequest) {
        // 1. İlk olarak email ile kullanıcıyı veritabanından çekiyoruz
        User user = userService.getOneUserByEmail(userRequest.getEmail());

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Kullanıcı bulunamazsa 401 döndürüyoruz
        }

        // 2. Kullanıcının userName ve password bilgilerini doğrulamak için auth token oluşturuyoruz
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getUserName(), userRequest.getPassword());
        Authentication auth = authenticationManager.authenticate(authToken);

        // 3. Authentication başarılı olursa, JWT token oluşturuyoruz
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwtToken = jwtTokenProvider.generateJwtToken(auth);

        // 4. Token ve gerekli diğer bilgileri AuthDto ile döndürüyoruz
        String accessToken = "Bearer " + jwtToken;
        String message = "You have logged in";

        AuthDto authResponse = new AuthDto();
        authResponse.setMessage(message);
        authResponse.setUserId(user.getId());
        authResponse.setAccessToken(accessToken);
        authResponse.setRole(user.getRole());

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto> register (@RequestBody UserRegisterDto userRequest){
        String message ;
        if(userService.getOneUserByUserName(userRequest.getUserName()) != null){
            message = "Username is alredy in use";
            AuthDto authResponse = new AuthDto();
            authResponse.setMessage(message);
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
        }

        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        User user = new User();
        user.setUserName(userRequest.getUserName());
        user.setPassword(userRequest.getPassword());
        user.setRole("USER");
        user.setAge(userRequest.getAge());
        user.setSurname(userRequest.getSurname());
        user.setEmail(userRequest.getEmail());
        user.setTcNo(userRequest.getTcNo());
        User created = userService.createOneUser(user);
        message = "User successfully has been created";
        AuthDto authResponse = new AuthDto();
        authResponse.setMessage(message);
        authResponse.setUserId(user.getId());
        authResponse.setRole(created.getRole());
        return new ResponseEntity<>( authResponse, HttpStatus.CREATED);

    }

}
