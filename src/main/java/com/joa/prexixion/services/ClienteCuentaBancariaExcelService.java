package com.joa.prexixion.services;

import com.joa.prexixion.dto.ClienteCuentaBancariaProjection;
import com.joa.prexixion.repositories.ClienteCuentaBancariaRepository;
import com.joa.prexixion.utils.PoiUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class ClienteCuentaBancariaExcelService {

    private final ClienteCuentaBancariaRepository repository;

    public ClienteCuentaBancariaExcelService(ClienteCuentaBancariaRepository repository) {
        this.repository = repository;
    }

    public byte[] exportarExcelCCI(String estados, String grupos) {
        List<Integer> listEstados = Arrays.stream(estados.split(","))
                .map(String::trim).map(Integer::valueOf).toList();
        List<Integer> listGrupos = Arrays.stream(grupos.split(","))
                .map(String::trim).map(Integer::valueOf).toList();

        List<ClienteCuentaBancariaProjection> list = repository.getCuentaBancariaData(listEstados, listGrupos);
        return generateExcel(list);
    }

    private byte[] generateExcel(List<ClienteCuentaBancariaProjection> list) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook();

            // --- COLORES ---
            XSSFColor GERENCIA_BLUE = PoiUtils.createColor(0, 51, 204);
            XSSFColor GERENCIA_GREY = PoiUtils.createColor(214, 220, 228);
            XSSFColor MATTE_BLACK   = PoiUtils.createColor(43, 43, 43);
            XSSFColor WHITE         = PoiUtils.createColor(255, 255, 255);

            // --- FONTS ---
            boolean negrita = true;
            XSSFFont cabeceraFont  = PoiUtils.fuente(wb, WHITE, 17, negrita);
            XSSFFont whiteNegFont9 = PoiUtils.fuente(wb, WHITE, 9, negrita);
            XSSFFont dataNegFont   = PoiUtils.fuente(wb, MATTE_BLACK, 9, negrita);

            // --- ESTILOS ---
            XSSFCellStyle cabeceraStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, GERENCIA_BLUE, cabeceraFont);

            XSSFCellStyle subHeaderStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, MATTE_BLACK, whiteNegFont9);
            PoiUtils.addBorders(subHeaderStyle, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle dataStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, GERENCIA_GREY, dataNegFont);
            PoiUtils.addBorders(dataStyle, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle dataContStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, GERENCIA_GREY, dataNegFont);
            PoiUtils.addBorders(dataContStyle, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle dataLeftContStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.LEFT, GERENCIA_GREY, dataNegFont);
            PoiUtils.addBorders(dataLeftContStyle, BorderStyle.THIN, IndexedColors.WHITE);

            // --- HOJA ---
            Sheet sheet = wb.createSheet("CCI");
            int rowNum = 0;
            int colNum = 0;

            // CABECERA PRINCIPAL
            Row cabecera = sheet.createRow(rowNum);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);
            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(cabeceraStyle);
            cellCabecera.setCellValue("C  O  R  P  O  R  A  C  I  O  N      G  E  R  E  N  C  I  A.  C  O  M      S. A. C.");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:H1"));

            // SUB-CABECERA
            rowNum++;
            Row subHeader = sheet.createRow(rowNum);
            String[] headers = {"N°", "ESTADO", "ALTA.COM", "R.U.C.", "Y", "C",
                    "APELLIDOS Y NOMBRES o RAZON SOCIAL", "CCI"};
            for (String h : headers) {
                Cell c = subHeader.createCell(colNum++);
                c.setCellStyle(subHeaderStyle);
                c.setCellValue(h);
            }

            rowNum++;
            int inicioFilt = rowNum;
            rowNum++;

            // --- DATA ---
            int i = 1;
            for (ClienteCuentaBancariaProjection obj : list) {
                Row data = sheet.createRow(rowNum);
                int colData = 0;

                Cell iData = data.createCell(colData++);
                iData.setCellValue(i);
                iData.setCellStyle(dataStyle);

                Cell estadoData = data.createCell(colData++);
                estadoData.setCellValue(obj.getDescEstado() != null ? obj.getDescEstado() : "");
                estadoData.setCellStyle(dataStyle);

                Cell altaData = data.createCell(colData++);
                altaData.setCellValue(obj.getAcInicioCom() != null ? obj.getAcInicioCom() : "");
                altaData.setCellStyle(dataStyle);

                Cell rucData = data.createCell(colData++);
                rucData.setCellValue(obj.getIdCliente() != null ? obj.getIdCliente() : "");
                rucData.setCellStyle(dataContStyle);

                Cell yData = data.createCell(colData++);
                yData.setCellValue(obj.getY() != null ? obj.getY() : "");
                yData.setCellStyle(dataContStyle);

                Cell tipoData = data.createCell(colData++);
                tipoData.setCellValue(obj.getAbrContribuyente() != null ? obj.getAbrContribuyente() : "");
                tipoData.setCellStyle(dataContStyle);

                Cell rsData = data.createCell(colData++);
                rsData.setCellValue(obj.getRazonSocial() != null ? obj.getRazonSocial() : "");
                rsData.setCellStyle(dataLeftContStyle);

                Cell cciData = data.createCell(colData++);
                cciData.setCellValue(obj.getCcbCCI() != null ? obj.getCcbCCI() : "");
                cciData.setCellStyle(dataContStyle);

                rowNum++;
                i++;
            }

            int finFilt = rowNum - 1;
            sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, 7));
            sheet.createFreezePane(0, 3);

            for (int contCol = 0; contCol < colNum; contCol++) {
                sheet.autoSizeColumn(contCol);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel CCI", e);
        }
    }
}
