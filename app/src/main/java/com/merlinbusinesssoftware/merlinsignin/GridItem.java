package com.merlinbusinesssoftware.merlinsignin;

/**
 * Created by aroras on 03/07/16.
 */
public class GridItem {
    private String image            = "";
    private String title            = "";
    private String staff_id         = "";
    private String signin_time      = "";
    private String signout_time     = "";
    private String status           = "";
    private String lastActivity     = "";
    private int    primaryId        = 0;
    private int    department_code  = 0;


    public GridItem() {
        super();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStaffId(){
        return staff_id;
    }

    public void setStaffId(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getSignin_time() {
        return signin_time;
    }

    public void setSignin_time(String signintime) {
        this.signin_time = signintime;
    }

    public String getSignout_time() {return signout_time;}

    public void setSignout_time(String signouttime) {
        this.signout_time = signouttime;
    }

    public int getDepartment_code() {
        return department_code;
    }

    public void setDepartment_code(Integer departmentcode) { this.department_code = departmentcode;}

    public String getStatus() { return status;}

    public void setStatus(String status) { this.status = status;}

    public Integer getPrimaryId() { return primaryId;}

    public void setPrimaryId(int primaryId) { this.primaryId = primaryId;}

    public String getLastActivity() { return lastActivity;}

    public void setLastActivity(String lastActivity) { this.lastActivity = lastActivity;}

}