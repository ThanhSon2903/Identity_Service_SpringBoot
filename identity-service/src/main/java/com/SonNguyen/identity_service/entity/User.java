package com.SonNguyen.identity_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String userName;
    String passWord;
    String firstName;
    String lastName;
    LocalDate date;

    @ElementCollection(fetch = FetchType.EAGER)  // Nếu roles là Set<String>
    Set<String> roles;
}
