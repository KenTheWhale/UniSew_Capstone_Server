package com.unisew.server.repositories;

import com.unisew.server.models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepo extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByAccountId(int accountId);
}
