package main.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){this.userService = userService;}

    @GetMapping(path="user")
    public List<UserEntity> getAllUsers(){
        return userService.getUsers();
    }

    @GetMapping(path = "login")
    public Optional<UserEntity> login(@RequestParam("login") String login, @RequestParam("password") String password){
        return userService.loginUser(login,password);
    }
}
