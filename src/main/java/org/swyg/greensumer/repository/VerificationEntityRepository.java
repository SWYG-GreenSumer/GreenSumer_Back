package org.swyg.greensumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.VerificationEntity;

import java.util.Optional;

public interface VerificationEntityRepository extends JpaRepository<VerificationEntity, Integer> {

    Optional<VerificationEntity> findBySubject(String subject);

    void deleteById(Integer Id);
}
