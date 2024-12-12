package com.sodeca.gestionimmo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "personnel")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Personnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
@Column
private String matricule;
    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String telephone;

    @Column
    private String poste; // Ex : Responsable IT, Technicien, etc.

    @Column
    private String departement; // Ex : IT, Production, Administration

    @Column
    private String adresse;
    @Column
    private String localite;
    @Column
    private  String service;
    @Column(unique = true)
    private String keycloakUserId; // ID de l'utilisateur dans Keycloak

    @Column
    private boolean actif = true; // Statut actif ou inactif
}
