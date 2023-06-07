package com.elk.controller;

import com.elk.dto.UserDTO;
import com.elk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.elk.utils.ResponseUtils.successResponseWithBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @GetMapping("/hello")
    public ResponseEntity<Object> hello() {
        return successResponseWithBody("Hello");
    }

    @PostMapping("/user")
    public ResponseEntity<Object> addUser(@RequestBody UserDTO userDTO) {
        userDTO = userService.addUser(userDTO);
        return successResponseWithBody(userDTO);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Long userId,
                                                   @RequestBody UserDTO userDTO) {
        userDTO = userService.updateUser(userId, userDTO);
        return successResponseWithBody(userDTO);
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getUsers() {
        List<UserDTO> userDTOList = userService.getUsers();
        return successResponseWithBody(userDTOList);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getDepartments(@PathVariable("userId") Long userId) {
        UserDTO departmentDTO = userService.getUserById(userId);
        return successResponseWithBody(departmentDTO);
    }
}
