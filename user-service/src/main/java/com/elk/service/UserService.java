package com.elk.service;

import com.elk.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO addUser(UserDTO userDTO);

    UserDTO updateUser(Long userId, UserDTO userDTO);

    List<UserDTO> getUsers();

    UserDTO getUserById(Long userId);
}
