package com.kelompok.moodflow.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * PILAR PBO: ABSTRACTION & INHERITANCE
 * MappedSuperclass memastikan atribut di kelas ini diturunkan ke tabel subclass di database.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    // PILAR PBO: ENCAPSULATION (Atribut private, diakses via Getter/Setter bawaan Lombok)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // PILAR PBO: ABSTRACTION (Method abstrak yang WAJIB diimplementasikan oleh subclass)
    public abstract String getEntitySummary();
}