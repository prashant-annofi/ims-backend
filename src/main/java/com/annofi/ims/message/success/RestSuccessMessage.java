package com.annofi.ims.message.success;

import com.annofi.ims.message.RestMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class RestSuccessMessage extends RestMessage {

    @JsonProperty("messages")
    private List<SuccessMessage> successMessageList;

    public RestSuccessMessage(String message)
    {
        this(new String[]{message});
    }
    
    public RestSuccessMessage(String[] messages)
    {
       setTimeStamp(new Date().getTime());
       setHttpStatus(HttpStatus.OK.value());
       setTitle("Success");
       setDetail("Successful");
       setDeveloperMessage("Successful");
       setPath("");

       successMessageList = new ArrayList<>();
       for(String message:messages)
       {
           SuccessMessage successMessage = new SuccessMessage(message);
           successMessageList.add(successMessage);
       }
    }
}
