package main.User;

import jakarta.transaction.Transactional;
import main.Driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    public static UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> loginUser(String login, String password) {
        return userRepository.loginUser(login, password);
    }

    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    public List<UserEntity> findUsers(String name, String login, String privilege) {
        if (privilege != null) {
            if (name != null) {
                if (login != null) {
                    return userRepository.findUserAll(login, name, privilege);
                } else {
                    //priv and name
                    return userRepository.findUserPrivilegeName(name, privilege);
                }
            } else {
                if (login != null) {
                    //login and priv
                    return userRepository.findUserPrivilegeLogin(login, privilege);
                } else {
                    //only priv
                    return userRepository.findUserPrivilege(privilege);
                }
            }
        } else {
            if (name != null) {
                if (login != null) {
                    //name and login
                    return userRepository.findUserLoginName(login, name);
                } else {
                    // name
                    return userRepository.findUserName(name);
                }
            } else {
                if (login != null) {
                    //login
                    return userRepository.findUserLogin(login);
                } else {
                    //all records
                    return userRepository.findAllSorted();
                }
            }

        }
    }

    @Transactional
    public void updatePassword(Long userId, String newPassword, String oldPassword) {
        System.out.println("update");
        UserEntity user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("User with this id doesnt exist")
        );
        if (Objects.equals(user.getPassword(), oldPassword)) {
            user.setPassword(newPassword);
        } else {
            throw new IllegalStateException("Incorrect password");
        }

    }

    @Transactional
    public UserEntity updateUser(Long userId, String login, String name, String password, String privilege){
        UserEntity userById = userRepository.findById(userId).orElseThrow(
                () ->new IllegalStateException("User with that id does not exist")
        );

        if( login != null && !Objects.equals(login,userById.getLogin())){
            userById.setLogin(login);
        }

        if( name != null && !Objects.equals(name,userById.getName())){
            userById.setName(name);
        }

        if( password != null && !Objects.equals(password,userById.getPassword())){
            userById.setPassword(password);
        }

        if( privilege != null && !Objects.equals(privilege,userById.getPrivilege())){
            userById.setPrivilege(privilege);
        }
    return userById;
    }

}
