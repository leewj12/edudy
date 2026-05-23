package com.kosmo.backend.attendance.holiday.dto;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class HolidayResponse {

    private Header header;
    private Body body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @Getter @Setter
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    public static class Body {
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;


        public Items getItems() {
            return items;
        }

        public void setItems(Items items) {
            this.items = items;
        }

        public int getNumOfRows() {
            return numOfRows;
        }

        public void setNumOfRows(int numOfRows) {
            this.numOfRows = numOfRows;
        }

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Items {
            @XmlElement(name = "item")  // 이 한 줄 추가!
            private List<Item> item;

            public List<Item> getItem() {
                return item;
            }

            public void setItem(List<Item> item) {
                this.item = item;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            public static class Item {
                private String dateKind;
                private String dateName;
                private String isHoliday;
                private String locdate;
                private String seq;
                private String remarks;

                // 선택적으로 getter/setter 모두 직접 작성해도 안정적입니다
                public String getLocdate() { return locdate; }
                public String getDateName() { return dateName; }
            }
        }
    }
}
