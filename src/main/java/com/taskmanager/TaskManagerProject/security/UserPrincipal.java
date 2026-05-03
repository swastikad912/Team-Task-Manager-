package com.taskmanager.TaskManagerProject.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPrincipal {
    private Long id;
    private String email;
    private String name;
}
