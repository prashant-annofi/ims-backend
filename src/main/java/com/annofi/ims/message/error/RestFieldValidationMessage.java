package com.annofi.ims.message.error;

import com.annofi.ims.message.RestMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;

@Data
public class RestFieldValidationMessage extends RestMessage {

    //@JsonProperty("messages")
    //private Map<String, List<FieldValidationErrorMessage>> errors = new HashMap<String, List<FieldValidationErrorMessage>>();

    @JsonProperty("messages")
    private List<FieldValidationErrorMessage> errors = new ArrayList<>();

}
