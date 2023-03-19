package com.annofi.ims.PageModel;

import lombok.Data;

@Data
public class PageModel {
    private Long length;
    private Long pageIndex;
    private Long totalPages;
    private Long pageSize;
}
