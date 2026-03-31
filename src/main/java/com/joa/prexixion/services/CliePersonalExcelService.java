package com.joa.prexixion.services;

import com.joa.prexixion.dto.CliePersonalProjection;
import com.joa.prexixion.repositories.CliePersonalRepository;
import com.joa.prexixion.utils.PoiUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class CliePersonalExcelService {

    private final CliePersonalRepository repository;

    public CliePersonalExcelService(CliePersonalRepository repository) {
        this.repository = repository;
    }

    public byte[] exportarExcelPersonal(String estados, String grupos) {
        List<Integer> listEstados = Arrays.stream(estados.split(","))
                .map(String::trim).map(Integer::valueOf).toList();
        List<Integer> listGrupos = Arrays.stream(grupos.split(","))
                .map(String::trim).map(Integer::valueOf).toList();

        List<CliePersonalProjection> list = repository.getPersonalData(listEstados, listGrupos);
        return generateExcel(list);
    }

    private byte[] generateExcel(List<CliePersonalProjection> list) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook();

            // --- COLORES ---
            XSSFColor GERENCIA_BLUE = PoiUtils.createColor(0, 51, 204);
            XSSFColor GERENCIA_GREY = PoiUtils.createColor(214, 220, 228);
            XSSFColor MATTE_BLACK   = PoiUtils.createColor(43, 43, 43);
            XSSFColor LIGHT_GREY    = PoiUtils.createColor(242, 244, 244);
            XSSFColor WHITE         = PoiUtils.createColor(255, 255, 255);
            XSSFColor RED           = PoiUtils.createColor(255, 0, 0);
            XSSFColor GREEN         = PoiUtils.createColor(0, 204, 0);
            XSSFColor GREY          = PoiUtils.createColor(153, 153, 153);
            XSSFColor ORANGE        = PoiUtils.createColor(255, 102, 0);
            XSSFColor LIGHT_BLUE    = PoiUtils.createColor(51, 153, 255);

            // --- FONTS ---
            boolean negrita = true;
            XSSFFont cabeceraFont    = PoiUtils.fuente(wb, WHITE, 17, negrita);
            XSSFFont whiteNegFont9   = PoiUtils.fuente(wb, WHITE, 9, negrita);
            XSSFFont dataNegFont     = PoiUtils.fuente(wb, MATTE_BLACK, 9, negrita);
            XSSFFont dataFont        = PoiUtils.fuente(wb, MATTE_BLACK, 9);
            XSSFFont dataGreenFont   = PoiUtils.fuente(wb, GREEN, 9, negrita);
            XSSFFont dataRedFont     = PoiUtils.fuente(wb, RED, 9, negrita);
            XSSFFont dataGreyFont    = PoiUtils.fuente(wb, GREY, 9, negrita);
            XSSFFont dataYellowFont  = PoiUtils.fuente(wb, ORANGE, 9, negrita);
            XSSFFont dataLightBlueFont = PoiUtils.fuente(wb, LIGHT_BLUE, 9, negrita);

            // --- ESTILOS ---
            XSSFCellStyle cabeceraStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, GERENCIA_BLUE, cabeceraFont);

            XSSFCellStyle subHeaderStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, MATTE_BLACK, whiteNegFont9);
            PoiUtils.addBorders(subHeaderStyle, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle dataStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, GERENCIA_GREY, dataNegFont);
            PoiUtils.addBorders(dataStyle, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle dataLeftStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.LEFT, GERENCIA_GREY, dataNegFont);
            PoiUtils.addBorders(dataLeftStyle, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle dataContStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, GERENCIA_GREY, dataNegFont);
            PoiUtils.addBorders(dataContStyle, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle dataLeftContStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.LEFT, GERENCIA_GREY, dataNegFont);
            PoiUtils.addBorders(dataLeftContStyle, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle dataStyle2 = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataNegFont);
            PoiUtils.addBorders(dataStyle2, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle fondoGreyStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataFont);
            PoiUtils.addBorders(fondoGreyStyle, BorderStyle.THIN, IndexedColors.WHITE);

            XSSFCellStyle dataStyleLeftSinNegrita = PoiUtils.createCellStyle(wb, HorizontalAlignment.LEFT, LIGHT_GREY, dataFont);
            PoiUtils.addBorders(dataStyleLeftSinNegrita, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreenStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataGreenFont);
            PoiUtils.addBorders(dataGreenStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataRedStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataRedFont);
            PoiUtils.addBorders(dataRedStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataGreyStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataGreyFont);
            PoiUtils.addBorders(dataGreyStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataYellowStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataYellowFont);
            PoiUtils.addBorders(dataYellowStyle, BorderStyle.THIN, IndexedColors.WHITE);

            CellStyle dataLightBlueStyle = PoiUtils.createCellStyle(wb, HorizontalAlignment.CENTER, LIGHT_GREY, dataLightBlueFont);
            PoiUtils.addBorders(dataLightBlueStyle, BorderStyle.THIN, IndexedColors.WHITE);

            // --- HOJA ---
            Sheet sheet = wb.createSheet("Personal de Cliente");
            int rowNum = 0;
            int colNum = 0;

            // CABECERA PRINCIPAL
            Row cabecera = sheet.createRow(rowNum);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);
            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(cabeceraStyle);
            cellCabecera.setCellValue("C  O  R  P  O  R  A  C  I  O  N      G  E  R  E  N  C  I  A.  C  O  M      S. A. C.");
            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:P1"));

            // SUB-CABECERA
            rowNum++;
            Row subHeader = sheet.createRow(rowNum);
            String[] headers = {"N°", "GRUPO E.", "ESTADO", "RUC", "Y", "RAZON SOCIAL",
                    "TIPO CC", "DNI", "APELLIDOS", "NOMBRES", "PUESTO",
                    "TELÉFONO", "CORREO", "F. NACIMIENTO", "CLAVE SOL", "ADM"};
            for (String h : headers) {
                Cell c = subHeader.createCell(colNum++);
                c.setCellStyle(subHeaderStyle);
                c.setCellValue(h);
            }

            rowNum++;
            int inicioFilt = rowNum - 1;
            rowNum++;

            // --- DATA ---
            int i = 1;
            for (CliePersonalProjection cp : list) {
                Row data = sheet.createRow(rowNum);
                int colData = 0;

                // N°
                Cell iData = data.createCell(colData++);
                iData.setCellValue(i);
                iData.setCellStyle(subHeaderStyle);

                // Grupo Económico
                Cell grupoEconomicoData = data.createCell(colData++);
                grupoEconomicoData.setCellValue(cp.getDescGrupoEconomico() != null ? cp.getDescGrupoEconomico() : "");
                grupoEconomicoData.setCellStyle(dataStyle2);

                // Estado (con color según idEstado)
                Cell estadoData = data.createCell(colData++);
                estadoData.setCellValue(cp.getDescEstado() != null ? cp.getDescEstado().toUpperCase() : "");
                Integer idEstado = cp.getIdEstado();
                if (idEstado != null) {
                    switch (idEstado) {
                        case 1 -> estadoData.setCellStyle(dataGreenStyle);
                        case 2 -> estadoData.setCellStyle(dataRedStyle);
                        case 3 -> estadoData.setCellStyle(dataLightBlueStyle);
                        case 4 -> estadoData.setCellStyle(dataYellowStyle);
                        case 5 -> estadoData.setCellStyle(dataGreyStyle);
                        default -> estadoData.setCellStyle(dataStyle2);
                    }
                } else {
                    estadoData.setCellStyle(dataStyle2);
                }

                // RUC
                Cell rucData = data.createCell(colData++);
                rucData.setCellValue(cp.getRuc() != null ? cp.getRuc() : "");
                rucData.setCellStyle(dataContStyle);

                // Y
                Cell yData = data.createCell(colData++);
                yData.setCellValue(cp.getY() != null ? cp.getY() : "");
                yData.setCellStyle(dataContStyle);

                // Razón Social
                Cell rsData = data.createCell(colData++);
                rsData.setCellValue(cp.getRazonSocial() != null ? cp.getRazonSocial() : "");
                rsData.setCellStyle(dataLeftContStyle);

                // Tipo CC
                Cell tipoCCData = data.createCell(colData++);
                tipoCCData.setCellValue(cp.getDescTipoCC() != null ? cp.getDescTipoCC() : "");
                tipoCCData.setCellStyle(dataStyleLeftSinNegrita);

                // DNI
                Cell dniData = data.createCell(colData++);
                dniData.setCellValue(cp.getPlDni() != null ? cp.getPlDni() : "");
                dniData.setCellStyle(fondoGreyStyle);

                // Apellidos
                Cell apellidosData = data.createCell(colData++);
                apellidosData.setCellValue(cp.getPlApellido() != null ? cp.getPlApellido() : "");
                apellidosData.setCellStyle(dataStyleLeftSinNegrita);

                // Nombres
                Cell nombresData = data.createCell(colData++);
                nombresData.setCellValue(cp.getPlNombre() != null ? cp.getPlNombre() : "");
                nombresData.setCellStyle(dataStyleLeftSinNegrita);

                // Puesto
                Cell puestoData = data.createCell(colData++);
                puestoData.setCellValue(cp.getDescPuesto() != null ? cp.getDescPuesto() : "");
                puestoData.setCellStyle(dataStyleLeftSinNegrita);

                // Teléfono
                Cell telefonoData = data.createCell(colData++);
                telefonoData.setCellValue(cp.getPlTelefono() != null ? cp.getPlTelefono() : "");
                telefonoData.setCellStyle(dataStyleLeftSinNegrita);

                // Correo
                Cell correoData = data.createCell(colData++);
                correoData.setCellValue(cp.getPlCorreo() != null ? cp.getPlCorreo() : "");
                correoData.setCellStyle(dataStyleLeftSinNegrita);

                // Fecha Nacimiento
                Cell fNacimientoData = data.createCell(colData++);
                fNacimientoData.setCellValue(cp.getPlFNacimiento() != null ? cp.getPlFNacimiento() : "");
                fNacimientoData.setCellStyle(fondoGreyStyle);

                // Clave SOL
                Cell tieneClaveSolData = data.createCell(colData++);
                tieneClaveSolData.setCellValue(cp.getPlTieneClaveSol() != null ? cp.getPlTieneClaveSol() : "");
                tieneClaveSolData.setCellStyle(fondoGreyStyle);

                // Administración
                Cell administracionData = data.createCell(colData++);
                Integer plAdm = cp.getPlAdministracion();
                administracionData.setCellValue(plAdm != null && plAdm == 1 ? "SI" : "NO");
                administracionData.setCellStyle(fondoGreyStyle);

                rowNum++;
                i++;
            }

            int finFilt = rowNum - 1;
            sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, 15));
            sheet.createFreezePane(0, 3);

            for (int contCol = 0; contCol < colNum; contCol++) {
                sheet.autoSizeColumn(contCol);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de personal", e);
        }
    }
}
