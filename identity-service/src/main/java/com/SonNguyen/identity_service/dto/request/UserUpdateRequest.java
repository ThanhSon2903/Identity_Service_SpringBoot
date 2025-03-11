package com.SonNguyen.identity_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder//Builder giúp bạn tạo đối tượng theo cách dễ đọc và linh hoạt mà không phải lo về thứ tự tham số.
@NoArgsConstructor//Tạo một constructor không tham số (default constructor) cho lớp
@AllArgsConstructor//Tạo một constructor có tham số cho tất cả các trường (fields) trong lớp
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String passWord;
    String firstName;
    String lastName;
    LocalDate date;
}
