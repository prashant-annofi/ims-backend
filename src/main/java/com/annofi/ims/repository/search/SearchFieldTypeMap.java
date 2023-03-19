package com.annofi.ims.repository.search;

import java.util.HashMap;
import java.util.Map;

public class SearchFieldTypeMap {
    public static String typeEquals = "equals";
    public static String typeLike = "like";
    public static String typeBoolean = "boolean";
    public static String typeIn = "in";

    
    public static Map<String, String> fieldTypeMap =  new HashMap(){{
        put("id", typeEquals);
        put("employeeId", typeEquals);
        put("zoneId", typeEquals);
        put("stateId", typeEquals);
        put("hqId", typeEquals);
        put("subHqId", typeEquals);
        put("divisionId", typeEquals);
        put("productGroupId", typeEquals);
        put("productSegmentId", typeEquals);
        put("qualificationId", typeEquals);
        put("specializationId", typeEquals);
        put("categoryId", typeEquals);
        put("categoryDivisionId", typeEquals);
        put("status", typeEquals);
        put("designationId", typeEquals);
        put("reportingManager", typeEquals);
        put("territoryId", typeEquals);
        put("nepaliMonth", typeEquals);
        put("nepaliYear", typeEquals);
        put("numberOfDays", typeEquals);
        put("toCode", typeEquals);
        put("fromCode", typeEquals);
        put("managerId", typeEquals);
        put("accured",typeEquals);
        put("leaveTypeId",typeEquals);
        put("leaveType", typeEquals);
        put("subHqId", typeEquals);
        
        put("active", typeBoolean);
        put("approved", typeBoolean);
        put("isDeletedFrom", typeBoolean);
        put("isDeletedTo", typeBoolean);
        put("isSaved", typeBoolean);
        put("isDeletedFromTrash", typeBoolean);
        put("isDeletedToTrash", typeBoolean);
        put("isStatus",typeBoolean);
        
        put("name", typeLike);
        put("code", typeEquals);     //but should have been string instead of long.
        put("nmc_no", typeLike);
        put("category", typeLike);
        put("address", typeLike);
        put("website", typeLike);
        put("gender", typeLike);
        //put("status", typeLike);
        put("remarks", typeLike);
        put("statusName", typeLike);
        put("stationType", typeLike);
        put("shortName", typeLike);
        put("sequenceNumber", typeLike);
        put("type", typeLike);
        put("product_type", typeLike);
        put("username", typeLike);
        put("employeeName", typeLike);
        put("employeeCode", typeLike);
        put("subject", typeLike);
        put("dayType", typeLike);
//        put("status", typeLike);
    }};
}
