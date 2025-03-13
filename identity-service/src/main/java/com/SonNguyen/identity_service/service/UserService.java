package com.SonNguyen.identity_service.service;

import com.SonNguyen.identity_service.dto.request.UserCreationRequest;
import com.SonNguyen.identity_service.dto.request.UserUpdateRequest;
import com.SonNguyen.identity_service.dto.response.UserResponse;
import com.SonNguyen.identity_service.entity.User;
import com.SonNguyen.identity_service.enums.Role;
import com.SonNguyen.identity_service.exception.AppException;
import com.SonNguyen.identity_service.exception.ErrorCode;
import com.SonNguyen.identity_service.mapper.UserMapper;
import com.SonNguyen.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request) {
        // Kiểm tra và xử lý người dùng giống như trước
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User newUser = userMapper.toUser(request);
        //mã hoá thông tin người dùng
        newUser.setPassWord(passwordEncoder.encode(request.getPassWord()));

        HashSet<String>roles = new HashSet<String>();
        roles.add(Role.USER.name());
        newUser.setRoles(roles);
        return userMapper.toUserResponse(userRepository.save(newUser));
    }

    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        if(userRepository==null){
            log.info("❌ userRepository is NULL!");
            return new ArrayList<>();
        }

        try{
            List<User> users = userRepository.findAll();
            log.info("✅ Fetched users from DB, size: {}", users.size());
            List<UserResponse> responses = users.stream()
                    .map(userMapper::toUserResponse)
                    .toList();
            log.info("✅ Converted users, size: {}", responses.size());
            return responses;
        } catch (Exception e) {
            log.error("🔥 Error fetching users: ", e);
            return new ArrayList<>();
        }
    }


    public UserResponse findUserById(String userId){
        log.info("In method get userById");
        return userMapper.toUserResponse(userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED)));
    }
    public UserResponse updateUser(String userId,UserUpdateRequest request){
        User user = userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("User not found!"));
        userMapper.updateUser(user,request);
        return userMapper.toUserResponse(userRepository.save(user));
    }
    public void deleteUserById(String userId){
        userRepository.deleteById(userId);
    }
}