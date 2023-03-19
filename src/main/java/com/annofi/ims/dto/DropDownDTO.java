package com.annofi.ims.dto;

import lombok.Data;

@Data
public class DropDownDTO {
    private Long id;
    private String name;

    public DropDownDTO(){};
    public DropDownDTO(Long id){
        this.id = id;
    }
    public DropDownDTO(Long id, String name){
        this.id = id;
        this.name = name;
    }
}