package com.annofi.ims.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchResultDTO {
	
    @JsonProperty("s_no")
    private Long SNo;
}
