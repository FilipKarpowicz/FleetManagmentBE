package main.User;

import jakarta.transaction.Transactional;
import org.json.JSONObject;
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

    public JSONObject findUsers(String name, String login, String privilege, Integer batch) {
        List<UserEntity> users;
        if (privilege != null) {
            if (name != null) {
                if (login != null) {
                    users = userRepository.findUserAll(login, name, privilege);
                } else {
                    //priv and name
                    users = userRepository.findUserPrivilegeName(name, privilege);
                }
            } else {
                if (login != null) {
                    //login and priv
                    users = userRepository.findUserPrivilegeLogin(login, privilege);
                } else {
                    //only priv
                    users = userRepository.findUserPrivilege(privilege);
                }
            }
        } else {
            if (name != null) {
                if (login != null) {
                    //name and login
                    users = userRepository.findUserLoginName(login, name);
                } else {
                    // name
                    users = userRepository.findUserName(name);
                }
            } else {
                if (login != null) {
                    //login
                    users = userRepository.findUserLogin(login);
                } else {
                    //all records
                    users = userRepository.findAllSorted();
                }
            }

        }
        int from = batch * 10 - 10;
        int to = Math.min(batch * 10, users.size());
        int size = users.size() / 10 + 1;
        JSONObject response = new JSONObject();
        response.put("size",size);
        response.put("data",users.subList(from,to));
        System.out.println(response);
        return response;
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

    public JSONObject addNewUser(UserEntity user) {
        Optional<UserEntity> userByLogin = userRepository.findUserEntityByLogin(user.getLogin());
        if(userByLogin.isPresent()){
            JSONObject response = new JSONObject();
            response.put("status","ERROR");
            response.put("message","User with that login already exists");
            return response;
        }else {
            userRepository.save(user);
            JSONObject response = new JSONObject();
            response.put("status","SUCCESS");
            response.put("message","User added");
            return response;
        }

    }
}
