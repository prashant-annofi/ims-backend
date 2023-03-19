package com.annofi.ims.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Entity
@Data
@Table(name = "roles",
uniqueConstraints = { @UniqueConstraint(name = "UniqueName", columnNames = { "name"}) })
public class Role {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Short id;

	private String name;
	
	@JsonProperty("operations")
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "role_operations",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name="operation_id"))
    private Set<Operation> operations;
}
