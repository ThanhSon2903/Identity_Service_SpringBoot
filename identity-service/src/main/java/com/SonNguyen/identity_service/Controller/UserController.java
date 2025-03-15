package com.SonNguyen.identity_service.Controller;

import com.SonNguyen.identity_service.dto.request.ApiResponse;
import com.SonNguyen.identity_service.dto.request.UserCreationRequest;
import com.SonNguyen.identity_service.dto.request.UserUpdateRequest;
import com.SonNguyen.identity_service.dto.response.UserResponse;
import com.SonNguyen.identity_service.entity.User;
import com.SonNguyen.identity_service.exception.AppException;
import com.SonNguyen.identity_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController//Đánh dấu là lớp này sẽ xử lý các yêu cầu HTTP và trả về dưới dạng JSON or XML
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> creationUser(@RequestBody @Valid UserCreationRequest request){
        ApiResponse<UserResponse>apiResponse = new ApiResponse<>();
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>>getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username:{}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        List<UserResponse> users = userService.getUsers();
        if (users == null || users.isEmpty()) {
            System.out.println("⚠️ Không có user nào được trả về!");
        }
        return ApiResponse.<List<UserResponse>>builder()
                .result(users)
                .build();
    }

    @GetMapping("/debug/token")
    public ResponseEntity<String> checkToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        log.info("Token received: {}", authHeader);
        return ResponseEntity.ok(authHeader != null ? "Token exists" : "Token missing");
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUserById(@PathVariable("userId") String id){
        return ApiResponse.<UserResponse>builder()
                .result(userService.findUserById(id))
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUserById(@PathVariable("userId")String id, @RequestBody @Valid UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(id,request))
                .build();
    }

    @DeleteMapping("/{userId}")
    public String deleteUserById(@PathVariable("userId")String id){
        userService.deleteUserById(id);
        return "User has been deleted!";
    }
}