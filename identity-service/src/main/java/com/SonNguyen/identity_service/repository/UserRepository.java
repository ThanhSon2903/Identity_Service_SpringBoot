package com.SonNguyen.identity_service.repository;

import com.SonNguyen.identity_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,String> {
    public boolean existsByUserName(String userName);
    Optional<User> findByUserName(String userName);
}
//Optional<User> có nghĩa là phương thức này sẽ trả về
// một đối tượng User nếu tìm thấy người dùng, và nếu không tìm thấy, nó sẽ trả về Optional.empty().
