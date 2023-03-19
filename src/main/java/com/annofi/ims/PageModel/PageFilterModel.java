package com.annofi.ims.PageModel;

import com.annofi.ims.dto.search.SearchDTO;
import com.annofi.ims.dto.search.SortDTO;
import lombok.Data;

import java.util.List;

@Data
public class PageFilterModel {
    private PageModel pagination;
    private List<SearchDTO> filter;
    private List<SortDTO> sortDTO;
    private Long totalRowCount;
}
