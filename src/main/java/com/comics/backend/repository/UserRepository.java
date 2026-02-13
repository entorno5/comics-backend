
package com.comics.backend.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.comics.backend.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByMail(String mail);
}
