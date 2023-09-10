package main.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT s FROM UserEntity s WHERE s.login=?1 AND s.password=?2")
    Optional<UserEntity> loginUser(String login, String password);

    @Query("SELECT s FROM UserEntity s WHERE s.login LIKE %?1% AND " +
            "s.Name LIKE %?2% AND s.privilege=?3")
    List<UserEntity> findUserAll(String login, String name, String privilege);

    @Query("SELECT s FROM UserEntity s WHERE s.Name LIKE %?1% AND s.privilege=?2")
    List<UserEntity> findUserPrivilegeName(String name, String privilege);

    @Query("SELECT s FROM UserEntity s WHERE s.login LIKE %?1% AND s.privilege=?2")
    List<UserEntity> findUserPrivilegeLogin(String login, String privilege);

    @Query("SELECT s FROM UserEntity s WHERE s.login LIKE %?1% AND s.Name LIKE %?2%")
    List<UserEntity> findUserLoginName(String login, String name);

    @Query("SELECT s FROM UserEntity s WHERE s.privilege=?1")
    List<UserEntity> findUserPrivilege( String privilege);

    @Query("SELECT s FROM UserEntity s WHERE s.Name LIKE %?1%")
    List<UserEntity> findUserName( String name);

    @Query("SELECT s FROM UserEntity s WHERE s.login LIKE %?1%")
    List<UserEntity> findUserLogin(String login);


}
