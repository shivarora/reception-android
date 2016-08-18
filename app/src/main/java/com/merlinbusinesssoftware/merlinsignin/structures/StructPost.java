package com.merlinbusinesssoftware.merlinsignin.structures;

/**
 * Created by aroras on 24/05/16.
 */


    import java.util.Date;
    import java.util.List;

    import com.google.gson.annotations.SerializedName;

    public class StructPost {

        public int id;
        public String type;
        public String accountid;
        public String accountname;
        public String contactname;
        public int employeeid;
        public String employeename;
        public String vehiclereg;
        public String settime;
        public int reclogid;
        public int logid;
        public int pendingid;
        public String imagepath;
        public int contactid;
        public String signout;

        public List tags;

        public StructPost() {

        }
    }
//
//    //Tag.java
//    public class Tag {
//
//        public String name;
//        public String url;
//
//        public Tag() {
//
//        }
//    }

