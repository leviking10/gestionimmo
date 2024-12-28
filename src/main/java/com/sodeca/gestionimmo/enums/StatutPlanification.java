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

    public boolean canTransitionTo(StatutPlanification newStatut) {
        return false;
    }
}
