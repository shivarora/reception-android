package com.merlinbusinesssoftware.merlinsignin;

/**
 * Created by aroras on 18/07/16.
 */
public class SearchItem {

        private String visitorName;
        private int id;
        private String image;


        public SearchItem() {
            super();
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getVisitorName() {
            return visitorName;
        }

        public void setVisitorName(String visitorName) {
            this.visitorName = visitorName;
        }

        public Integer getId(){
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

}
