package chat_storage;

import chat_enty.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
