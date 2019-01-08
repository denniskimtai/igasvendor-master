package codegreed_devs.com.igasvendor.models;

public class Vendor {
    private String uid;
    private String businessName;
    private String businessEmail;
    private String businessLocation;

    public Vendor(String uid, String name, String email, String location){
        this.uid = uid;
        this.businessName = name;
        this.businessEmail = email;
        this.businessLocation = location;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getBusinessLocation() {
        return businessLocation;
    }

    public void setBusinessLocation(String businessLocation) {
        this.businessLocation = businessLocation;
    }
}
