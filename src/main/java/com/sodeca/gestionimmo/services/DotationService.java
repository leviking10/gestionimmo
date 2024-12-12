package com.sodeca.gestionimmo.services;

import com.sodeca.gestionimmo.dto.DotationDTO;
import java.util.List;

public interface DotationService {
    DotationDTO createDotation(DotationDTO dto);
    List<DotationDTO> getDotationsByTechnicien(Long technicienId);
    List<DotationDTO> getDotationsByPiece(Long pieceId);
}
