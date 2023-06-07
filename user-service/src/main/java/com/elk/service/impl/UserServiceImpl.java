package com.elk.service.impl;

import com.elk.dto.DepartmentDTO;
import com.elk.dto.UserDTO;
import com.elk.exceptions.InternalServerException;
import com.elk.exceptions.InvalidRequestException;
import com.elk.exceptions.ResourceNotFoundException;
import com.elk.model.User;
import com.elk.repositories.UserRepository;
import com.elk.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Override
    public UserDTO addUser(UserDTO userDTO) {
        log.info("Invoke addUser method.");
        try {
            User user = User.buildEntity(userDTO);
            userRepository.save(user);
            log.info("End addUser method.");
            return UserDTO.buildDTO(user);
        } catch (Exception ex) {
            log.error("Exception while add user.", ex);
            throw new InternalServerException("Exception while add user.");
        }
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        log.info("Invoke updateUser method.");
        validateUpdateRequest(userDTO, userId);
        try {
            User user = User.buildEntity(userDTO);
            user.setId(userId);
            userRepository.save(user);
            log.info("End updateUser method.");
            return UserDTO.buildDTO(user);
        } catch (Exception ex) {
            log.error("Exception while update user.", ex);
            throw new InternalServerException("Exception while update user.");
        }
    }

    @Override
    public List<UserDTO> getUsers() {
        log.info("Invoke getUsers method.");
        try {
            List<User> userList = userRepository.findAll();
            log.info("End getUsers method.");
            return userList.stream().map(UserDTO::buildDTO).toList();
        } catch (Exception ex) {
            log.error("Exception while get all users.", ex);
            throw new InternalServerException("Exception while get all users.");
        }
    }

    @Override
    public UserDTO getUserById(Long userId) {
        log.info("Invoke getUserById method.");
        User user = getUserByUserId(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found by userId : " + userId);
        }
        DepartmentDTO departmentDTO = restTemplate.getForObject("http://localhost:8081/api/v1/department/" + user.getDepartmentId(), DepartmentDTO.class);
        UserDTO userDTO = UserDTO.buildDTO(user);
        userDTO.setDepartment(departmentDTO);
        log.info("End getUserById method.");
        return userDTO;
    }

    private void validateUpdateRequest(UserDTO userDTO, Long userId) {
        if (userDTO == null) {
            throw new InvalidRequestException("Request is null.");
        }
        if (!Objects.equals(userDTO.getId(), userId)) {
            throw new InvalidRequestException("DepartmentId is not match with request.");
        }
        User user = getUserByUserId(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found by userId : " + userId);
        }
    }

    private User getUserByUserId(Long userId) {
        log.info("Invoke getUserByUserId method by userId : {}", userId);
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElse(null);
    }
}
