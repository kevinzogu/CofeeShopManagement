package al.sda.CofeeShopManagement.service;

import al.sda.CofeeShopManagement.entity.User;
import al.sda.CofeeShopManagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    private void dbg(String s) {
        logger.info("CustomUserDetailsService =>  {}", s);
    }
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        dbg("loadUserByUsername with username: " + username);
        User user = userRepository.findByUsernameAndStatus(username,true).orElse(null);
        
        if (user == null) {
            dbg("User not found or Inactive: " + username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
   
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
    
}

