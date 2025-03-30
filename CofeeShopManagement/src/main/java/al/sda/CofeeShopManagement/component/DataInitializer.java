package al.sda.CofeeShopManagement.component;

import al.sda.CofeeShopManagement.enums.Roles;
import al.sda.CofeeShopManagement.entity.User;
import al.sda.CofeeShopManagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private void dbg(String s) {
        logger.info("DataInitializer =>  {}", s);
    }
    
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) {
        dbg("Initializing data...");
        Stream.of(
                new User("admin1", "Admin", "1", "Admin1@sda.al", "069123456789", "Tirana", "01/01/2000",LocalDateTime.now(), List.of(Roles.ADMIN.getRoleName())),
                new User("admin2", "Admin", "2", "Admin2@sda.al", "0691234567891", "Tirana", "01/01/2000",LocalDateTime.now(), List.of(Roles.ADMIN.getRoleName()))
        ).forEach(this::createUserIfNotExists);
    }
    
    private void createUserIfNotExists(User user) {
        dbg("Creating user if not exists...");
        if (userRepository.findByUsername(user.getUsername()).isEmpty()) {
            dbg("User does not exist, creating...");
            user.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(user);
        }
    }
}
