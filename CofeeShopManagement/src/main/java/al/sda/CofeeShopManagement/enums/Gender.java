package al.sda.CofeeShopManagement.enums;

public enum Gender{
    M("Male")
    ,F("Female");
    
    private String description;
    
    private Gender(String description){
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
};
