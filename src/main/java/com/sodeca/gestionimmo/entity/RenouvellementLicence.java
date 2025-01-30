package com.sodeca.gestionimmo.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "renouvellements_licences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RenouvellementLicence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "licence_id", nullable = false)
    private Licence licence; // Licence concernée

    @Column(nullable = false)
    private LocalDate dateRenouvellement; // Date du renouvellement

    @Column(nullable = false)
    private int quantiteAjoutee; // Nombre de licences ajoutées lors du renouvellement

    @Column(nullable = false)
    private double coutRenouvellement; // Coût total du renouvellement
}
