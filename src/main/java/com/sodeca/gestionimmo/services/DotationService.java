    package com.sodeca.gestionimmo.services;

    import com.sodeca.gestionimmo.dto.DotationDTO;
    import com.sodeca.gestionimmo.entity.Personnel;
    import com.sodeca.gestionimmo.entity.PieceDetachee;

    import java.time.LocalDate;
    import java.util.List;

    public interface DotationService {
        DotationDTO createDotation(DotationDTO dto);
        List<DotationDTO> getDotationsByTechnicien(Long technicienId);
        List<DotationDTO> getDotationsByPiece(Long pieceId);

        List<DotationDTO> getAllDotations();

        List<DotationDTO> getDotationsByDateRange(LocalDate startDate, LocalDate endDate);
    }
