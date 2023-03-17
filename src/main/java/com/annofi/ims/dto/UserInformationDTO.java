package com.annofi.ims.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class UserInformationDTO implements Serializable {

    private String username;

    private String name;

    private String emailId;

    private String gender;

    private String employeeCode;

    private String designation;

    private String hq;
}
