package com.aether.security.mapper;

import com.aether.security.pojo.entity.UserAccount;
import com.aether.security.pojo.vto.AuthView;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserMapper extends MongoRepository<UserAccount, String> {
    Optional<UserAccount> findByUsername(String username);

    Optional<UserAccount> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    default AuthView toView(UserAccount user) {
        return new AuthView(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getAvatarColor(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}



