package main.User;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "searchUsers")
    public ResponseEntity<Object> getAllUsers(
            @RequestParam Integer batch,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String login,
            @RequestParam(required = false) String privilege
    ) {
        return userService.findUsers(name, login, privilege, batch);
    }

    @GetMapping(path = "login")
    public ResponseEntity<Object> login(@RequestParam("login") String login, @RequestParam("password") String password) {
        return userService.loginUser(login,password);
    }

    @GetMapping(path = "getUserByToken")
    public ResponseEntity<Object> login(@RequestParam("token") String token) {
        return userService.getUser(token);
    }


    @PutMapping(path = "setPassword")
    public ResponseEntity<Object> updatePassword(
            @RequestHeader String newPassword,
            @RequestHeader String oldPassword,
            @RequestHeader Long userId
    ) {
        return userService.updatePassword(userId, newPassword, oldPassword);
    }

    @PutMapping(path = "modifyById")
    public ResponseEntity<Object> updateUser(@RequestParam Long userId,
                                                 @RequestParam(required = false) String login,
                                                 @RequestParam(required = false) String password,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String privilege) {
        return userService.updateUser(userId, login, name, password, privilege);
    }

    @PostMapping(path = "add")
    public ResponseEntity<Object> addUser(@RequestBody UserEntity user) {
        return userService.addNewUser(user);
    }

    @DeleteMapping(path = "deleteById")
    public ResponseEntity<Object> deleteUser(@RequestParam Long userId) {
        return userService.deleteUser(userId);
    }

    @DeleteMapping(path = "deleteByToken")
    public ResponseEntity<Object> deleteByToken(@RequestParam String token) {
        return userService.deleteUserById(token);
    }

    @PutMapping(path = "modifyByToken")
    public ResponseEntity<Object> updateAccount(@RequestParam String token, @RequestParam(required = false) String login, @RequestParam(required = false) String name){
        return userService.modifyUserByToken(token,login,name);
    }
}
