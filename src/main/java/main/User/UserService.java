package main.User;

import jakarta.transaction.Transactional;
import main.Errand.Errand;
import main.Errand.ErrandRepository;
import org.h2.engine.User;
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

    public ResponseEntity<Object> findUsers(String name, String login, String privilege, Integer batch) {
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
        int size = (users.size() - 1)/ 10 + 1;

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("size", size);
        data.put("users", users.subList(from, to));
        response.put("data", data);
        response.put("status", "success");
        response.put("message", "Dane przekazane poprawnie");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> updatePassword(Long userId, String newPassword, String oldPassword) {
        Map<String, Object> response = new HashMap<>();

        Optional<UserEntity> maybeUser = userRepository.findById(userId);
        if(maybeUser.isEmpty()){
            response.put("status", "record-not-found-0014");
            response.put("message", "Użytkownik o numerze ID " + userId + " nie istnieje w bazie danych");
        }
        else if(newPassword != null && oldPassword != null){
            UserEntity user = maybeUser.get();
            if (Objects.equals(user.getPassword(), oldPassword)) {
                user.setPassword(newPassword);
                response.put("status", "success");
                response.put("message", "Hasło zostało zmienione");
            } else {
                response.put("status", "conflict-0007");
                response.put("message", "Podano nieprawidłowe stare hasło");
            }
        }
        else{
            response.put("status", "conflict-0006");
            response.put("message", "Proszę uzupełnić wszystkie wymagane pola");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> updateUser(Long userId, String login, String name, String password, String privilege) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> maybeUserById = userRepository.findById(userId);

        if(maybeUserById.isEmpty()){
            response.put("status", "record-not-found-0013");
            response.put("message", "Użytkownik o id " + userId + " nie istnieje w bazie danych");
        }
        else {
            UserEntity userById = maybeUserById.get();
            boolean modifyFlag = false;
            if (login != null && !Objects.equals(login, userById.getLogin())) {
                userById.setLogin(login);
                modifyFlag = true;
            }

            if (name != null && !Objects.equals(name, userById.getName())) {
                userById.setName(name);
                modifyFlag = true;
            }

            if (password != null && !Objects.equals(password, userById.getPassword())) {
                userById.setPassword(password);
                modifyFlag = true;
            }

            if (privilege != null && !Objects.equals(privilege, userById.getPrivilege())) {
                userById.setPrivilege(privilege);
                modifyFlag = true;
            }

            if(!modifyFlag){
                response.put("status", "conflict-0009");
                response.put("message", "Żadna wartość nie została zmieniona");
            }
            else{
                response.put("status", "success");
                response.put("message", "Dane użytkownika zostały pomyślnie zmienione");
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Object> addNewUser(UserEntity user) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> userByLogin = userRepository.findUserEntityByLogin(user.getLogin());
        if(user.getLogin() != null && user.getName() != null && user.getPassword() != null && user.getPrivilege() != null) {
            if (userByLogin.isPresent()) {
                response.put("status", "conflict-0006");
                response.put("message", "Użytkownik w loginem " + user.getLogin() + " już istnieje w bazie danych");
            } else {
                userRepository.save(user);
                response.put("status", "success");
                response.put("message", "Użytkownik dodany do bazy");
            }
        }
        else{
            response.put("status", "conflict-0007");
            response.put("message", "Proszę uzupełnić wszystkie wymagane pola");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.deleteById(userId);
            response.put("status", "success");
            response.put("message", "Użytkownik został usunięty");
        } else {
            response.put("status", "record-not-found-0012");
            response.put("message", "Użytkownik o numerze ID " + userId + " nie istnieje w bazie danych");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Object> deleteUserById(String token) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> user = userRepository.findUserEntityByToken(token);
        if (user.isPresent()) {
            userRepository.deleteById(user.get().getUserId());
            response.put("status", "success");
            response.put("message", "Użytkownik został usunięty");
        } else {
            response.put("status", "record-not-found-0012");
            response.put("message", "Użytkownik nie istnieje w bazie danych");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Object> getUser(String token) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> user = userRepository.findUserEntityByToken(token);
        if (user.isPresent()) {
            response.put("status", "success");
            response.put("message", "Znaleziono użytkownika");
            response.put("data",user.get());
        } else {
            response.put("status", "record-not-found-0012");
            response.put("message", "Użytkownik nie istnieje w bazie danych");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> modifyUserByToken(String token, String login, String name) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByToken(token);
        boolean modifyFlag = false;
        boolean duplicateflag = false;
        if(optionalUserEntity.isEmpty()){
            response.put("status","record-not-found-0239");
            response.put("message","Twoje konto nie istnieje w bazie danych");
        }else{
            UserEntity user = optionalUserEntity.get();
            if(login != null && !Objects.equals(login,user.getLogin())){
                if(userRepository.findUserEntityByLogin(login).isPresent()){
                    duplicateflag = true;
                    response.put("status","data-not-changed-0002");
                    response.put("message","Podany login jest już zajęty.");
                }else{
                    user.setLogin(login);
                    modifyFlag = true;
                }
            }
            if(name != null && !Objects.equals(name,user.getName())){
                user.setName(name);
                modifyFlag = true;
            }
            if(!modifyFlag && !duplicateflag){
                response.put("status","data-not-changed-0001");
                response.put("message","Wprowadzono niepoprawne dane.");
            }else if(!duplicateflag){
                response.put("status","success");
                response.put("message","Dane uzytkownika zmienione!");
                response.put("data",user);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
