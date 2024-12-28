package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.entity.Planification;
import com.sodeca.gestionimmo.entity.Signalement;
import com.sodeca.gestionimmo.enums.EtatImmobilisation;
import com.sodeca.gestionimmo.enums.StatutPlanification;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    public boolean canTransitionSignalementToPlanification(Signalement signalement) {
        // Vérifiez les conditions pour transformer un signalement en planification
        return signalement.getImmobilisation().getEtatImmo() == EtatImmobilisation.SIGNALE;
    }

    public boolean canTransitionPlanificationToIntervention(Planification planification) {
        // Vérifiez si la planification peut devenir une intervention
        return planification.getStatut() == StatutPlanification.TERMINE;
    }
}
