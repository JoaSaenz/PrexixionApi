package com.joa.prexixion.services;

import com.joa.prexixion.dto.ClienteClavesProjection;
import com.joa.prexixion.repositories.ClienteClavesRepository;
import com.joa.prexixion.utils.ExcelStyleManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class ClienteClavesExcelService {

    private final ClienteClavesRepository repository;

    public ClienteClavesExcelService(ClienteClavesRepository repository) {
        this.repository = repository;
    }

    public byte[] exportarExcelClaves(String estados, String grupos) {
        List<Integer> listEstados = Arrays.stream(estados.split(","))
                .map(String::trim).map(Integer::valueOf).toList();
        List<Integer> listGrupos = Arrays.stream(grupos.split(","))
                .map(String::trim).map(Integer::valueOf).toList();

        List<ClienteClavesProjection> list = repository.getClavesData(listEstados, listGrupos);
        return generateExcel(list);
    }

    private byte[] generateExcel(List<ClienteClavesProjection> list) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook();

            ExcelStyleManager styleManager = new ExcelStyleManager(wb);

            // --- ESTILOS ---
            XSSFCellStyle estiloDatosCentroGris = styleManager.getGenericStyle(ExcelStyleManager.LIGHT_GREY_RGB, ExcelStyleManager.MATTE_BLACK_RGB, false);
            XSSFCellStyle estiloDatosIzquierdaGris = styleManager.getGenericStyle(ExcelStyleManager.LIGHT_GREY_RGB, ExcelStyleManager.MATTE_BLACK_RGB, false);
            estiloDatosIzquierdaGris.setAlignment(HorizontalAlignment.LEFT);

            XSSFCellStyle estiloDatosCentroGrisNegrita = styleManager.getGenericStyle(ExcelStyleManager.LIGHT_GREY_RGB, ExcelStyleManager.MATTE_BLACK_RGB, true);

            XSSFCellStyle estiloDatosCentroNegroNegrita = styleManager.getFondoBlackStyle();

            XSSFCellStyle estiloDatosCentroGrisRojoNegrita = styleManager.getGenericStyle(ExcelStyleManager.LIGHT_GREY_RGB, ExcelStyleManager.RED_RGB, true);

            XSSFCellStyle estiloCabeceraCentroAzul = styleManager.getHeaderStyle();
            XSSFCellStyle estiloSubCabeceraCentroAzulNegrita = styleManager.getSubHeaderStyleBlue();

            // --- HOJA ---
            XSSFSheet sheet = wb.createSheet("REPORTE");
            int rowNum = 0;
            int colNum = 0;

            // CABECERA PRINCIPAL
            Row cabecera = sheet.createRow(rowNum);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);
            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(estiloCabeceraCentroAzul);
            cellCabecera.setCellValue("C  O  R  P  O  R  A  C  I  O  N      G  E  R  E  N  C  I  A.  C  O  M      S. A. C.");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:U1"));

            rowNum++;
            Row subHeader = sheet.createRow(rowNum);

            String[] headers = {"N°", "CAT", "CGE", "STO", "Estado", "RUC", "Y", "C", "Razón Social",
                    "Main U.", "Main C.", "UPS U.", "UPS C.", "Gear U.", "Gear C.",
                    "Signer U.", "Signer C.", "AfpNet U.", "AfpNet C.", "Sis U.", "Sis C."};
            for (String h : headers) {
                Cell c = subHeader.createCell(colNum++);
                c.setCellStyle(estiloSubCabeceraCentroAzulNegrita);
                c.setCellValue(h);
            }

            rowNum++;
            int inicioFilt = rowNum - 1;
            rowNum++;

            // --- DATA ---
            int i = 1;
            for (ClienteClavesProjection c : list) {
                Row data = sheet.createRow(rowNum);
                colNum = 0;

                // N°
                Cell dataN = data.createCell(colNum++);
                dataN.setCellStyle(estiloDatosCentroNegroNegrita);
                dataN.setCellValue(i);

                // CAT
                Cell dataCategoria = data.createCell(colNum++);
                dataCategoria.setCellStyle(estiloDatosCentroGrisNegrita);
                dataCategoria.setCellValue(c.getAbrCategoria() != null ? c.getAbrCategoria() : "");

                // CGE
                Cell dataCGE = data.createCell(colNum++);
                Integer cge = c.getCategoriaGrupoE();
                if (cge != null && cge == 1) {
                    dataCGE.setCellStyle(estiloDatosCentroGrisRojoNegrita);
                    dataCGE.setCellValue("SI");
                } else {
                    dataCGE.setCellStyle(estiloDatosCentroGrisNegrita);
                    dataCGE.setCellValue("");
                }

                // STO
                Cell dataSTO = data.createCell(colNum++);
                Integer sto = c.getCategoriaStore();
                if (sto != null && sto == 1) {
                    dataSTO.setCellStyle(estiloDatosCentroGrisRojoNegrita);
                    dataSTO.setCellValue("SI");
                } else {
                    dataSTO.setCellStyle(estiloDatosCentroGrisNegrita);
                    dataSTO.setCellValue("");
                }

                // Estado
                Cell dataEstado = data.createCell(colNum++);
                dataEstado.setCellStyle(estiloDatosCentroGrisNegrita);
                dataEstado.setCellValue(c.getDescEstado() != null ? c.getDescEstado() : "");

                // RUC
                Cell dataRuc = data.createCell(colNum++);
                dataRuc.setCellStyle(estiloDatosCentroGrisNegrita);
                dataRuc.setCellValue(c.getRuc() != null ? c.getRuc() : "");

                // Y
                Cell dataY = data.createCell(colNum++);
                dataY.setCellStyle(estiloDatosCentroGrisNegrita);
                dataY.setCellValue(c.getY() != null ? c.getY() : "");

                // C (Contribuyente)
                Cell dataC = data.createCell(colNum++);
                dataC.setCellStyle(estiloDatosCentroGrisNegrita);
                dataC.setCellValue(c.getAbrContribuyente() != null ? c.getAbrContribuyente() : "");

                // Razón Social
                Cell dataRZ = data.createCell(colNum++);
                dataRZ.setCellStyle(estiloDatosIzquierdaGris);
                dataRZ.setCellValue(c.getRazonSocial() != null ? c.getRazonSocial() : "");

                // Main U / C
                createStringCell(data, colNum++, c.getSolU(), estiloDatosCentroGris);
                createStringCell(data, colNum++, c.getSolC(), estiloDatosCentroGris);

                // UPS U / C
                createStringCell(data, colNum++, c.getUpsU(), estiloDatosCentroGris);
                createStringCell(data, colNum++, c.getUpsC(), estiloDatosCentroGris);

                // Gear U / C
                createStringCell(data, colNum++, c.getSoldierU(), estiloDatosCentroGris);
                createStringCell(data, colNum++, c.getSoldierC(), estiloDatosCentroGris);

                // Signer U / C
                createStringCell(data, colNum++, c.getSignerU(), estiloDatosCentroGris);
                createStringCell(data, colNum++, c.getSignerC(), estiloDatosCentroGris);

                // AfpNet U / C
                createStringCell(data, colNum++, c.getAfpU(), estiloDatosCentroGris);
                createStringCell(data, colNum++, c.getAfpC(), estiloDatosCentroGris);

                // Sis U / C
                createStringCell(data, colNum++, c.getSisU(), estiloDatosCentroGris);
                createStringCell(data, colNum++, c.getSisC(), estiloDatosCentroGris);

                rowNum++;
                i++;
            }

            int finFilt = rowNum - 1;
            sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, 20));
            sheet.createFreezePane(0, 3);

            for (int contCol = 0; contCol < colNum; contCol++) {
                sheet.autoSizeColumn(contCol);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de claves", e);
        }
    }

    private void createStringCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellStyle(style);
        cell.setCellValue(value != null ? value : "");
    }
}
