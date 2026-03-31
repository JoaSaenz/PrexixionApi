package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.ClienteCuentaBancariaProjection;
import com.joa.prexixionapi.repositories.ClienteCuentaBancariaRepository;
import com.joa.prexixionapi.utils.ExcelStyleManager;
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

            ExcelStyleManager styleManager = new ExcelStyleManager(wb);

            // --- ESTILOS ---
            XSSFCellStyle estiloCabeceraCentroAzul = styleManager.getHeaderStyle();

            XSSFCellStyle estiloSubCabeceraCentroNegroNegrita = styleManager.getSubHeaderStyle();

            XSSFCellStyle estiloDatosCentroGrisNegrita = styleManager.getDataCenterBoldStyle();

            XSSFCellStyle estiloDatosIzquierdaGrisNegrita = styleManager.getDataLeftBoldStyle();

            // --- HOJA ---
            Sheet sheet = wb.createSheet("CCI");
            int rowNum = 0;
            int colNum = 0;

            // CABECERA PRINCIPAL
            Row cabecera = sheet.createRow(rowNum);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);
            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(estiloCabeceraCentroAzul);
            cellCabecera.setCellValue("C  O  R  P  O  R  A  C  I  O  N      G  E  R  E  N  C  I  A.  C  O  M      S. A. C.");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:H1"));

            // SUB-CABECERA
            rowNum++;
            Row subHeader = sheet.createRow(rowNum);
            String[] headers = {"N°", "ESTADO", "ALTA.COM", "R.U.C.", "Y", "C",
                    "APELLIDOS Y NOMBRES o RAZON SOCIAL", "CCI"};
            for (String h : headers) {
                Cell c = subHeader.createCell(colNum++);
                c.setCellStyle(estiloSubCabeceraCentroNegroNegrita);
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
                iData.setCellStyle(estiloDatosCentroGrisNegrita);

                Cell estadoData = data.createCell(colData++);
                estadoData.setCellValue(obj.getDescEstado() != null ? obj.getDescEstado() : "");
                estadoData.setCellStyle(estiloDatosCentroGrisNegrita);

                Cell altaData = data.createCell(colData++);
                altaData.setCellValue(obj.getAcInicioCom() != null ? obj.getAcInicioCom() : "");
                altaData.setCellStyle(estiloDatosCentroGrisNegrita);

                Cell rucData = data.createCell(colData++);
                rucData.setCellValue(obj.getIdCliente() != null ? obj.getIdCliente() : "");
                rucData.setCellStyle(estiloDatosCentroGrisNegrita);

                Cell yData = data.createCell(colData++);
                yData.setCellValue(obj.getY() != null ? obj.getY() : "");
                yData.setCellStyle(estiloDatosCentroGrisNegrita);

                Cell contribuyenteData = data.createCell(colData++);
                contribuyenteData.setCellValue(obj.getAbrContribuyente() != null ? obj.getAbrContribuyente() : "");
                contribuyenteData.setCellStyle(estiloDatosCentroGrisNegrita);

                Cell rsData = data.createCell(colData++);
                rsData.setCellValue(obj.getRazonSocial() != null ? obj.getRazonSocial() : "");
                rsData.setCellStyle(estiloDatosIzquierdaGrisNegrita);

                Cell cciData = data.createCell(colData++);
                cciData.setCellValue(obj.getCcbCCI() != null ? obj.getCcbCCI() : "");
                cciData.setCellStyle(estiloDatosCentroGrisNegrita);

                i++;
                rowNum++;
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
