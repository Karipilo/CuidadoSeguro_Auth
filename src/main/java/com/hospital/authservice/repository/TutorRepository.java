package com.hospital.authservice.repository;

import com.hospital.authservice.entity.Tutor;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface TutorRepository
        extends JpaRepository<Tutor, Long> {

}