package main.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    public static UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){this.userRepository = userRepository;}

    public Optional<UserEntity> loginUser(String login, String password){
        return userRepository.loginUser(login,password);
    }

    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }
}
