package com.annofi.ims.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RestMessage {

    private String title;

    @JsonProperty("http_status")
    private int httpStatus;

    private String detail;

    @JsonProperty("time_stamp")
    private long timeStamp;

    private String path;

    @JsonProperty("developer_message")
    private String developerMessage;


}
