package com.sodeca.gestionimmo.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sodeca.gestionimmo.dto.PieceDetacheeDTO;
import com.sodeca.gestionimmo.entity.PieceDetachee;
import com.sodeca.gestionimmo.exceptions.BusinessException;
import com.sodeca.gestionimmo.mapper.PieceDetacheeMapper;
import com.sodeca.gestionimmo.repository.PieceDetacheeRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class PieceImportService {

    private final PieceDetacheeRepository pieceRepository;
    private final PieceDetacheeMapper pieceMapper;

    public PieceImportService(PieceDetacheeRepository pieceRepository, PieceDetacheeMapper pieceMapper) {
        this.pieceRepository = pieceRepository;
        this.pieceMapper = pieceMapper;
    }

    public List<PieceDetacheeDTO> importPieces(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename().toLowerCase();

        if (isExcelFile(fileName)) {
            return processExcelFile(file);
        } else if (isCsvFile(fileName)) {
            return processCsvFile(file);
        } else {
            throw new BusinessException("Format de fichier non supporté. Veuillez uploader un fichier Excel ou CSV.");
        }
    }

    private boolean isExcelFile(String fileName) {
        return fileName.endsWith(".xlsx") || fileName.endsWith(".xls");
    }

    private boolean isCsvFile(String fileName) {
        return fileName.endsWith(".csv");
    }

    private List<PieceDetacheeDTO> processExcelFile(MultipartFile file) throws IOException {
        List<PieceDetacheeDTO> pieces = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            try {
                PieceDetacheeDTO pieceDTO = mapExcelRowToPieceDTO(row);
                PieceDetachee pieceEntity = pieceMapper.toEntity(pieceDTO);
                PieceDetachee savedPiece = pieceRepository.save(pieceEntity);
                pieces.add(pieceMapper.toDTO(savedPiece));
            } catch (Exception e) {
                throw new BusinessException("Erreur ligne " + row.getRowNum() + ": " + e.getMessage());
            }
        }
        workbook.close();
        return pieces;
    }

    private List<PieceDetacheeDTO> processCsvFile(MultipartFile file) throws IOException {
        List<PieceDetacheeDTO> pieces = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            boolean isHeader = true;

            while ((values = csvReader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                PieceDetacheeDTO pieceDTO = mapCsvRowToPieceDTO(values);
                PieceDetachee pieceEntity = pieceMapper.toEntity(pieceDTO);
                PieceDetachee savedPiece = pieceRepository.save(pieceEntity);
                pieces.add(pieceMapper.toDTO(savedPiece));
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException("Erreur validation CSV.", e);
        }
        return pieces;
    }

    private PieceDetacheeDTO mapExcelRowToPieceDTO(Row row) {
        PieceDetacheeDTO dto = new PieceDetacheeDTO();
        dto.setReference(getCellValue(row.getCell(0)));
        dto.setNom(getCellValue(row.getCell(1)));
        dto.setStockDisponible(Integer.parseInt(getCellValue(row.getCell(2))));
        dto.setStockMinimum(Integer.parseInt(getCellValue(row.getCell(3))));
        dto.setDescription(getCellValue(row.getCell(4)));
        return dto;
    }

    private PieceDetacheeDTO mapCsvRowToPieceDTO(String[] values) {
        PieceDetacheeDTO dto = new PieceDetacheeDTO();
        dto.setReference(values[0]);
        dto.setNom(values[1]);
        dto.setStockDisponible(Integer.parseInt(values[2]));
        dto.setStockMinimum(Integer.parseInt(values[3]));
        dto.setDescription(values[4]);
        return dto;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }
}
