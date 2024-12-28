package com.sodeca.gestionimmo.entity;

import com.sodeca.gestionimmo.enums.TypeMouvement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mouvements_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private PieceDetachee piece;

    @Column(nullable = false)
    private int quantite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMouvement typeMouvement;

    @Column(nullable = false)
    private LocalDateTime dateMouvement;

    @Column
    private String commentaire; @PrePersist
    public void adjustQuantite() {
        // Ajuste la quantité selon le type de mouvement
        if (typeMouvement == TypeMouvement.SORTIE) {
            quantite = -Math.abs(quantite); // Toujours négatif pour les sorties
        } else if (typeMouvement == TypeMouvement.ENTREE) {
            quantite = Math.abs(quantite); // Toujours positif pour les entrées
        }
    }
}