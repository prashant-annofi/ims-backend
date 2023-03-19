package com.annofi.ims.dto.search;

import lombok.Data;

@Data
public class SearchDTO {
    private String field;
    private String value;
    private String operator;
}
