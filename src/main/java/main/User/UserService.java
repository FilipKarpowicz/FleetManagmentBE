package main.User;

import jakarta.transaction.Transactional;
import main.Errand.Errand;
import main.Errand.ErrandRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<Object> loginUser(String login, String password) {
        Map<String, Object> response = new HashMap<String, Object>();
        Optional<UserEntity> optionalUserEntity = userRepository.loginUser(login, password);
        if(optionalUserEntity.isPresent()){
            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();
            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            UserEntity newUser = optionalUserEntity.get();
            newUser.setToken(generatedString);
            userRepository.save(newUser);
            response.put("token",generatedString);
            response.put("privilege",newUser.getPrivilege());
            response.put("name",newUser.getName());
            response.put("status","SUCCESS");
            response.put("message","Login succeed");
        }else {
            response.put("status","ERROR");
            response.put("message","User does not exist");
        }
        return new ResponseEntity<Object>(response, HttpStatus.OK);
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
        response.put("size", size);
        response.put("data", users.subList(from, to));
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
    public UserEntity updateUser(Long userId, String login, String name, String password, String privilege) {
        UserEntity userById = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("User with that id does not exist")
        );

        if (login != null && !Objects.equals(login, userById.getLogin())) {
            userById.setLogin(login);
        }

        if (name != null && !Objects.equals(name, userById.getName())) {
            userById.setName(name);
        }

        if (password != null && !Objects.equals(password, userById.getPassword())) {
            userById.setPassword(password);
        }

        if (privilege != null && !Objects.equals(privilege, userById.getPrivilege())) {
            userById.setPrivilege(privilege);
        }
        return userById;
    }

    public JSONObject addNewUser(UserEntity user) {
        Optional<UserEntity> userByLogin = userRepository.findUserEntityByLogin(user.getLogin());
        JSONObject response = new JSONObject();
        if (userByLogin.isPresent()) {
            response.put("status", "ERROR");
            response.put("message", "User with that login already exists");
        } else {
            userRepository.save(user);
            response.put("status", "SUCCESS");
            response.put("message", "User added");
        }
        return response;

    }

    public JSONObject deleteUser(Long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        JSONObject response = new JSONObject();
        if (user.isPresent()) {
            userRepository.deleteById(userId);
            response.put("status", "SUCCESS");
            response.put("message", "User deleted");
        } else {
            response.put("status", "ERROR");
            response.put("message", "User with that id does not exist");
        }
        return response;
    }
}
