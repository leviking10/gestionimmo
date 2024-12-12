package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.entity.Amortissement;
import com.sodeca.gestionimmo.entity.Immobilisation;

import java.util.List;
import java.util.Optional;

public interface AmortissementStrategy {
    List<Amortissement> calculerAmortissements(Immobilisation immobilisation, Optional<Amortissement> dernierAmortissement);
}
