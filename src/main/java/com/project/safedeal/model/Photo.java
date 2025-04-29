package com.project.safedeal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

@Entity
@Table(name = "photos")
@Data
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    private String photo;
}