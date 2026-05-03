package com.taskmanager.TaskManagerProject.service;

import com.taskmanager.TaskManagerProject.exception.ApiException;
import com.taskmanager.TaskManagerProject.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthContextService {
    public UserPrincipal currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ApiException("Unauthorized");
        }
        return principal;
    }
}
