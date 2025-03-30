package al.sda.CofeeShopManagement.controller;

import al.sda.CofeeShopManagement.entity.User;
import al.sda.CofeeShopManagement.enums.Gender;
import al.sda.CofeeShopManagement.enums.Roles;
import al.sda.CofeeShopManagement.repository.UserRepository;
import al.sda.CofeeShopManagement.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    Logger logger = Logger.getLogger(this.getClass().getName());
    
    private void dbg(String message) {
        logger.info("AdminController => " + message);
    }
    
    private final UserService userService;
    
    public AdminController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/dashboard")
    public String getAdminDashboard() {
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findByRolesContaining(Roles.MANAGER.getRoleName());
        users.addAll(userService.findByRolesContaining(Roles.BARISTA.getRoleName()));
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user,
                                RedirectAttributes redirectAttributes) {
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("message", "User created successfully");
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        dbg("Inside updateUser method " + user);
        dbg("Going to call userService.updateUser");
        userService.updateUser(user);
        dbg("After calling userService.updateUser");
        redirectAttributes.addFlashAttribute("message", "User updated successfully");
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully");
        return "redirect:/admin/users";
    }
    
}
