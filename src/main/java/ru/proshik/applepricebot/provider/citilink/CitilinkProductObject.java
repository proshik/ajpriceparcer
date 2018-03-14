package ru.proshik.applepricebot.provider.citilink;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CitilinkProductObject {

        private String id;
        private Integer categoryId;
        private Integer price;
        private String shortName;
        private String categoryName;
        private String brandName;

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public Integer getCategoryId() {
            return categoryId;
        }
        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }
        public Integer getPrice() {
            return price;
        }
        public void setPrice(Integer price) {
            this.price = price;
        }
        public String getShortName() {
            return shortName;
        }
        public void setShortName(String shortName) {
            this.shortName = shortName;
        }
        public String getCategoryName() {
            return categoryName;
        }
        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
        public String getBrandName() {
            return brandName;
        }
        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

}
