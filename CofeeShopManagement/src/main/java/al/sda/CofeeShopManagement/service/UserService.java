package al.sda.CofeeShopManagement.service;

import al.sda.CofeeShopManagement.entity.User;
import al.sda.CofeeShopManagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class UserService {
    
    Logger logger = Logger.getLogger(this.getClass().getName());
    
    private void dbg(String message) {
        logger.info("UserService => " + message);
    }
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    
    public void updateUser(User user) {
        dbg("Inside updateUser method " + user);
        User userFromDB = userRepository.findById(user.getId()).get();
        
        dbg("User from dbg " + userFromDB);
        
        if (userFromDB != null) {
            dbg("Inside if userFromDB != null");
            dbg("Password : " + user.getPassword());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                dbg("Password is not null or empty");
                userFromDB.setPassword(passwordEncoder.encode(user.getPassword()));
                dbg("Setting password : " + userFromDB.getPassword());
            }
            dbg("Setting userFromDB properties");
            dbg("Setting Username : " + user.getUsername());
            userFromDB.setUsername(user.getUsername());
            dbg("Setting Name : " + user.getName());
            userFromDB.setName(user.getName());
            dbg("Setting Surname : " + user.getSurname());
            userFromDB.setSurname(user.getSurname());
            dbg("Setting Phone : " + user.getPhone());
            userFromDB.setPhone(user.getPhone());
            dbg("Setting Email : " + user.getEmail());
            userFromDB.setEmail(user.getEmail());
            dbg("Setting Address : " + user.getAddress());
            userFromDB.setAddress(user.getAddress());
            dbg("Setting birthDate : " + user.getBirthDate());
            userFromDB.setBirthDate(user.getBirthDate());
            dbg("Setting roles : " + user.getRoles());
            userFromDB.setRoles(new ArrayList<>(user.getRoles()));
            userFromDB.setLastLogin(LocalDateTime.now());
            userRepository.save(userFromDB);
        }
        
    }
    
    public List<User> findByRolesContaining(String roleName) {
        return userRepository.findByRolesContaining(roleName);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(new User());
    }
}
