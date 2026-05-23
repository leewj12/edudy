package com.kosmo.backend.attendance.holiday.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "OpenAPI_ServiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class HolidayWrapperResponse {

    @XmlElement(name = "response")
    private HolidayResponse response;
//    @XmlElement(name = "cmmMsgHeader")
//    private Header header;
//
//    @XmlElement(name = "body")
//    private HolidayResponse.Body body;
}