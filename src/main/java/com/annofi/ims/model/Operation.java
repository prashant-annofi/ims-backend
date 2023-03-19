package com.annofi.ims.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "operations")
public class Operation {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Short id;
	
	private String name;
	
	private String module;
	
	private String description;
}
