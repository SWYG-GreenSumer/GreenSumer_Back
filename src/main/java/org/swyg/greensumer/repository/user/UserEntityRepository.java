package org.swyg.greensumer.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.UserEntity;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);
}
