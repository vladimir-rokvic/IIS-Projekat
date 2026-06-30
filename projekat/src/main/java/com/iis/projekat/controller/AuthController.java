package com.iis.projekat.controller;

import com.iis.projekat.dto.LoginRequest;
import com.iis.projekat.dto.RegisterRequest;
import com.iis.projekat.model.User;
import com.iis.projekat.repository.*;
import com.iis.projekat.repository.Beneficiary.BeneficiaryRepository;
import com.iis.projekat.security.JwtUtil;
import com.iis.projekat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    private final EmployeeRepository employeeRepository;
    private final DonorRepository donorRepository;
    private final BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          EmployeeRepository employeeRepository,
                          DonorRepository donorRepository,
                          BeneficiaryRepository beneficiaryRepository) {

        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.donorRepository = donorRepository;
        this.beneficiaryRepository = beneficiaryRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        userService.register(request);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email,
                        request.password
                )
        );

        User user = userRepository.findByEmail(request.email)
                .orElseThrow();

        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();

        response.put("token", token);
        response.put("email", user.getEmail());
        response.put("name", user.getName());
        response.put("surname", user.getSurname());
        response.put("id", user.getId());

        employeeRepository.findByEmail(user.getEmail()).ifPresent(emp -> response.put("role", emp.getEmployeeType().name()));

        if (volunteerRepository.existsByEmail(user.getEmail())) {
            response.put("role", "VOLUNTEER");
        } else if (donorRepository.existsByEmail(user.getEmail())) {
            response.put("role", "DONOR");
        } else if (beneficiaryRepository.existsByEmail(user.getEmail())) {
            response.put("role", "BENEFICIARY");
        }

        return ResponseEntity.ok(response);
    }
}
