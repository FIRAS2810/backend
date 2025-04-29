package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.DemandeDemission;

public interface DemandeDemissionRepository  extends JpaRepository <DemandeDemission, Long> {

}
