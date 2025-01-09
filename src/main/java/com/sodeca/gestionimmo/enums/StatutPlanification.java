package com.sodeca.gestionimmo.enums;

public enum StatutPlanification {

    PLANIFIE {
        @Override
        public boolean canTransitionTo(StatutPlanification newStatut) {
            return newStatut == EN_COURS || newStatut == ANNULE;
        }
    },
    EN_COURS {
        @Override
        public boolean canTransitionTo(StatutPlanification newStatut) {
            return newStatut == TERMINE || newStatut == SUSPENDU;
        }
    },
    TERMINE, ANNULE, SUSPENDU;

    /**
     * Méthode par défaut pour les transitions de statut.
     * Par défaut, aucune transition n'est permise.
     *
     * @param newStatut le statut cible
     * @return false, car la transition est interdite par défaut
     */
    public boolean canTransitionTo(StatutPlanification newStatut) {
        return false;
    }
}
