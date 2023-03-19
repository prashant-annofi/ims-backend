package com.annofi.ims.repository.search;

import com.annofi.ims.PageModel.PageFilterModel;
import com.annofi.ims.dto.search.SearchDTO;
import com.annofi.ims.dto.search.SearchResultDTO;
import com.annofi.ims.dto.search.SortDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SearchRepository {

    public static String MAIN_JOIN = "mainJoin";

    @Autowired
    EntityManager em;

    public Page<SearchResultDTO> getSearchResult(PageFilterModel pageFilterModel)
    {
    	int pageSize = pageFilterModel.getPagination().getPageSize().intValue();
    	if(pageSize == 0) {
    		pageSize = pageFilterModel.getTotalRowCount().intValue();
    	}
    	
        Pageable page = PageRequest.of(pageFilterModel.getPagination().getPageIndex().intValue(), pageSize);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SearchResultDTO> cq = cb.createQuery(SearchResultDTO.class);
        Map<String,From> joins = processList(cq, cb);
        From mainJoin = joins.get(SearchRepository.MAIN_JOIN);
        
        List<Predicate> predicates = this.getPredicates(pageFilterModel, cb, joins, mainJoin);
        
        cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        
        try {
	        //if(pageFilterModel.getSortDTO() != null) {
	        if(!"MessageTo".equals(mainJoin.getJavaType().getSimpleName()) && !"TourProgram".equals(mainJoin.getJavaType().getSimpleName())) {
	        	SortDTO sortDTO = new SortDTO();
	    		sortDTO.setField("id");
	    		sortDTO.setOrderType("desc");
	    		if(pageFilterModel.getSortDTO() == null) {
	    			pageFilterModel.setSortDTO(new ArrayList<SortDTO>());
	    		}
	    		pageFilterModel.getSortDTO().add(sortDTO);
    		}
	    	
	            List<Order> orderFields = new ArrayList<>();
	            for (SortDTO item : pageFilterModel.getSortDTO()
	            ) {
	            	String fieldType = SearchFieldTypeMap.fieldTypeMap.get(item.getField());
	                String fieldName = item.getField();
		            From join = mainJoin;
	                
	                if(fieldName.contains("."))
	                {
	                    String[] data = fieldName.split("\\.");
	                    String joinName = data[0];
	                    fieldName = data[1];
	                    fieldType = SearchFieldTypeMap.fieldTypeMap.get(fieldName);
	                    join = joins.get(joinName);
	                }
	                
	                if("desc".equals(item.getOrderType())) {
	                    orderFields.add(cb.desc(join.get(fieldName)));
	                }
	                else if("asc".equals(item.getOrderType())){
	                    orderFields.add(cb.asc(join.get(fieldName)));
	                }
	            }
	            cq.orderBy(orderFields);
	        //}
	        /*else {
	        	String a = mainJoin.getJavaType().getSimpleName();
	        	if(!"TourProgram".equals(mainJoin.getJavaType().getSimpleName()) && !"MessageTo".equals(mainJoin.getJavaType().getSimpleName())) {
		        	List<Order> orderFields = new ArrayList<>();
	                orderFields.add(cb.desc(mainJoin.get("id")));
		            cq.orderBy(orderFields);
	        	}
	        }*/
        }
        catch (Exception e) {
			// TODO: handle exception
		}
        
        int totalRows = 0;
        if(pageFilterModel.getTotalRowCount() == null) {
        	totalRows = em.createQuery(cq).getResultList().size();
        }
        else {
        	totalRows = pageFilterModel.getTotalRowCount().intValue();
        }
        List<SearchResultDTO> searchedDtoList = em.createQuery(cq).setFirstResult(page.getPageNumber() * page.getPageSize()).setMaxResults(page.getPageSize()).getResultList();
        
        //TypedQuery<SearchResultDTO> a = em.createQuery(cq);
        //String b = a.unwrap(org.hibernate.Query.class).getQueryString();
        //For serial numbers
        Long count = new Long((page.getPageNumber()) * page.getPageSize())+1;
        for (SearchResultDTO item : searchedDtoList) 
        {
            item.setSNo(count);
            count++;
        }
        return new PageImpl<SearchResultDTO>(searchedDtoList, page, totalRows);
    }

    public Long getSearchResultCount(PageFilterModel pageFilterModel) {
		try {
	    	CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<Long> cqInt = cb.createQuery(Long.class);
	        Map<String,From> joins = processListCount(cqInt,cb);
	        From mainJoin = joins.get(SearchRepository.MAIN_JOIN);
	
	        List<Predicate> predicates = getPredicates(pageFilterModel, cb, joins, mainJoin);
		
	        cqInt.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
			cqInt.select(cb.countDistinct(mainJoin.get("id")));
	    	TypedQuery<Long> typedQuery = em.createQuery(cqInt);
	    	Long a = typedQuery.getSingleResult();
	    	
	    	return a;
		}
		catch (Exception e) {
			return 0L;
		}
    }
    
    private List<Predicate> getPredicates(PageFilterModel pageFilterModel,CriteriaBuilder cb, Map<String, From> joins, From mainJoin) {
        List<Predicate> predicates = new ArrayList<>();
        try {
        //if(mainJoin.get("deleted")!=null){
            predicates.add(cb.equal(mainJoin.get("deleted"),false));
        //}
        }
        finally {
	        if(pageFilterModel.getFilter() != null)
	        {
	            for (SearchDTO item : pageFilterModel.getFilter()) {
	                String fieldType = SearchFieldTypeMap.fieldTypeMap.get(item.getField());
	                String fieldName = item.getField();
	                String operator = item.getOperator();
	                From join = mainJoin;
	                
	                if(fieldName.contains("."))
	                {
	                    String[] data = fieldName.split("\\.");
	                    String joinName = data[0];
	                    fieldName = data[1];
	                    fieldType = SearchFieldTypeMap.fieldTypeMap.get(fieldName);
	                    join = joins.get(joinName);
	                }
	                
	                if(operator == null)
	                {
	                	operator = fieldType;
	                }
	                
                	//TODO remove logic to search in SearchFieldTypeMap
                	if(operator != null) {
		                switch(operator) {
	                		case "equals":
	                		{
	                			if(!"".equals(item.getValue())) {
			                		if(item.getValue().contains(",")) {
			                			List<Long> values = new ArrayList<Long>();
			                			values = Stream.of(item.getValue().split(",")).map(Long::parseLong).collect(Collectors.toList());
			                			predicates.add(cb.in(join.get(fieldName)).value(values));
			                		}
			                		else if("notEquals".equals(item.getOperator())) {
			                			predicates.add(cb.notEqual(join.get(fieldName), item.getValue()));
			                		}
			                		else {
			                			predicates.add(cb.equal(join.get(fieldName), item.getValue()));
			                		}
			                	}
	                			break;
	                		}
	                		case "like":
	                		{
	                			predicates.add(cb.like(join.get(fieldName), "%" + item.getValue() + "%"));
	                			break;
	                		}
	                		case "boolean":
	                		{
	                			if("0".equals(item.getValue())){
			                        predicates.add(cb.equal(join.get(fieldName), false));
			                    }
			                    else if("1".equals(item.getValue())){
			                        predicates.add(cb.equal(join.get(fieldName), true));
			                    }
	                			break;
	                		}
	                		case "in":
	                		{
	                			if("String".equals(join.get(fieldName).getJavaType().getSimpleName())){
			                		predicates.add(join.get(fieldName).in(Stream.of(item.getValue().split(",")).collect(Collectors.toList())));
			                	}
			                	else if("boolean".equals(join.get(fieldName).getJavaType().getSimpleName().toLowerCase())){
			                		predicates.add(join.get(fieldName).in(Stream.of(item.getValue().split(",")).map(Boolean::parseBoolean).collect(Collectors.toList())));
			                	}
			                	else {
			                		predicates.add(join.get(fieldName).in(Stream.of(item.getValue().split(",")).map(Long::parseLong).collect(Collectors.toList())));
			                	}
	                			break;
	                		}
	                		case "notEquals":
	                		{
	                			predicates.add(cb.notEqual(join.get(fieldName), item.getValue()));
	                			break;
	                		}
	                	}
                	}
	            }
	        }
	        return predicates;
        }
    }

    protected abstract Map<String,From> processList( CriteriaQuery<SearchResultDTO> cq, CriteriaBuilder cb);
    protected abstract Map<String,From> processListCount( CriteriaQuery<Long> cq, CriteriaBuilder cb);
   
}
