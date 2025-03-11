package com.SonNguyen.identity_service.dto.request;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)// Nếu chúng ta dùng annotation này thì mặc định các method = private
public class UserCreationRequest {

    @Size(min = 3,message = "USERNAME_INVALID")
    String userName;

    @Size(min = 3,message = "PASSWORD_INVALID")
    String passWord;

    String firstName;
    String lastName;
    LocalDate date;
}
