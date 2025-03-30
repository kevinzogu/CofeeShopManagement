package al.sda.CofeeShopManagement.enums;

public enum Roles {
    ADMIN("ADMIN","Administrator","ROLE_ADMIN"),
    MANAGER("MANAGER","Manager" ,"ROLE_MANAGER"),
    BARISTA ("BARISTA","Barista" ,"ROLE_BARISTA"),;
    
    private String name;
    private String Description;
    private String RoleName;
    
    Roles(String name, String description,String roleName) {
        this.name = name;
        this.Description = description;
        this.RoleName = roleName;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return Description;
    }
    
    public String getRoleName() {
        return RoleName;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        Description = description;
    }
    
    public void setRoleName(String roleName) {
        RoleName = roleName;
    }
}
