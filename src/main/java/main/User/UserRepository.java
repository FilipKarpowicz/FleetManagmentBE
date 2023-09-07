package main.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT s FROM UserEntity s WHERE s.login=?1 AND s.password=?2")
    Optional<UserEntity> loginUser(String login, String password);
}
