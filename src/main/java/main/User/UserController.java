package main.User;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){this.userService = userService;}

    @GetMapping(path="users")
    public String getAllUsers(
            @RequestParam Integer batch,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String login,
            @RequestParam(required = false) String privilege
    ){
        return userService.findUsers(name,login,privilege,batch).toString();
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

    @PutMapping(path = "user/modify")
    public ResponseEntity<UserEntity> updateUser(@RequestParam Long userId,
                           @RequestParam(required = false) String login,
                           @RequestParam(required = false) String password,
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) String privilege){
        UserEntity user = userService.updateUser(userId,login,name,password,privilege);
        return ResponseEntity.ok(user);
    }

    @PostMapping(path = "user/add")
    public ResponseEntity<String> addUser(@RequestBody UserEntity user){
        JSONObject response = userService.addNewUser(user);
        String status = response.getString("status");
        System.out.println(status);
        if(Objects.equals(status, "SUCCESS")){
            return ResponseEntity.ok(response.toString());
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response.toString());
        }
    }
}
