package com.sodeca.gestionimmo.enums;
public enum StatutIntervention {
    EN_ATTENTE {
        @Override
        public boolean canTransitionTo(StatutIntervention newStatut) {
            return newStatut == EN_COURS;
        }
    },
    EN_COURS {
        @Override
        public boolean canTransitionTo(StatutIntervention newStatut) {
            return newStatut == TERMINEE;
        }
    },
    TERMINEE;

    public boolean canTransitionTo(StatutIntervention newStatut) {
        return false;
    }
}
