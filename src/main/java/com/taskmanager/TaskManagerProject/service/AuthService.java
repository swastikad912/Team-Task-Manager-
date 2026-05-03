package com.taskmanager.TaskManagerProject.service;

import com.taskmanager.TaskManagerProject.dto.AuthDtos;
import com.taskmanager.TaskManagerProject.entity.User;
import com.taskmanager.TaskManagerProject.exception.ApiException;
import com.taskmanager.TaskManagerProject.repository.UserRepository;
import com.taskmanager.TaskManagerProject.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthDtos.AuthResponse signup(AuthDtos.SignupRequest request) {
        userRepository.findByEmail(request.getEmail().toLowerCase()).ifPresent(u -> {
            throw new ApiException("Email is already registered");
        });

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(encoder.encode(request.getPassword()));
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new AuthDtos.AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ApiException("Invalid credentials"));
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new AuthDtos.AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }
}