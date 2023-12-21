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
        Map<String, Object> data = new HashMap<String, Object>();
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
            data.put("token",generatedString);
            data.put("privilege",newUser.getPrivilege());
            data.put("name",newUser.getName());
            response.put("data", data);
            response.put("status","success");
            response.put("message","Login success");
        }else {
            response.put("status","data-not-found-0025");
            response.put("message","Incorrect login or password");
        }
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity<Object> findUsers(String name, String login, String privilege, Integer batch, String token) {
        List<UserEntity> users;
        Optional<UserEntity> maybeAskingUser = userRepository.findUserEntityByToken(token);
        Map<String, Object> response = new HashMap<>();

        if(maybeAskingUser.isPresent()) {
            UserEntity askingUser = maybeAskingUser.get();
            if(askingUser.getPrivilege().equals("Admin")) {
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
                int size = (users.size() - 1) / 10 + 1;

                Map<String, Object> data = new HashMap<>();
                data.put("size", size);
                data.put("users", users.subList(from, to));
                response.put("data", data);
                response.put("status", "success");
                response.put("message", "Dane przekazane poprawnie");
            }
            else{
                response.put("status", "privilige-too-low-0001");
                response.put("message", "Brak wystarczających uprawnień do wyświetlenia listy użytkowników");
            }
        }
        else{
            response.put("status", "data-not-found-0026");
            response.put("message", "Brak użytkownika o podanym tokenie w bazie danych");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> updatePassword(String token, String newPassword, String oldPassword) {
        Map<String, Object> response = new HashMap<>();

        Optional<UserEntity> maybeUser = userRepository.findUserEntityByToken(token);
        if(maybeUser.isEmpty()){
            response.put("status", "data-not-found-0020");
            response.put("message", "Brak użytkownika o podanym tokenie w bazie danych");
        }
        else if(newPassword != null && oldPassword != null){
            UserEntity user = maybeUser.get();
            if (Objects.equals(user.getPassword(), oldPassword)) {
                user.setPassword(newPassword);
                response.put("status", "success");
                response.put("message", "Hasło zostało zmienione");
            } else {
                response.put("status", "conflict-0012");
                response.put("message", "Podano nieprawidłowe stare hasło");
            }
        }
        else{
            response.put("status", "conflict-0013");
            response.put("message", "Proszę uzupełnić wszystkie wymagane pola");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> updateUser(Long userId, String login, String name, String password, String privilege, String token) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> maybeAskingUser = userRepository.findUserEntityByToken(token);


        if(maybeAskingUser.isPresent()) {
            UserEntity askingUser = maybeAskingUser.get();
            if (askingUser.getPrivilege().equals("Admin")) {
                Optional<UserEntity> maybeUserById = userRepository.findById(userId);
                if (maybeUserById.isEmpty()) {
                    response.put("status", "data-not-found-0021");
                    response.put("message", "Użytkownik o id " + userId + " nie istnieje w bazie danych");
                } else {
                    UserEntity userById = maybeUserById.get();
                    boolean modifyFlag = false;
                    boolean loginRepeatedFlag = false;
                    if (login != null && !Objects.equals(login, userById.getLogin())) {
                        Optional<UserEntity> userByLogin = userRepository.findUserEntityByLogin(login);
                        if(userByLogin.isPresent()){
                            loginRepeatedFlag = true;
                        } else {
                            userById.setLogin(login);
                            modifyFlag = true;
                        }
                    }

                    if (name != null && !Objects.equals(name, userById.getName()) && !loginRepeatedFlag) {
                        userById.setName(name);
                        modifyFlag = true;
                    }

                    if (password != null && !Objects.equals(password, userById.getPassword()) && !loginRepeatedFlag) {
                        userById.setPassword(password);
                        modifyFlag = true;
                    }

                    if (privilege != null && !Objects.equals(privilege, userById.getPrivilege()) && !loginRepeatedFlag) {
                        userById.setPrivilege(privilege);
                        modifyFlag = true;
                    }

                    if(loginRepeatedFlag){
                        response.put("status", "conflict-0019");
                        response.put("message", "Podany login jest już zajęty");
                    }
                    else if (!modifyFlag) {
                        response.put("status", "conflict-0014");
                        response.put("message", "Żadna wartość nie została zmieniona");
                    } else {
                        response.put("status", "success");
                        response.put("message", "Dane użytkownika zostały pomyślnie zmienione");
                    }
                }
            } else {
                response.put("status", "privilige-too-low-0003");
                response.put("message", "Brak wystarczających uprawnień aby zaktualizować dane użytkownika");
            }
        } else {
            response.put("status", "data-not-found-0028");
            response.put("message", "Brak użytkownika o podanym tokenie w bazie danych");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Object> addNewUser(String token, UserEntity user) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> maybeAskingUser = userRepository.findUserEntityByToken(token);

        if(maybeAskingUser.isPresent()) {
            UserEntity askingUser = maybeAskingUser.get();
            if(askingUser.getPrivilege().equals("Admin")) {
                Optional<UserEntity> userByLogin = userRepository.findUserEntityByLogin(user.getLogin());
                if (user.getLogin() != null && user.getName() != null && user.getPassword() != null && user.getPrivilege() != null) {
                    if (userByLogin.isPresent()) {
                        response.put("status", "conflict-0015");
                        response.put("message", "Użytkownik w loginem " + user.getLogin() + " już istnieje w bazie danych");
                    } else {
                        userRepository.save(user);
                        response.put("status", "success");
                        response.put("message", "Użytkownik dodany do bazy");
                    }
                } else {
                    response.put("status", "conflict-0016");
                    response.put("message", "Proszę uzupełnić wszystkie wymagane pola");
                }
            }
            else{
                response.put("status", "privilige-too-low-0002");
                response.put("message", "Brak wystarczających uprawnień aby dodać nowego użytkownika");
            }
        }
        else{
            response.put("status", "data-not-found-0027");
            response.put("message", "Brak użytkownika o podanym tokenie w bazie danych");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    public ResponseEntity<Object> deleteUser(String token, Long userId) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> maybeAskingUser = userRepository.findUserEntityByToken(token);

        if(maybeAskingUser.isPresent()) {
            UserEntity askingUser = maybeAskingUser.get();
            if(askingUser.getPrivilege().equals("Admin")) {
                Optional<UserEntity> user = userRepository.findById(userId);
                if (user.isPresent()) {
                    userRepository.deleteById(userId);
                    response.put("status", "success");
                    response.put("message", "Użytkownik został usunięty");
                } else {
                    response.put("status", "data-not-found-0022");
                    response.put("message", "Użytkownik o numerze ID " + userId + " nie istnieje w bazie danych");
                }
            } else{
                response.put("status", "privilige-too-low-0004");
                response.put("message", "Brak wystarczających uprawnień do usuwania użytkowników");
            }
        }
        else{
            response.put("status", "data-not-found-0029");
            response.put("message", "Brak użytkownika o podanym tokenie w bazie danych");
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
            response.put("status", "data-not-found-0023");
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
            response.put("status", "data-not-found-0024");
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
                    response.put("status","conflict-0017");
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
                response.put("status","conflict-0018");
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
