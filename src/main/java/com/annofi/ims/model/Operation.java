package com.annofi.ims.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@Data
@Table(name = "operations",
uniqueConstraints = { @UniqueConstraint(name = "UniqueName", columnNames = { "name"}) })
public class Operation {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Short id;
	
	private String name;
	
	private String module;
	
	private String description;
}
