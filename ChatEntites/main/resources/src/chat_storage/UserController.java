package chat_storage;

import chat_enty.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @ResponseBody
    public String createUser(@RequestBody User user) {
        userService.createUser(user);
        return "User created successfully";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public User getUser(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user != null) {
            userService.deleteUser(user);
            return "User deleted successfully";
        }
        return "User not found";
    }
}
