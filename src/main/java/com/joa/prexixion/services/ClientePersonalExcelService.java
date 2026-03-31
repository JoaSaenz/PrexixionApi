package com.joa.prexixion.services;

import com.joa.prexixion.dto.ClientePersonalProjection;
import com.joa.prexixion.repositories.ClientePersonalRepository;
import com.joa.prexixion.utils.ExcelStyleManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class ClientePersonalExcelService {

    private final ClientePersonalRepository repository;

    public ClientePersonalExcelService(ClientePersonalRepository repository) {
        this.repository = repository;
    }

    public byte[] exportarExcelPersonal(String estados, String grupos) {
        List<Integer> listEstados = Arrays.stream(estados.split(","))
                .map(String::trim).map(Integer::valueOf).toList();
        List<Integer> listGrupos = Arrays.stream(grupos.split(","))
                .map(String::trim).map(Integer::valueOf).toList();

        List<ClientePersonalProjection> list = repository.getPersonalData(listEstados, listGrupos);
        return generateExcel(list);
    }

    private byte[] generateExcel(List<ClientePersonalProjection> list) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook();

            ExcelStyleManager styleManager = new ExcelStyleManager(wb);

            // --- ESTILOS ---
            XSSFCellStyle estiloCabeceraCentroAzul = styleManager.getHeaderStyle();
            XSSFCellStyle estiloSubCabeceraCentroNegroNegrita = styleManager.getSubHeaderStyle();

            XSSFCellStyle estiloDatosCentroGrisNegrita = styleManager.getDataCenterBoldStyle();
            XSSFCellStyle estiloDatosIzquierdaGrisNegrita = styleManager.getDataLeftBoldStyle();

            XSSFCellStyle estiloDatosCentroGris = styleManager.getDataCenterStyle();
            XSSFCellStyle estiloDatosIzquierdaGris = styleManager.getDataLeftStyle();

            XSSFCellStyle estiloDatosCentroVerdeNegrita = styleManager.getDataStatusStyle(ExcelStyleManager.GREEN_RGB);
            XSSFCellStyle estiloDatosCentroRojoNegrita = styleManager.getDataStatusStyle(ExcelStyleManager.RED_RGB);
            XSSFCellStyle estiloDatosCentroGrisOscuroNegrita = styleManager.getDataStatusStyle(ExcelStyleManager.GREY_RGB);
            XSSFCellStyle estiloDatosCentroNaranjaNegrita = styleManager.getDataStatusStyle(ExcelStyleManager.ORANGE_RGB);
            XSSFCellStyle estiloDatosCentroAzulNegrita = styleManager.getDataStatusStyle(ExcelStyleManager.LIGHT_BLUE_RGB);

            // --- HOJA ---
            Sheet sheet = wb.createSheet("Personal de Cliente");
            int rowNum = 0;
            int colNum = 0;

            // CABECERA PRINCIPAL
            Row cabecera = sheet.createRow(rowNum);
            cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);
            Cell cellCabecera = cabecera.createCell(0);
            cellCabecera.setCellStyle(estiloCabeceraCentroAzul);
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
                c.setCellStyle(estiloSubCabeceraCentroNegroNegrita);
                c.setCellValue(h);
            }

            rowNum++;
            int inicioFilt = rowNum - 1;
            rowNum++;

            // --- DATA ---
            int i = 1;
            for (ClientePersonalProjection cp : list) {
                Row data = sheet.createRow(rowNum);
                int colData = 0;

                // N°
                Cell iData = data.createCell(colData++);
                iData.setCellValue(i);
                iData.setCellStyle(estiloSubCabeceraCentroNegroNegrita);

                // Grupo Económico
                Cell grupoEconomicoData = data.createCell(colData++);
                grupoEconomicoData.setCellValue(cp.getDescGrupoEconomico() != null ? cp.getDescGrupoEconomico() : "");
                grupoEconomicoData.setCellStyle(estiloDatosCentroGrisNegrita);

                // Estado (con color según idEstado)
                Cell estadoData = data.createCell(colData++);
                estadoData.setCellValue(cp.getDescEstado() != null ? cp.getDescEstado() : "");
                
                int idEstado = cp.getIdEstado() != null ? cp.getIdEstado() : 0;
                switch (idEstado) {
                    case 1 -> estadoData.setCellStyle(estiloDatosCentroVerdeNegrita);
                    case 2 -> estadoData.setCellStyle(estiloDatosCentroRojoNegrita);
                    case 3 -> estadoData.setCellStyle(estiloDatosCentroGrisOscuroNegrita);
                    case 4 -> estadoData.setCellStyle(estiloDatosCentroNaranjaNegrita);
                    case 5 -> estadoData.setCellStyle(estiloDatosCentroAzulNegrita);
                    default -> estadoData.setCellStyle(estiloDatosCentroGrisNegrita);
                }

                // RUC
                Cell rucData = data.createCell(colData++);
                rucData.setCellValue(cp.getRuc() != null ? cp.getRuc() : "");
                rucData.setCellStyle(estiloDatosCentroGrisNegrita);

                // Y
                Cell yData = data.createCell(colData++);
                yData.setCellValue(cp.getY() != null ? cp.getY() : "");
                yData.setCellStyle(estiloDatosCentroGrisNegrita);

                // Razón Social
                Cell rsData = data.createCell(colData++);
                rsData.setCellValue(cp.getRazonSocial() != null ? cp.getRazonSocial() : "");
                rsData.setCellStyle(estiloDatosIzquierdaGrisNegrita);

                // TIPO CC
                Cell tipoCCData = data.createCell(colData++);
                tipoCCData.setCellValue(cp.getDescTipoCC() != null ? cp.getDescTipoCC() : "");
                tipoCCData.setCellStyle(estiloDatosIzquierdaGris);

                // DNI
                Cell dniData = data.createCell(colData++);
                dniData.setCellValue(cp.getPlDni() != null ? cp.getPlDni() : "");
                dniData.setCellStyle(estiloDatosCentroGris);

                // APELLIDOS
                Cell apellidosData = data.createCell(colData++);
                apellidosData.setCellValue(cp.getPlApellido() != null ? cp.getPlApellido() : "");
                apellidosData.setCellStyle(estiloDatosIzquierdaGris);

                // NOMBRES
                Cell nombresData = data.createCell(colData++);
                nombresData.setCellValue(cp.getPlNombre() != null ? cp.getPlNombre() : "");
                nombresData.setCellStyle(estiloDatosIzquierdaGris);

                // PUESTO
                Cell puestoData = data.createCell(colData++);
                puestoData.setCellValue(cp.getDescPuesto() != null ? cp.getDescPuesto() : "");
                puestoData.setCellStyle(estiloDatosIzquierdaGris);

                // TELÉFONO
                Cell telfData = data.createCell(colData++);
                telfData.setCellValue(cp.getPlTelefono() != null ? cp.getPlTelefono() : "");
                telfData.setCellStyle(estiloDatosCentroGris);

                // CORREO
                Cell correoData = data.createCell(colData++);
                correoData.setCellValue(cp.getPlCorreo() != null ? cp.getPlCorreo() : "");
                correoData.setCellStyle(estiloDatosIzquierdaGris);

                // Fecha Nacimiento
                Cell fNacimientoData = data.createCell(colData++);
                fNacimientoData.setCellValue(cp.getPlFNacimiento() != null ? cp.getPlFNacimiento() : "");
                fNacimientoData.setCellStyle(estiloDatosCentroGris);

                // Clave SOL
                Cell tieneClaveSolData = data.createCell(colData++);
                tieneClaveSolData.setCellValue(cp.getPlTieneClaveSol() != null ? cp.getPlTieneClaveSol() : "");
                tieneClaveSolData.setCellStyle(estiloDatosCentroGris);

                // Administración
                Cell administracionData = data.createCell(colData++);
                Integer plAdm = cp.getPlAdministracion();
                administracionData.setCellValue(plAdm != null && plAdm == 1 ? "SI" : "NO");
                administracionData.setCellStyle(estiloDatosCentroGris);

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
