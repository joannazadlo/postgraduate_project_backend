package io.github.joannazadlo.recipedash.repository;

import io.github.joannazadlo.recipedash.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
