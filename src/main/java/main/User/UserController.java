package main.User;

import io.swagger.annotations.ResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){this.userService = userService;}

    @GetMapping(path="users")
    public List<UserEntity> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String login,
            @RequestParam(required = false) String privilege
    ){
        return userService.findUsers(name,login,privilege);
    }

    @GetMapping(path = "login")
    public Optional<UserEntity> login(@RequestParam("login") String login, @RequestParam("password") String password){
        return userService.loginUser(login,password);
    }



    @PutMapping(path = "user/setPassword")

    public void updatePassword(
            @RequestHeader String newPassword,
            @RequestHeader String oldPassword,
            @RequestHeader Long userId
    ) {
       userService.updatePassword(userId,newPassword,oldPassword);
    }
}
