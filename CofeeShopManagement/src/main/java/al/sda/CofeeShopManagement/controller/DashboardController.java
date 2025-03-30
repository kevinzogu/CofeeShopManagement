package al.sda.CofeeShopManagement.controller;

import al.sda.CofeeShopManagement.enums.Roles;
import al.sda.CofeeShopManagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.atomic.AtomicReference;

@Controller
public class DashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final UserRepository userRepository;
    
    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    private void dbg(String s) {
        logger.info("DashboardController =>  {}", s);
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        dbg("Inside dashboard");
        AtomicReference<String> role = new AtomicReference<>("");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        dbg("Authentication => " + authentication);
        
        if (authentication != null && authentication.isAuthenticated()) {
            dbg("Authentication is authenticated");
            org.springframework.security.core.userdetails.User user = (User) authentication.getPrincipal();
            dbg("user => " + user);
            model.addAttribute("username", user.getUsername());
            user.getAuthorities().forEach(a -> {
                if (a.getAuthority().equals(Roles.ADMIN.getRoleName())) {
                    role.set("admin");
                } else if (a.getAuthority().equals(Roles.BARISTA.getRoleName())) {
                    role.set("barista");
                } else if (a.getAuthority().equals(Roles.MANAGER.getRoleName())) {
                    role.set("manager");
                }
            });
        }
        
        if (role.get().equals("admin")) {
            return "redirect:/admin/dashboard";
        } else if (role.get().equals("barista")) {
            return "redirect:/barista/dashboard";
        } else if (role.get().equals("manager")) {
            return "redirect:/manager/dashboard";
        }
        return "dashboard";
    }
}