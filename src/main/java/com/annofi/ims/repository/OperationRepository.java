package com.annofi.ims.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annofi.ims.model.Operation;

public interface OperationRepository extends JpaRepository<Operation, Long>{

}
