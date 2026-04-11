package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.ActivoDTO;
import com.joa.prexixionapi.dto.ActivoDepreciacionDTO;
import com.joa.prexixionapi.dto.ActivoExcelDTO;
import com.joa.prexixionapi.utils.DateUtils;
import com.joa.prexixionapi.utils.PoiUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DepreciacionReporteExcelService {

    private final ActivoService activoService;
    private final DepreciacionService depreciacionService;

    public byte[] generateExcel(String idCliente, String razonSocial) throws Exception {
        // 1. Fetch raw data
        ActivoExcelDTO excelData = activoService.getActivosExcelData(idCliente);
        List<ActivoDTO> list = excelData.getActivos();

        String fechaMenor = excelData.getMinFechaCompra();
        int hojaAnioI = getAnioRequired(fechaMenor);
        int anioInicioBienesCont = hojaAnioI;
        int anioInicioBienesTrib = hojaAnioI;

        int hojaAnioF = getAnioRequired(excelData.getMaxFechaContableTributaria());
        int anioFinBienesCont = hojaAnioF;
        int anioFinBienesTrib = hojaAnioF;

        // 2. Extrapolate missing years up to hojaAnioF
        for (ActivoDTO bien : list) {
            depreciacionService.completarAniosFaltantes(bien.getDepreciacionesContables(), bien, hojaAnioF, 1);
            depreciacionService.completarAniosFaltantes(bien.getDepreciacionesTributarias(), bien, hojaAnioF, 2);
        }

        // 3. Generate POI Workbook
        Workbook wb = new XSSFWorkbook();

        // <editor-fold defaultstate="collapsed" desc=" E S T I L O S ">
        // <editor-fold defaultstate="collapsed" desc="COLORES PERSONALIZADOS">
        XSSFColor GERENCIA_BLUE = new XSSFColor(new byte[] { (byte) 0, (byte) 51, (byte) 204 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor GERENCIA_GREY = new XSSFColor(new byte[] { (byte) 214, (byte) 220, (byte) 228 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor MATTE_BLACK = new XSSFColor(new byte[] { (byte) 43, (byte) 43, (byte) 43 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());

        XSSFColor VERY_LIGHT_RED = new XSSFColor(new byte[] { (byte) 255, (byte) 102, (byte) 102 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor LIGHT_RED = new XSSFColor(new byte[] { (byte) 255, (byte) 51, (byte) 51 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor RED = new XSSFColor(new byte[] { (byte) 255, (byte) 0, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor DARK_RED = new XSSFColor(new byte[] { (byte) 204, (byte) 0, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor VERY_DARK_RED = new XSSFColor(new byte[] { (byte) 153, (byte) 0, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());

        XSSFColor VERY_LIGHT_BLUE = new XSSFColor(new byte[] { (byte) 51, (byte) 204, (byte) 255 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor LIGHT_BLUE = new XSSFColor(new byte[] { (byte) 51, (byte) 153, (byte) 255 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor BLUE = new XSSFColor(new byte[] { (byte) 0, (byte) 0, (byte) 255 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor DARK_BLUE = new XSSFColor(new byte[] { (byte) 0, (byte) 0, (byte) 204 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor VERY_DARK_BLUE = new XSSFColor(new byte[] { (byte) 0, (byte) 0, (byte) 153 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());

        XSSFColor VERY_LIGHT_GREEN = new XSSFColor(new byte[] { (byte) 102, (byte) 255, (byte) 102 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor LIGHT_GREEN = new XSSFColor(new byte[] { (byte) 0, (byte) 255, (byte) 51 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor GREEN = new XSSFColor(new byte[] { (byte) 0, (byte) 204, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor DARK_GREEN = new XSSFColor(new byte[] { (byte) 0, (byte) 153, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor VERY_DARK_GREEN = new XSSFColor(new byte[] { (byte) 0, (byte) 102, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());

        XSSFColor VERY_LIGHT_YELLOW = new XSSFColor(new byte[] { (byte) 255, (byte) 255, (byte) 204 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor LIGHT_YELLOW = new XSSFColor(new byte[] { (byte) 255, (byte) 255, (byte) 153 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor YELLOW = new XSSFColor(new byte[] { (byte) 255, (byte) 255, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor DARK_YELLOW = new XSSFColor(new byte[] { (byte) 255, (byte) 204, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());

        XSSFColor LIGHT_ORANGE = new XSSFColor(new byte[] { (byte) 255, (byte) 153, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor ORANGE = new XSSFColor(new byte[] { (byte) 255, (byte) 102, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());

        XSSFColor GOLD = new XSSFColor(new byte[] { (byte) 255, (byte) 204, (byte) 51 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());

        XSSFColor LIGHT_GREY = new XSSFColor(new byte[] { (byte) 204, (byte) 204, (byte) 204 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor GREY = new XSSFColor(new byte[] { (byte) 153, (byte) 153, (byte) 153 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor DARK_GREY = new XSSFColor(new byte[] { (byte) 102, (byte) 102, (byte) 102 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor VERY_DARK_GREY = new XSSFColor(new byte[] { (byte) 51, (byte) 51, (byte) 51 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());

        XSSFColor LIGHT_BROWN = new XSSFColor(new byte[] { (byte) 153, (byte) 102, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor BROWN = new XSSFColor(new byte[] { (byte) 102, (byte) 51, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        XSSFColor VERY_DARK_BROWN = new XSSFColor(new byte[] { (byte) 51, (byte) 0, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());

        XSSFColor PURPLE = new XSSFColor(new byte[] { (byte) 102, (byte) 0, (byte) 153 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());

        XSSFColor BLACK = new XSSFColor(new byte[] { (byte) 0, (byte) 0, (byte) 0 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());

        XSSFColor WHITE = new XSSFColor(new byte[] { (byte) 255, (byte) 255, (byte) 255 },
                new org.apache.poi.xssf.usermodel.DefaultIndexedColorMap());
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="FONTS">
        Boolean negrita = true;
        XSSFFont fontWhite17b = PoiUtils.fuenteAN((XSSFWorkbook) wb, WHITE, 17, negrita);
        XSSFFont fontWhite8b = PoiUtils.fuenteAN((XSSFWorkbook) wb, WHITE, 8, negrita);
        XSSFFont fontBlack8b = PoiUtils.fuenteAN((XSSFWorkbook) wb, MATTE_BLACK, 8, negrita);
        XSSFFont fontBlack8 = PoiUtils.fuenteAN((XSSFWorkbook) wb, MATTE_BLACK, 8);

        XSSFFont fontGreen8b = PoiUtils.fuenteAN((XSSFWorkbook) wb, GREEN, 8, negrita);
        XSSFFont fontRed8b = PoiUtils.fuenteAN((XSSFWorkbook) wb, RED, 8, negrita);
        XSSFFont fontGrey8b = PoiUtils.fuenteAN((XSSFWorkbook) wb, GREY, 8, negrita);
        XSSFFont fontYellow8b = PoiUtils.fuenteAN((XSSFWorkbook) wb, ORANGE, 8, negrita);
        XSSFFont fontLightBlue8b = PoiUtils.fuenteAN((XSSFWorkbook) wb, LIGHT_BLUE, 8, negrita);
        // </editor-fold>

        CreationHelper ch = ((XSSFWorkbook) wb).getCreationHelper();

        // <editor-fold defaultstate="collapsed" desc="CELL STYLES">
        XSSFCellStyle styleHeader = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                MATTE_BLACK, fontWhite17b);

        XSSFCellStyle styleSubHeader = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                MATTE_BLACK, fontWhite8b);
        PoiUtils.addBorders(styleSubHeader, BorderStyle.THIN, IndexedColors.WHITE);

        XSSFCellStyle styleSubHeaderLeft = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT,
                MATTE_BLACK, fontWhite8b);
        PoiUtils.addBorders(styleSubHeaderLeft, BorderStyle.THIN, IndexedColors.WHITE);

        XSSFCellStyle styleCenter = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                GERENCIA_GREY, fontBlack8);
        PoiUtils.addBorders(styleCenter, BorderStyle.THIN, IndexedColors.WHITE);

        XSSFCellStyle styleLeft = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.LEFT,
                GERENCIA_GREY, fontBlack8);
        PoiUtils.addBorders(styleLeft, BorderStyle.THIN, IndexedColors.WHITE);

        XSSFCellStyle styleDate = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                GERENCIA_GREY, fontBlack8);
        styleDate.setDataFormat(ch.createDataFormat().getFormat("dd/MM/yyyy"));
        PoiUtils.addBorders(styleDate, BorderStyle.THIN, IndexedColors.WHITE);

        XSSFCellStyle styleCenter2 = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                LIGHT_GREY, fontBlack8);
        PoiUtils.addBorders(styleCenter2, BorderStyle.THIN, IndexedColors.WHITE);

        XSSFCellStyle styleMoney = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.RIGHT,
                GERENCIA_GREY, fontBlack8b);
        PoiUtils.addBorders(styleMoney, BorderStyle.THIN, IndexedColors.WHITE);
        styleMoney.setDataFormat(ch.createDataFormat().getFormat("#,###.00"));

        XSSFCellStyle styleMoneyTotal = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.RIGHT,
                LIGHT_GREY, fontBlack8b);
        PoiUtils.addBorders(styleMoneyTotal, BorderStyle.THIN, IndexedColors.WHITE);
        styleMoneyTotal.setDataFormat(ch.createDataFormat().getFormat("#,###.00"));

        XSSFCellStyle styleGreen = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                GERENCIA_GREY, fontGreen8b);
        PoiUtils.addBorders(styleGreen, BorderStyle.THIN, IndexedColors.WHITE);

        XSSFCellStyle styleRed = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                GERENCIA_GREY, fontRed8b);
        PoiUtils.addBorders(styleRed, BorderStyle.THIN, IndexedColors.WHITE);

        XSSFCellStyle styleGrey = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                GERENCIA_GREY, fontGrey8b);
        PoiUtils.addBorders(styleGrey, BorderStyle.THIN, IndexedColors.WHITE);

        XSSFCellStyle styleYellow = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                GERENCIA_GREY, fontYellow8b);
        PoiUtils.addBorders(styleYellow, BorderStyle.THIN, IndexedColors.WHITE);

        XSSFCellStyle styleBlue = PoiUtils.createCellStyle((XSSFWorkbook) wb, HorizontalAlignment.CENTER,
                GERENCIA_GREY, fontLightBlue8b);
        PoiUtils.addBorders(styleBlue, BorderStyle.THIN, IndexedColors.WHITE);
        // </editor-fold>

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="HOJA BIENES CONTABLES">
        String sheetName = "BIENES CONTA";
        Sheet sheet = wb.createSheet(sheetName);

        int rowNum = 1;
        int colNum = 0;

        // <editor-fold defaultstate="collapsed" desc=" C A B E C E R A ">
        Row totalesResumen = sheet.createRow(rowNum);
        rowNum++;

        Row cabecera = sheet.createRow(rowNum);
        cabecera.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 3);

        Cell cellCabecera = cabecera.createCell(colNum);
        cellCabecera.setCellStyle(styleHeader);
        cellCabecera.setCellValue("DEPRECIACIONES   -   " + razonSocial);
        // cellCabecera.setCellValue("D E P R E C I A C I O N E S - C O R P O R A C I O
        // N G E R E N C I A. C O M S. A. C.");
        sheet.addMergedRegion(CellRangeAddress.valueOf("A3:V3"));

        rowNum++;

        colNum = 0;
        Row subHeaders = sheet.createRow(rowNum);
        Cell cellSubCabecera = subHeaders.createCell(colNum);
        cellSubCabecera.setCellStyle(styleSubHeader);
        cellSubCabecera.setCellValue("Id");
        colNum++;

        Cell cellSubCabecera0 = subHeaders.createCell(colNum);
        cellSubCabecera0.setCellStyle(styleSubHeader);
        cellSubCabecera0.setCellValue("RUC/DNI Proveedor");
        colNum++;

        Cell cellSubCabecera4 = subHeaders.createCell(colNum);
        cellSubCabecera4.setCellStyle(styleSubHeader);
        cellSubCabecera4.setCellValue("Proveedor");
        colNum++;

        Cell cellSubCabecera1 = subHeaders.createCell(colNum);
        cellSubCabecera1.setCellStyle(styleSubHeader);
        cellSubCabecera1.setCellValue("Descripción");
        colNum++;

        Cell cellSubCabeceraMarca = subHeaders.createCell(colNum);
        cellSubCabeceraMarca.setCellStyle(styleSubHeader);
        cellSubCabeceraMarca.setCellValue("Marca");
        colNum++;

        Cell cellSubCabeceraModelo = subHeaders.createCell(colNum);
        cellSubCabeceraModelo.setCellStyle(styleSubHeader);
        cellSubCabeceraModelo.setCellValue("Modelo");
        colNum++;

        Cell cellSubCabeceraSeriePlaca = subHeaders.createCell(colNum);
        cellSubCabeceraSeriePlaca.setCellStyle(styleSubHeader);
        cellSubCabeceraSeriePlaca.setCellValue("Serie y/o placa");
        colNum++;

        Cell cellSubCabecera3 = subHeaders.createCell(colNum);
        cellSubCabecera3.setCellStyle(styleSubHeader);
        cellSubCabecera3.setCellValue("Tipo");
        colNum++;

        Cell cellSubCabecera2 = subHeaders.createCell(colNum);
        cellSubCabecera2.setCellStyle(styleSubHeader);
        cellSubCabecera2.setCellValue("D.Cont.");
        colNum++;

        Cell cellSubCabecera5 = subHeaders.createCell(colNum);
        cellSubCabecera5.setCellStyle(styleSubHeader);
        cellSubCabecera5.setCellValue("D.Trib.");
        colNum++;

        Cell cellSubCabecera6 = subHeaders.createCell(colNum);
        cellSubCabecera6.setCellStyle(styleSubHeader);
        cellSubCabecera6.setCellValue("Cuenta");
        colNum++;

        Cell cellSubCabecera7 = subHeaders.createCell(colNum);
        cellSubCabecera7.setCellStyle(styleSubHeader);
        cellSubCabecera7.setCellValue("Documento");
        colNum++;

        Cell cellSubCabeceraFechaCompra = subHeaders.createCell(colNum);
        cellSubCabeceraFechaCompra.setCellStyle(styleSubHeader);
        cellSubCabeceraFechaCompra.setCellValue("F. Compra");
        colNum++;

        Cell cellSubCabecera12 = subHeaders.createCell(colNum);
        cellSubCabecera12.setCellStyle(styleSubHeader);
        cellSubCabecera12.setCellValue("F. Inicio");
        colNum++;

        Cell cellSubCabecera8 = subHeaders.createCell(colNum);
        cellSubCabecera8.setCellStyle(styleSubHeader);
        cellSubCabecera8.setCellValue("F. Fin Cont.");
        colNum++;

        Cell cellSubCabecera9 = subHeaders.createCell(colNum);
        cellSubCabecera9.setCellStyle(styleSubHeader);
        cellSubCabecera9.setCellValue("F. Fin Trib.");
        colNum++;

        Cell cellSubCabeceraFechaBaja = subHeaders.createCell(colNum);
        cellSubCabeceraFechaBaja.setCellStyle(styleSubHeader);
        cellSubCabeceraFechaBaja.setCellValue("F. Baja");
        colNum++;

        Cell cellSubMail = subHeaders.createCell(colNum);
        cellSubMail.setCellStyle(styleSubHeader);
        cellSubMail.setCellValue("P.U.");
        colNum++;

        Cell cellSubCabecera10 = subHeaders.createCell(colNum);
        cellSubCabecera10.setCellStyle(styleSubHeader);
        cellSubCabecera10.setCellValue("Cant.");
        colNum++;

        Cell cellSubCabecera11 = subHeaders.createCell(colNum);
        cellSubCabecera11.setCellStyle(styleSubHeader);
        cellSubCabecera11.setCellValue("Moneda");
        colNum++;

        Cell cellSubCabeceraComp = subHeaders.createCell(colNum);
        cellSubCabeceraComp.setCellStyle(styleSubHeader);
        cellSubCabeceraComp.setCellValue("TC");
        colNum++;

        Cell cellSubCabecera13 = subHeaders.createCell(colNum);
        cellSubCabecera13.setCellStyle(styleSubHeader);
        cellSubCabecera13.setCellValue("Costo inicial");
        colNum++;

        int rowNumHeadBC = 3;

        int colNumBC = CellReference.convertColStringToIndex("X");

        // Jalar las fechas e iteraciones:
        double inicialTotalBnCont = 0.0;
        double periodoTotalBnCont = 0.0;
        double retirosTotalBnCont = 0.0;
        double finalTotalBnCont = 0.0;

        while (anioInicioBienesCont <= anioFinBienesCont) {
            rowNumHeadBC = 3;

            Cell chBienCont = cabecera.createCell(colNumBC);
            chBienCont.setCellStyle(styleSubHeader);
            chBienCont.setCellValue("DEPRECIACIÓN " + anioInicioBienesCont);

            String nameColI = CellReference.convertNumToColString(colNumBC);

            Cell chBienContInicial = subHeaders.createCell(colNumBC);
            chBienContInicial.setCellStyle(styleSubHeader);
            chBienContInicial.setCellValue("INICIAL");
            Cell chBienContInicialTotal = totalesResumen.createCell(colNumBC);
            colNumBC++;

            Cell chBienContPeriodo = subHeaders.createCell(colNumBC);
            chBienContPeriodo.setCellStyle(styleSubHeader);
            chBienContPeriodo.setCellValue("PERIODO");
            Cell chBienContPeriodoTotal = totalesResumen.createCell(colNumBC);
            colNumBC++;

            Cell chBienContRetiros = subHeaders.createCell(colNumBC);
            chBienContRetiros.setCellStyle(styleSubHeader);
            chBienContRetiros.setCellValue("RETIROS");
            Cell chBienContRetirosTotal = totalesResumen.createCell(colNumBC);
            colNumBC++;

            Cell chBienContFinal = subHeaders.createCell(colNumBC);
            chBienContFinal.setCellStyle(styleSubHeader);
            chBienContFinal.setCellValue("FINAL");
            Cell chBienContFinalTotal = totalesResumen.createCell(colNumBC);

            String nameColF = CellReference.convertNumToColString(colNumBC);

            sheet.addMergedRegion(
                    CellRangeAddress.valueOf(nameColI + rowNumHeadBC + ":" + nameColF + rowNumHeadBC));
            PoiUtils.addBorders(CellRangeAddress.valueOf(nameColI + rowNumHeadBC + ":" + nameColF + rowNumHeadBC),
                    BorderStyle.THIN, IndexedColors.WHITE, sheet);

            colNumBC++;
            colNumBC++;

            for (ActivoDTO bien : list) {
                ActivoDepreciacionDTO dpContBC = getDepContableByYear(bien, anioInicioBienesCont);

                inicialTotalBnCont += dpContBC.getInicial();
                periodoTotalBnCont += dpContBC.getTotal();

                switch (bien.getIdEstado()) {
                    case 1:
                        finalTotalBnCont += dpContBC.getInicial() + dpContBC.getTotal();
                        break;
                    case 2:
                        if (getAnioFromDateString(bien.getFechaBaja()) != anioInicioBienesCont) {
                            finalTotalBnCont += dpContBC.getInicial() + dpContBC.getTotal();
                        } else {
                            retirosTotalBnCont += dpContBC.getInicial() + dpContBC.getTotal();
                        }
                        break;
                }

            }

            setCellValue(inicialTotalBnCont, chBienContInicialTotal, styleMoneyTotal, styleCenter2);
            setCellValue(periodoTotalBnCont, chBienContPeriodoTotal, styleMoneyTotal, styleCenter2);
            setCellValue(retirosTotalBnCont, chBienContRetirosTotal, styleMoneyTotal, styleCenter2);
            setCellValue(finalTotalBnCont, chBienContFinalTotal, styleMoneyTotal, styleCenter2);

            inicialTotalBnCont = 0.0;
            periodoTotalBnCont = 0.0;
            retirosTotalBnCont = 0.0;
            finalTotalBnCont = 0.0;

            // Iterar los bienes y sacar los totales
            anioInicioBienesCont++;
        }
        // </editor-fold>
        rowNum++;
        int inicioFilt = rowNum;
        rowNum++;

        // <editor-fold defaultstate="collapsed" desc=" L L E N A D O D E L A D A T A ">
        for (ActivoDTO bien : list) {

            // <editor-fold defaultstate="collapsed" desc="Activo Contable">
            Row data = sheet.createRow(rowNum);
            colNum = 0;

            Cell cell = data.createCell(colNum);
            cell.setCellValue(bien.getId());
            cell.setCellStyle(styleLeft);
            colNum++;

            Cell cell1 = data.createCell(colNum);
            cell1.setCellValue(bien.getIdProveedor());
            cell1.setCellStyle(styleCenter);
            colNum++;

            Cell cell2 = data.createCell(colNum);
            cell2.setCellValue(bien.getProveedor());
            cell2.setCellStyle(styleLeft);
            colNum++;

            Cell cell3 = data.createCell(colNum);
            cell3.setCellValue(bien.getDescripcion());
            cell3.setCellStyle(styleLeft);
            colNum++;

            Cell cellMarca = data.createCell(colNum);
            cellMarca.setCellValue(bien.getMarca());
            cellMarca.setCellStyle(styleLeft);
            colNum++;

            Cell cellModelo = data.createCell(colNum);
            cellModelo.setCellValue(bien.getModelo());
            cellModelo.setCellStyle(styleLeft);
            colNum++;

            Cell cellSeriePlaca = data.createCell(colNum);
            cellSeriePlaca.setCellValue(bien.getSeriePlaca());
            cellSeriePlaca.setCellStyle(styleLeft);
            colNum++;

            Cell cell4 = data.createCell(colNum);
            cell4.setCellValue(bien.getTipo());
            cell4.setCellStyle(styleLeft);
            colNum++;

            Cell cell5 = data.createCell(colNum);
            cell5.setCellValue(bien.getPorcentajeDepreciacionContable());
            cell5.setCellStyle(styleCenter);
            colNum++;

            Cell cell6 = data.createCell(colNum);
            cell6.setCellValue(bien.getPorcentajeDepreciacionTributaria());
            cell6.setCellStyle(styleCenter);
            colNum++;

            Cell cell7 = data.createCell(colNum);
            cell7.setCellValue(bien.getCuenta());
            cell7.setCellStyle(styleLeft);
            colNum++;

            Cell cell8 = data.createCell(colNum);
            cell8.setCellValue(bien.getDocumento());
            cell8.setCellStyle(styleLeft);
            colNum++;

            Cell cellFechaCompra = data.createCell(colNum);
            cellFechaCompra.setCellStyle(styleCenter);
            colNum++;

            if (bien.getFechaCompra() != null) {
                if (!bien.getFechaCompra().equals("")) {
                    cellFechaCompra.setCellValue(stringToDate(bien.getFechaCompra()));
                    cellFechaCompra.setCellStyle(styleDate);
                }
            }

            Cell cell9 = data.createCell(colNum);
            cell9.setCellValue(stringToDate(bien.getFechaInicio()));
            cell9.setCellStyle(styleDate);
            colNum++;

            Cell cell10 = data.createCell(colNum);
            cell10.setCellValue(stringToDate(bien.getFechaFinContable()));
            cell10.setCellStyle(styleDate);
            colNum++;

            Cell cell11 = data.createCell(colNum);
            cell11.setCellValue(stringToDate(bien.getFechaFinTributaria()));
            cell11.setCellStyle(styleDate);
            colNum++;

            Cell cellFechaBaja = data.createCell(colNum);
            cellFechaBaja.setCellStyle(styleCenter);
            colNum++;

            if (bien.getIdEstado() == 2) {
                if (bien.getFechaBaja() != null) {
                    if (!bien.getFechaBaja().equals("")) {
                        cellFechaBaja.setCellValue(stringToDate(bien.getFechaBaja()));
                        cellFechaBaja.setCellStyle(styleDate);
                    }
                }
            }

            Cell cell12 = data.createCell(colNum);
            cell12.setCellValue(bien.getPrecioUnitario());
            cell12.setCellStyle(styleMoneyTotal);
            colNum++;

            Cell cell13 = data.createCell(colNum);
            cell13.setCellValue(bien.getCantidad());
            cell13.setCellStyle(styleCenter);
            colNum++;

            Cell cell14 = data.createCell(colNum);
            cell14.setCellValue(bien.getMoneda());
            cell14.setCellStyle(styleCenter);
            colNum++;

            Cell cell15 = data.createCell(colNum);
            cell15.setCellValue(bien.getTipoCambio());
            cell15.setCellStyle(styleCenter);
            colNum++;

            Cell cell16 = data.createCell(colNum);
            cell16.setCellValue(bien.getCostoInicial());
            cell16.setCellStyle(styleMoneyTotal);
            colNum++;
            colNum++;

            rowNum++;
            // </editor-fold>

            // LISTAR TODAS LAS DEPRECIACIONES CONTABLES
            anioInicioBienesCont = hojaAnioI;
            anioFinBienesCont = hojaAnioF;

            // Jalar las fechas e iteraciones:
            while (anioInicioBienesCont <= anioFinBienesCont) {

                // <editor-fold defaultstate="collapsed" desc="Llenado de data súper resumen">
                ActivoDepreciacionDTO dpContBC = getDepContableByYear(bien, anioInicioBienesCont);

                Cell chBienContInicial = data.createCell(colNum);
                setCellValue(dpContBC.getInicial(), chBienContInicial, styleMoneyTotal, styleCenter2);
                colNum++;

                Cell chBienContPeriodo = data.createCell(colNum);
                setCellValue(dpContBC.getTotal(), chBienContPeriodo, styleMoneyTotal, styleCenter2);
                colNum++;

                Cell chBienContRetiros = data.createCell(colNum);
                colNum++;

                Cell chBienContFinal = data.createCell(colNum);
                colNum++;
                colNum++;

                switch (bien.getIdEstado()) {
                    case 1:
                        chBienContRetiros.setCellStyle(styleCenter2);

                        setCellValue(dpContBC.getInicial() + dpContBC.getTotal(), chBienContFinal, styleMoneyTotal,
                                styleCenter2);
                        break;
                    case 2:
                        if (getAnioFromDateString(bien.getFechaBaja()) != anioInicioBienesCont) {
                            chBienContRetiros.setCellStyle(styleCenter2);

                            setCellValue(dpContBC.getInicial() + dpContBC.getTotal(), chBienContFinal,
                                    styleMoneyTotal, styleCenter2);

                        } else {
                            setCellValue(dpContBC.getInicial() + dpContBC.getTotal(), chBienContRetiros,
                                    styleMoneyTotal, styleCenter2);

                            chBienContFinal.setCellStyle(styleCenter2);
                        }
                        break;
                }
                // </editor-fold>

                anioInicioBienesCont++;
            }

        }
        // </editor-fold>

        int finFilt = rowNum - 1;
        sheet.setAutoFilter(new CellRangeAddress(inicioFilt, finFilt, 0, (colNumBC - 2)));
        sheet.createFreezePane(4, 5, 4, 5);

        // <editor-fold defaultstate="collapsed" desc=" A N C H O D E C O L U M N A S ">
        for (int contCol = 0; contCol < colNum; contCol++) {
            sheet.autoSizeColumn(contCol);
        }
        // Col Descripción = doble de la col Id (col 0)
        sheet.setColumnWidth(3, sheet.getColumnWidth(0) * 2);
        // </editor-fold>
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="HOJA BIENES TRIBUTARIOS">
        Sheet sheetBT = wb.createSheet("BIENES TRIB");
        int rowNumBT = 1;
        int colNumBT = 0;

        // <editor-fold defaultstate="collapsed" desc=" C A B E C E R A ">
        Row rTotalesResumenBT = sheetBT.createRow(rowNumBT);
        rowNumBT++;

        Row rTituloBT = sheetBT.createRow(rowNumBT);
        rTituloBT.setHeightInPoints(sheetBT.getDefaultRowHeightInPoints() * 3);

        Cell cTituloBT = rTituloBT.createCell(colNumBT);
        cTituloBT.setCellStyle(styleHeader);
        cTituloBT.setCellValue("DEPRECIACIONES   -   " + razonSocial);
        // cTituloBT.setCellValue("D E P R E C I A C I O N E S - C O R P O R A C I O N G
        // E R E N C I A. C O M S. A. C.");
        sheetBT.addMergedRegion(CellRangeAddress.valueOf("A3:V3"));

        rowNumBT++;

        colNumBT = 0;
        Row rColumnasBT = sheetBT.createRow(rowNumBT);

        Cell cColumnaIdBT = rColumnasBT.createCell(colNumBT);
        cColumnaIdBT.setCellStyle(styleSubHeader);
        cColumnaIdBT.setCellValue("Id");
        colNumBT++;

        Cell cColumnaRucBT = rColumnasBT.createCell(colNumBT);
        cColumnaRucBT.setCellStyle(styleSubHeader);
        cColumnaRucBT.setCellValue("RUC/DNI Proveedor");
        colNumBT++;

        Cell cColumnaProveedorBT = rColumnasBT.createCell(colNumBT);
        cColumnaProveedorBT.setCellStyle(styleSubHeader);
        cColumnaProveedorBT.setCellValue("Proveedor");
        colNumBT++;

        Cell cColumnaDescripcionBT = rColumnasBT.createCell(colNumBT);
        cColumnaDescripcionBT.setCellStyle(styleSubHeader);
        cColumnaDescripcionBT.setCellValue("Descripción");
        colNumBT++;

        Cell cColumnaMarcaBT = rColumnasBT.createCell(colNum);
        cColumnaMarcaBT.setCellStyle(styleSubHeader);
        cColumnaMarcaBT.setCellValue("Marca");
        colNum++;

        Cell cColumnaModeloBT = rColumnasBT.createCell(colNum);
        cColumnaModeloBT.setCellStyle(styleSubHeader);
        cColumnaModeloBT.setCellValue("Modelo");
        colNum++;

        Cell cColumnaSeriePlacaBT = rColumnasBT.createCell(colNum);
        cColumnaSeriePlacaBT.setCellStyle(styleSubHeader);
        cColumnaSeriePlacaBT.setCellValue("Serie y/o placa");
        colNum++;

        Cell cColumnasTipoBT = rColumnasBT.createCell(colNumBT);
        cColumnasTipoBT.setCellStyle(styleSubHeader);
        cColumnasTipoBT.setCellValue("Tipo");
        colNumBT++;

        Cell cColumnasDpContBT = rColumnasBT.createCell(colNumBT);
        cColumnasDpContBT.setCellStyle(styleSubHeader);
        cColumnasDpContBT.setCellValue("D.Cont.");
        colNumBT++;

        Cell cColumnasDpTribBT = rColumnasBT.createCell(colNumBT);
        cColumnasDpTribBT.setCellStyle(styleSubHeader);
        cColumnasDpTribBT.setCellValue("D.Trib.");
        colNumBT++;

        Cell cColumnasCuentaBT = rColumnasBT.createCell(colNumBT);
        cColumnasCuentaBT.setCellStyle(styleSubHeader);
        cColumnasCuentaBT.setCellValue("Cuenta");
        colNumBT++;

        Cell cColumnasDocumentoBT = rColumnasBT.createCell(colNumBT);
        cColumnasDocumentoBT.setCellStyle(styleSubHeader);
        cColumnasDocumentoBT.setCellValue("Documento");
        colNumBT++;

        Cell cColumnasFechaCompraBT = rColumnasBT.createCell(colNumBT);
        cColumnasFechaCompraBT.setCellStyle(styleSubHeader);
        cColumnasFechaCompraBT.setCellValue("F. Compra");
        colNumBT++;

        Cell cColumnasFechaInicioBT = rColumnasBT.createCell(colNumBT);
        cColumnasFechaInicioBT.setCellStyle(styleSubHeader);
        cColumnasFechaInicioBT.setCellValue("F. Inicio");
        colNumBT++;

        Cell cColumnasFechaFinContBT = rColumnasBT.createCell(colNumBT);
        cColumnasFechaFinContBT.setCellStyle(styleSubHeader);
        cColumnasFechaFinContBT.setCellValue("F. Fin Cont.");
        colNumBT++;

        Cell cColumnasFechaFinTribBT = rColumnasBT.createCell(colNumBT);
        cColumnasFechaFinTribBT.setCellStyle(styleSubHeader);
        cColumnasFechaFinTribBT.setCellValue("F. Fin Trib.");
        colNumBT++;

        Cell cColumnasFechaBajaBT = rColumnasBT.createCell(colNumBT);
        cColumnasFechaBajaBT.setCellStyle(styleSubHeader);
        cColumnasFechaBajaBT.setCellValue("F. Baja");
        colNumBT++;

        Cell cColumnasPrecioUnitarioBT = rColumnasBT.createCell(colNumBT);
        cColumnasPrecioUnitarioBT.setCellStyle(styleSubHeader);
        cColumnasPrecioUnitarioBT.setCellValue("P.U.");
        colNumBT++;

        Cell cColumnasCantidadBT = rColumnasBT.createCell(colNumBT);
        cColumnasCantidadBT.setCellStyle(styleSubHeader);
        cColumnasCantidadBT.setCellValue("Cant.");
        colNumBT++;

        Cell cColumnasMonedaBT = rColumnasBT.createCell(colNumBT);
        cColumnasMonedaBT.setCellStyle(styleSubHeader);
        cColumnasMonedaBT.setCellValue("Moneda");
        colNumBT++;

        Cell cColumnasTipoCambioBT = rColumnasBT.createCell(colNumBT);
        cColumnasTipoCambioBT.setCellStyle(styleSubHeader);
        cColumnasTipoCambioBT.setCellValue("TC");
        colNumBT++;

        Cell cColumnasCostoInicialBT = rColumnasBT.createCell(colNumBT);
        cColumnasCostoInicialBT.setCellStyle(styleSubHeader);
        cColumnasCostoInicialBT.setCellValue("Costo inicial");
        colNumBT++;

        int rowNumResumenBT = 3;

        int colNumResumenBT = CellReference.convertColStringToIndex("X");

        // Jalar las fechas e iteraciones:
        double inicialTotalBT = 0.0;
        double periodoTotalBT = 0.0;
        double retirosTotalBT = 0.0;
        double finalTotalBT = 0.0;

        while (anioInicioBienesTrib <= anioFinBienesTrib) {
            rowNumResumenBT = 3;

            Cell chBienBT = rTituloBT.createCell(colNumResumenBT);
            chBienBT.setCellStyle(styleSubHeader);
            chBienBT.setCellValue("DEPRECIACIÓN " + anioInicioBienesTrib);

            String nameColIniBT = CellReference.convertNumToColString(colNumResumenBT);

            Cell chInicialBT = rColumnasBT.createCell(colNumResumenBT);
            chInicialBT.setCellStyle(styleSubHeader);
            chInicialBT.setCellValue("INICIAL");
            Cell chInicialTotalBT = rTotalesResumenBT.createCell(colNumResumenBT);
            colNumResumenBT++;

            Cell chPeriodoBT = rColumnasBT.createCell(colNumResumenBT);
            chPeriodoBT.setCellStyle(styleSubHeader);
            chPeriodoBT.setCellValue("PERIODO");
            Cell chPeriodoTotalBT = rTotalesResumenBT.createCell(colNumResumenBT);
            colNumResumenBT++;

            Cell chRetirosBT = rColumnasBT.createCell(colNumResumenBT);
            chRetirosBT.setCellStyle(styleSubHeader);
            chRetirosBT.setCellValue("RETIROS");
            Cell chRetirosTotalBT = rTotalesResumenBT.createCell(colNumResumenBT);
            colNumResumenBT++;

            Cell chFinalBT = rColumnasBT.createCell(colNumResumenBT);
            chFinalBT.setCellStyle(styleSubHeader);
            chFinalBT.setCellValue("FINAL");
            Cell chFinalTotalBT = rTotalesResumenBT.createCell(colNumResumenBT);

            String nameColFinBT = CellReference.convertNumToColString(colNumResumenBT);

            sheetBT.addMergedRegion(CellRangeAddress
                    .valueOf(nameColIniBT + rowNumResumenBT + ":" + nameColFinBT + rowNumResumenBT));
            PoiUtils.addBorders(
                    CellRangeAddress.valueOf(nameColIniBT + rowNumResumenBT + ":" + nameColFinBT + rowNumResumenBT),
                    BorderStyle.THIN, IndexedColors.WHITE, sheetBT);

            colNumResumenBT++;
            colNumResumenBT++;

            for (ActivoDTO bien : list) {
                ActivoDepreciacionDTO dpContBT = getDepTributariaByYear(bien, anioInicioBienesTrib);

                inicialTotalBT += dpContBT.getInicial();
                periodoTotalBT += dpContBT.getTotal();

                switch (bien.getIdEstado()) {
                    case 1:
                        finalTotalBT += dpContBT.getInicial() + dpContBT.getTotal();
                        break;
                    case 2:
                        if (getAnioFromDateString(bien.getFechaBaja()) != anioInicioBienesTrib) {
                            finalTotalBT += dpContBT.getInicial() + dpContBT.getTotal();
                        } else {
                            retirosTotalBT += dpContBT.getInicial() + dpContBT.getTotal();
                        }
                        break;
                }

            }

            setCellValue(inicialTotalBT, chInicialTotalBT, styleMoneyTotal, styleCenter2);
            setCellValue(periodoTotalBT, chPeriodoTotalBT, styleMoneyTotal, styleCenter2);
            setCellValue(retirosTotalBT, chRetirosTotalBT, styleMoneyTotal, styleCenter2);
            setCellValue(finalTotalBT, chFinalTotalBT, styleMoneyTotal, styleCenter2);

            inicialTotalBT = 0.0;
            periodoTotalBT = 0.0;
            retirosTotalBT = 0.0;
            finalTotalBT = 0.0;

            // Iterar los bienes y sacar los totales
            anioInicioBienesTrib++;
        }
        // </editor-fold>

        rowNumBT++;
        int inicioFiltBT = rowNumBT;
        rowNumBT++;

        // <editor-fold defaultstate="collapsed" desc=" L L E N A D O D E L A D A T A ">
        for (ActivoDTO bien : list) {

            // MOSTRAR SOLO LOS BIENES QUE TENGAN DEPRECIACIÓN TRIBUTARIA
            if (bien.getPorcentajeDepreciacionTributaria() != 0) {
                // <editor-fold defaultstate="collapsed" desc="Activo Tributario">
                Row dataBT = sheetBT.createRow(rowNumBT);
                colNumBT = 0;

                Cell cIdBT = dataBT.createCell(colNumBT);
                cIdBT.setCellValue(bien.getId());
                cIdBT.setCellStyle(styleLeft);
                colNumBT++;

                Cell cIdProveedorBT = dataBT.createCell(colNumBT);
                cIdProveedorBT.setCellValue(bien.getIdProveedor());
                cIdProveedorBT.setCellStyle(styleCenter);
                colNumBT++;

                Cell cProveedorBT = dataBT.createCell(colNumBT);
                cProveedorBT.setCellValue(bien.getProveedor());
                cProveedorBT.setCellStyle(styleLeft);
                colNumBT++;

                Cell cDescripcionBT = dataBT.createCell(colNumBT);
                cDescripcionBT.setCellValue(bien.getDescripcion());
                cDescripcionBT.setCellStyle(styleLeft);
                colNumBT++;

                Cell cMarcaBT = dataBT.createCell(colNumBT);
                cMarcaBT.setCellValue(bien.getMarca());
                cMarcaBT.setCellStyle(styleLeft);
                colNumBT++;

                Cell cModeloBT = dataBT.createCell(colNumBT);
                cModeloBT.setCellValue(bien.getModelo());
                cModeloBT.setCellStyle(styleLeft);
                colNumBT++;

                Cell cSeriePlacaBT = dataBT.createCell(colNumBT);
                cSeriePlacaBT.setCellValue(bien.getSeriePlaca());
                cSeriePlacaBT.setCellStyle(styleLeft);
                colNumBT++;

                Cell cTipoBT = dataBT.createCell(colNumBT);
                cTipoBT.setCellValue(bien.getTipo());
                cTipoBT.setCellStyle(styleLeft);
                colNumBT++;

                Cell cDpContBT = dataBT.createCell(colNumBT);
                cDpContBT.setCellValue(bien.getPorcentajeDepreciacionContable());
                cDpContBT.setCellStyle(styleCenter);
                colNumBT++;

                Cell cDpTribBT = dataBT.createCell(colNumBT);
                cDpTribBT.setCellValue(bien.getPorcentajeDepreciacionTributaria());
                cDpTribBT.setCellStyle(styleCenter);
                colNumBT++;

                Cell cCuentaBT = dataBT.createCell(colNumBT);
                cCuentaBT.setCellValue(bien.getCuenta());
                cCuentaBT.setCellStyle(styleLeft);
                colNumBT++;

                Cell cDocumentoBT = dataBT.createCell(colNumBT);
                cDocumentoBT.setCellValue(bien.getDocumento());
                cDocumentoBT.setCellStyle(styleLeft);
                colNumBT++;

                Cell cFechaCompraBT = dataBT.createCell(colNumBT);
                cFechaCompraBT.setCellStyle(styleCenter);
                colNumBT++;

                if (bien.getFechaCompra() != null) {
                    if (!bien.getFechaCompra().equals("")) {
                        cFechaCompraBT.setCellValue(stringToDate(bien.getFechaCompra()));
                        cFechaCompraBT.setCellStyle(styleDate);
                    }
                }

                Cell cFechaInicioBT = dataBT.createCell(colNumBT);
                cFechaInicioBT.setCellValue(stringToDate(bien.getFechaInicio()));
                cFechaInicioBT.setCellStyle(styleDate);
                colNumBT++;

                Cell cFechaFinContableBT = dataBT.createCell(colNumBT);
                cFechaFinContableBT.setCellValue(stringToDate(bien.getFechaFinContable()));
                cFechaFinContableBT.setCellStyle(styleDate);
                colNumBT++;

                Cell cFechaFinTributariaBT = dataBT.createCell(colNumBT);
                cFechaFinTributariaBT.setCellValue(stringToDate(bien.getFechaFinTributaria()));
                cFechaFinTributariaBT.setCellStyle(styleDate);
                colNumBT++;

                Cell cFechaBajaBT = dataBT.createCell(colNumBT);
                cFechaBajaBT.setCellStyle(styleCenter);
                colNumBT++;

                if (bien.getIdEstado() == 2) {
                    if (bien.getFechaBaja() != null) {
                        if (!bien.getFechaBaja().equals("")) {
                            cFechaBajaBT.setCellValue(stringToDate(bien.getFechaBaja()));
                            cFechaBajaBT.setCellStyle(styleDate);
                        }
                    }
                }

                Cell cPrecioUnitarioBT = dataBT.createCell(colNumBT);
                cPrecioUnitarioBT.setCellValue(bien.getPrecioUnitario());
                cPrecioUnitarioBT.setCellStyle(styleMoneyTotal);
                colNumBT++;

                Cell cCantidadBT = dataBT.createCell(colNumBT);
                cCantidadBT.setCellValue(bien.getCantidad());
                cCantidadBT.setCellStyle(styleCenter);
                colNumBT++;

                Cell cMonedaBT = dataBT.createCell(colNumBT);
                cMonedaBT.setCellValue(bien.getMoneda());
                cMonedaBT.setCellStyle(styleCenter);
                colNumBT++;

                Cell cTipoCambioBT = dataBT.createCell(colNumBT);
                cTipoCambioBT.setCellValue(bien.getTipoCambio());
                cTipoCambioBT.setCellStyle(styleCenter);
                colNumBT++;

                Cell cCostoInicialBT = dataBT.createCell(colNumBT);
                cCostoInicialBT.setCellValue(bien.getCostoInicial());
                cCostoInicialBT.setCellStyle(styleMoneyTotal);
                colNumBT++;
                colNumBT++;

                rowNumBT++;
                // </editor-fold>

                // LISTAR TODAS LAS DEPRECIACIONES CONTABLES
                anioInicioBienesTrib = hojaAnioI;
                anioFinBienesTrib = hojaAnioF;

                // Jalar las fechas e iteraciones:
                while (anioInicioBienesTrib <= anioFinBienesTrib) {

                    // <editor-fold defaultstate="collapsed" desc="Llenado de data súper resumen">
                    ActivoDepreciacionDTO dpBT = getDepTributariaByYear(bien, anioInicioBienesTrib);

                    Cell cInicialBT = dataBT.createCell(colNumBT);
                    setCellValue(dpBT.getInicial(), cInicialBT, styleMoneyTotal, styleCenter2);
                    colNumBT++;

                    Cell chPeriodoBT = dataBT.createCell(colNumBT);
                    setCellValue(dpBT.getTotal(), chPeriodoBT, styleMoneyTotal, styleCenter2);
                    colNumBT++;

                    Cell cRetirosBT = dataBT.createCell(colNumBT);
                    colNumBT++;

                    Cell cFinalBT = dataBT.createCell(colNumBT);
                    colNumBT++;
                    colNumBT++;

                    switch (bien.getIdEstado()) {
                        case 1:
                            cRetirosBT.setCellStyle(styleCenter2);

                            setCellValue(dpBT.getInicial() + dpBT.getTotal(), cFinalBT, styleMoneyTotal,
                                    styleCenter2);
                            break;
                        case 2:
                            if (getAnioFromDateString(bien.getFechaBaja()) != anioInicioBienesTrib) {
                                cRetirosBT.setCellStyle(styleCenter2);

                                setCellValue(dpBT.getInicial() + dpBT.getTotal(), cFinalBT, styleMoneyTotal,
                                        styleCenter2);

                            } else {
                                setCellValue(dpBT.getInicial() + dpBT.getTotal(), cRetirosBT, styleMoneyTotal,
                                        styleCenter2);

                                cFinalBT.setCellStyle(styleCenter2);
                            }
                            break;
                    }
                    // </editor-fold>

                    anioInicioBienesTrib++;
                }
            }

        }
        // </editor-fold>

        int finFiltBT = rowNumBT - 1;
        sheetBT.setAutoFilter(new CellRangeAddress(inicioFiltBT, finFiltBT, 0, (colNumResumenBT - 2)));
        sheetBT.createFreezePane(4, 5, 4, 5);

        // <editor-fold defaultstate="collapsed" desc=" A N C H O D E C O L U M N A S ">
        for (int contCol = 0; contCol < colNumBT; contCol++) {
            sheetBT.autoSizeColumn(contCol);
        }
        // Col Descripción = doble de la col Id (col 0)
        sheetBT.setColumnWidth(3, sheetBT.getColumnWidth(0) * 2);
        // </editor-fold>

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="HOJAS DE DEPRECIACIONES">
        while (hojaAnioI <= hojaAnioF) {
            Sheet sheetDp = wb.createSheet(String.valueOf(hojaAnioI));

            int rowNumDp = 0;
            int colNumDp = 0;

            // TOTALES
            Row rowTotales = sheetDp.createRow(rowNumDp);

            // <editor-fold defaultstate="collapsed" desc="VARIABLES PARA TOTALES">
            double saldoInicialTotal = 0.0;
            double comprasTotal = 0.0;
            double retirosTotal = 0.0;
            double saldoFinalTotal = 0.0;

            double acumuladoDpContTotal = 0.0;
            double eneDpContTotal = 0.0;
            double febDpContTotal = 0.0;
            double marDpContTotal = 0.0;
            double abrDpContTotal = 0.0;
            double mayDpContTotal = 0.0;
            double junDpContTotal = 0.0;
            double julDpContTotal = 0.0;
            double agoDpContTotal = 0.0;
            double sepDpContTotal = 0.0;
            double octDpContTotal = 0.0;
            double novDpContTotal = 0.0;
            double decDpContTotal = 0.0;
            double totalDpContTotal = 0.0;
            double retirosDpContTotal = 0.0;
            double finalDpContTotal = 0.0;
            double activoFijoDpContTotal = 0.0;

            double acumuladoDpTribTotal = 0.0;
            double eneDpTribTotal = 0.0;
            double febDpTribTotal = 0.0;
            double marDpTribTotal = 0.0;
            double abrDpTribTotal = 0.0;
            double mayDpTribTotal = 0.0;
            double junDpTribTotal = 0.0;
            double julDpTribTotal = 0.0;
            double agoDpTribTotal = 0.0;
            double sepDpTribTotal = 0.0;
            double octDpTribTotal = 0.0;
            double novDpTribTotal = 0.0;
            double decDpTribTotal = 0.0;
            double totalDpTribTotal = 0.0;
            double retirosDpTribTotal = 0.0;
            double finalDpTribTotal = 0.0;
            double activoFijoDpTribTotal = 0.0;

            double adicionTotal = 0.0;
            double deduccionTotal = 0.0;
            double analisisTotal = 0.0;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="TOTALES">
            colNumDp = CellReference.convertColStringToIndex("P");
            Cell cSaldoInicialTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cComprasTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cRetirosTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cSaldoFinalTotal = rowTotales.createCell(colNumDp);
            colNumDp++;

            colNumDp = CellReference.convertColStringToIndex("U");
            Cell cAcumuladoDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cEneDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cFebDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cMarDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cAbrDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cMayDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cJunDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cJulDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cAgoDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cSepDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cOctDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cNovDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cDecDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cTotalDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cRetirosDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cFinalDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cActivoFijoDpContTotal = rowTotales.createCell(colNumDp);
            colNumDp++;

            colNumDp = CellReference.convertColStringToIndex("AM");
            Cell cAcumuladoDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cEneDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cFebDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cMarDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cAbrDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cMayDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cJunDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cJulDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cAgoDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cSepDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cOctDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cNovDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cDecDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cTotalDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cRetirosDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cFinalDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cActivoFijoDpTribTotal = rowTotales.createCell(colNumDp);
            colNumDp++;

            colNumDp = CellReference.convertColStringToIndex("BE");
            Cell cAdicionTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cDeduccionTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            Cell cAnalisisTotal = rowTotales.createCell(colNumDp);
            colNumDp++;
            // </editor-fold>

            rowNumDp = 2;

            // <editor-fold defaultstate="collapsed" desc="CABECERAS DEPRECIACIONES">
            Row cabeceraDp = sheetDp.createRow(rowNumDp);
            cabeceraDp.setHeightInPoints(sheetDp.getDefaultRowHeightInPoints() * 3);

            colNumDp = CellReference.convertColStringToIndex("A");
            Cell cellSubCabDatosGenerales = cabeceraDp.createCell(colNumDp);
            cellSubCabDatosGenerales.setCellStyle(styleHeader);
            cellSubCabDatosGenerales.setCellValue("DATOS GENERALES  -  " + razonSocial);
            sheetDp.addMergedRegion(CellRangeAddress.valueOf("A3:N3"));

            colNumDp = CellReference.convertColStringToIndex("P");
            Cell cellSubCabActivo = cabeceraDp.createCell(colNumDp);
            cellSubCabActivo.setCellStyle(styleHeader);
            cellSubCabActivo.setCellValue("A C T I V O");
            sheetDp.addMergedRegion(CellRangeAddress.valueOf("P3:S3"));

            colNumDp = CellReference.convertColStringToIndex("U");
            Cell cellCabeceraDp = cabeceraDp.createCell(colNumDp);
            cellCabeceraDp.setCellStyle(styleHeader);
            cellCabeceraDp.setCellValue("DEPRECIACIONES CONTABLES - AÑO " + hojaAnioI);
            sheetDp.addMergedRegion(CellRangeAddress.valueOf("U3:AK3"));

            colNumDp = CellReference.convertColStringToIndex("AM");
            Cell cellCabeceraDpTrib = cabeceraDp.createCell(colNumDp);
            cellCabeceraDpTrib.setCellStyle(styleHeader);
            cellCabeceraDpTrib.setCellValue("DEPRECIACIONES TRIBUTARIAS - AÑO " + hojaAnioI);
            sheetDp.addMergedRegion(CellRangeAddress.valueOf("AM3:BC3"));

            colNumDp = CellReference.convertColStringToIndex("BE");
            Cell cellCabeceraAnalisis = cabeceraDp.createCell(colNumDp);
            cellCabeceraAnalisis.setCellStyle(styleHeader);
            cellCabeceraAnalisis.setCellValue("ANÁLISIS " + hojaAnioI);
            sheetDp.addMergedRegion(CellRangeAddress.valueOf("BE3:BG3"));
            // </editor-fold>

            rowNumDp++;

            // <editor-fold defaultstate="collapsed" desc="SUB CABECERAS DEPRECIACIONES">
            // SUB CABECERAS DEPRECIACIONES CONTABLES
            colNumDp = 0;
            Row subHeadersDp = sheetDp.createRow(rowNumDp);

            Cell cellSubCabeceraDp = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp.setCellStyle(styleSubHeader);
            cellSubCabeceraDp.setCellValue("Id");
            colNumDp++;

            Cell cellSubCabeceraDpDepCont = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpDepCont.setCellStyle(styleSubHeader);
            cellSubCabeceraDpDepCont.setCellValue("D.Cont.");
            colNumDp++;

            Cell cellSubCabeceraDpDepTrib = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpDepTrib.setCellStyle(styleSubHeader);
            cellSubCabeceraDpDepTrib.setCellValue("D.Trib.");
            colNumDp++;

            Cell cellSubCabeceraDp1 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp1.setCellStyle(styleSubHeader);
            cellSubCabeceraDp1.setCellValue("Descripción");
            colNumDp++;

            Cell cellSubCabeceraDpMarca = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpMarca.setCellStyle(styleSubHeader);
            cellSubCabeceraDpMarca.setCellValue("Marca");
            colNumDp++;

            Cell cellSubCabeceraDpModelo = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpModelo.setCellStyle(styleSubHeader);
            cellSubCabeceraDpModelo.setCellValue("Modelo");
            colNumDp++;

            Cell cellSubCabeceraDpSeriePlaca = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpSeriePlaca.setCellStyle(styleSubHeader);
            cellSubCabeceraDpSeriePlaca.setCellValue("Serie y/o placa");
            colNumDp++;

            Cell cellSubCabeceraDpFCompra = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpFCompra.setCellStyle(styleSubHeader);
            cellSubCabeceraDpFCompra.setCellValue("F.Compra");
            colNumDp++;

            Cell cellSubCabeceraDpFInicio = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpFInicio.setCellStyle(styleSubHeader);
            cellSubCabeceraDpFInicio.setCellValue("F.Inicio");
            colNumDp++;

            Cell cellSubCabeceraDpfFinCont = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpfFinCont.setCellStyle(styleSubHeader);
            cellSubCabeceraDpfFinCont.setCellValue("F. Fin Cont.");
            colNumDp++;

            Cell cellSubCabeceraDpTiCont = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpTiCont.setCellStyle(styleSubHeader);
            cellSubCabeceraDpTiCont.setCellValue("T. Cont");
            colNumDp++;

            Cell cellSubCabeceraDpfFinTrib = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpfFinTrib.setCellStyle(styleSubHeader);
            cellSubCabeceraDpfFinTrib.setCellValue("F.Fin Trib.");
            colNumDp++;

            Cell cellSubCabeceraDpTiTrib = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpTiTrib.setCellStyle(styleSubHeader);
            cellSubCabeceraDpTiTrib.setCellValue("T. Trib");
            colNumDp++;

            Cell cellSubCabeceraDpfBaja = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpfBaja.setCellStyle(styleSubHeader);
            cellSubCabeceraDpfBaja.setCellValue("F. Baja");
            colNumDp++;

            // Columna en blanco
            colNumDp++;

            Cell cellSubCabeceraDpSaldoInicial = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpSaldoInicial.setCellStyle(styleSubHeader);
            cellSubCabeceraDpSaldoInicial.setCellValue("Saldo inicial");
            colNumDp++;

            Cell cellSubCabeceraDpCompras = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpCompras.setCellStyle(styleSubHeader);
            cellSubCabeceraDpCompras.setCellValue("Compras");
            colNumDp++;

            Cell cellSubCabeceraDpRetiros = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpRetiros.setCellStyle(styleSubHeader);
            cellSubCabeceraDpRetiros.setCellValue("Retiros");
            colNumDp++;

            Cell cellSubCabeceraDpSaldoFinal = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpSaldoFinal.setCellStyle(styleSubHeader);
            cellSubCabeceraDpSaldoFinal.setCellValue("Saldo final");
            colNumDp++;

            // Columna en blanco
            colNumDp++;

            Cell cellSubCabeceraDp3 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp3.setCellStyle(styleSubHeader);
            cellSubCabeceraDp3.setCellValue("Inicial");
            colNumDp++;

            Cell cellSubCabeceraDp4 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp4.setCellStyle(styleSubHeader);
            cellSubCabeceraDp4.setCellValue("ene");
            colNumDp++;

            Cell cellSubCabeceraDp5 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp5.setCellStyle(styleSubHeader);
            cellSubCabeceraDp5.setCellValue("feb");
            colNumDp++;

            Cell cellSubCabeceraDp6 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp6.setCellStyle(styleSubHeader);
            cellSubCabeceraDp6.setCellValue("mar");
            colNumDp++;

            Cell cellSubCabeceraDp7 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp7.setCellStyle(styleSubHeader);
            cellSubCabeceraDp7.setCellValue("abr");
            colNumDp++;

            Cell cellSubCabeceraDp8 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp8.setCellStyle(styleSubHeader);
            cellSubCabeceraDp8.setCellValue("may");
            colNumDp++;

            Cell cellSubCabeceraDp9 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp9.setCellStyle(styleSubHeader);
            cellSubCabeceraDp9.setCellValue("jun");
            colNumDp++;

            Cell cellSubCabeceraDp10 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp10.setCellStyle(styleSubHeader);
            cellSubCabeceraDp10.setCellValue("jul");
            colNumDp++;

            Cell cellSubCabeceraDp11 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp11.setCellStyle(styleSubHeader);
            cellSubCabeceraDp11.setCellValue("ago");
            colNumDp++;

            Cell cellSubCabeceraDp12 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp12.setCellStyle(styleSubHeader);
            cellSubCabeceraDp12.setCellValue("sep");
            colNumDp++;

            Cell cellSubCabeceraDp13 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp13.setCellStyle(styleSubHeader);
            cellSubCabeceraDp13.setCellValue("oct");
            colNumDp++;

            Cell cellSubCabeceraDp14 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp14.setCellStyle(styleSubHeader);
            cellSubCabeceraDp14.setCellValue("nov");
            colNumDp++;

            Cell cellSubCabeceraDp15 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp15.setCellStyle(styleSubHeader);
            cellSubCabeceraDp15.setCellValue("dec");
            colNumDp++;

            Cell cellSubCabeceraDp16 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp16.setCellStyle(styleSubHeader);
            cellSubCabeceraDp16.setCellValue("Total");
            colNumDp++;

            Cell cellSubCabeceraDpContRetiros = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpContRetiros.setCellStyle(styleSubHeader);
            cellSubCabeceraDpContRetiros.setCellValue("Retiros");
            colNumDp++;

            Cell cellSubCabeceraDpContFinal = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpContFinal.setCellStyle(styleSubHeader);
            cellSubCabeceraDpContFinal.setCellValue("Final");
            colNumDp++;

            Cell cellSubCabeceraDp17 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp17.setCellStyle(styleSubHeader);
            cellSubCabeceraDp17.setCellValue("Activo fijo");
            colNumDp++;

            // Columna en blanco
            colNumDp++;

            // SUB CABECERAS DEPRECIACIONES TRIBUTARIAS
            Cell cellSubCabeceraDp18 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp18.setCellStyle(styleSubHeader);
            cellSubCabeceraDp18.setCellValue("Inicial");
            colNumDp++;

            Cell cellSubCabeceraDp19 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp19.setCellStyle(styleSubHeader);
            cellSubCabeceraDp19.setCellValue("ene");
            colNumDp++;

            Cell cellSubCabeceraDp20 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp20.setCellStyle(styleSubHeader);
            cellSubCabeceraDp20.setCellValue("feb");
            colNumDp++;

            Cell cellSubCabeceraDp21 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp21.setCellStyle(styleSubHeader);
            cellSubCabeceraDp21.setCellValue("mar");
            colNumDp++;

            Cell cellSubCabeceraDp22 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp22.setCellStyle(styleSubHeader);
            cellSubCabeceraDp22.setCellValue("abr");
            colNumDp++;

            Cell cellSubCabeceraDp23 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp23.setCellStyle(styleSubHeader);
            cellSubCabeceraDp23.setCellValue("may");
            colNumDp++;

            Cell cellSubCabeceraDp24 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp24.setCellStyle(styleSubHeader);
            cellSubCabeceraDp24.setCellValue("jun");
            colNumDp++;

            Cell cellSubCabeceraDp25 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp25.setCellStyle(styleSubHeader);
            cellSubCabeceraDp25.setCellValue("jul");
            colNumDp++;

            Cell cellSubCabeceraDp26 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp26.setCellStyle(styleSubHeader);
            cellSubCabeceraDp26.setCellValue("ago");
            colNumDp++;

            Cell cellSubCabeceraDp27 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp27.setCellStyle(styleSubHeader);
            cellSubCabeceraDp27.setCellValue("sep");
            colNumDp++;

            Cell cellSubCabeceraDp28 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp28.setCellStyle(styleSubHeader);
            cellSubCabeceraDp28.setCellValue("oct");
            colNumDp++;

            Cell cellSubCabeceraDp29 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp29.setCellStyle(styleSubHeader);
            cellSubCabeceraDp29.setCellValue("nov");
            colNumDp++;

            Cell cellSubCabeceraDp30 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp30.setCellStyle(styleSubHeader);
            cellSubCabeceraDp30.setCellValue("dec");
            colNumDp++;

            Cell cellSubCabeceraDp31 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp31.setCellStyle(styleSubHeader);
            cellSubCabeceraDp31.setCellValue("Total");
            colNumDp++;

            Cell cellSubCabeceraDpTribRetiros = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpTribRetiros.setCellStyle(styleSubHeader);
            cellSubCabeceraDpTribRetiros.setCellValue("Retiros");
            colNumDp++;

            Cell cellSubCabeceraDpTribFinal = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDpTribFinal.setCellStyle(styleSubHeader);
            cellSubCabeceraDpTribFinal.setCellValue("Final");
            colNumDp++;

            Cell cellSubCabeceraDp32 = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraDp32.setCellStyle(styleSubHeader);
            cellSubCabeceraDp32.setCellValue("Activo fijo");
            colNumDp++;

            // Columna en blanco
            colNumDp++;

            // SUB CABECERAS ANÁLISIS
            Cell cellSubCabeceraAnaAdicion = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraAnaAdicion.setCellStyle(styleSubHeader);
            cellSubCabeceraAnaAdicion.setCellValue("Adición");
            colNumDp++;
            Cell cellSubCabeceraAnaDeduccion = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraAnaDeduccion.setCellStyle(styleSubHeader);
            cellSubCabeceraAnaDeduccion.setCellValue("Deducción");
            colNumDp++;
            Cell cellSubCabeceraAnaTotal = subHeadersDp.createCell(colNumDp);
            cellSubCabeceraAnaTotal.setCellStyle(styleSubHeader);
            cellSubCabeceraAnaTotal.setCellValue("Total");
            colNumDp++;

            // </editor-fold>
            rowNumDp++;
            int inicioFiltDp = rowNumDp;
            rowNumDp++;

            // Agregar tipos de bienes no repetidos a la lista
            List<ActivoDepreciacionDTO> listResumen = new ArrayList<>();

            // LISTAR LAS DEPRECIACIONES DE LOS BIENES QUE CORRESPONDEN A ESTE AÑO
            for (ActivoDTO activo : list) {

                if (getAnioFromDateString(activo.getFechaCompra()) <= hojaAnioI) {

                    ActivoDepreciacionDTO dpCont = getDepContableByYear(activo, hojaAnioI);
                    ActivoDepreciacionDTO dpTrib = getDepTributariaByYear(activo, hojaAnioI);

                    if (listResumen.stream().noneMatch(o -> o.getIdBienTipo().equals(activo.getIdTipo()))) {
                        // <editor-fold defaultstate="collapsed" desc="SI EL TIPO DEL BIEN NO EXISTE EN
                        // EL ARREGLO DE RESÚMENES">
                        ActivoDepreciacionDTO objResumen = new ActivoDepreciacionDTO();
                        objResumen.setIdBienTipo(activo.getIdTipo());
                        objResumen.setIdBienTipoDescripcion(activo.getTipo());

                        if (dpCont.getIdCliente() != null) {
                            objResumen.setActivoSaldoInicial(dpCont.getActivoSaldoInicial());
                            objResumen.setActivoCompras(dpCont.getActivoCompras());
                            objResumen.setActivoRetiros(dpCont.getActivoRetiros());
                            objResumen.setActivoSaldoFinal(dpCont.getActivoSaldoFinal());
                        } else if (dpTrib.getIdCliente() != null) {
                            objResumen.setActivoSaldoInicial(dpTrib.getActivoSaldoInicial());
                            objResumen.setActivoCompras(dpTrib.getActivoCompras());
                            objResumen.setActivoRetiros(dpTrib.getActivoRetiros());
                            objResumen.setActivoSaldoFinal(dpTrib.getActivoSaldoFinal());
                        }

                        // RESUMEN - DEPRECIACIÓN CONTABLE
                        objResumen.setInicial(dpCont.getInicial());
                        objResumen.setEne(dpCont.getEne());
                        objResumen.setFeb(dpCont.getFeb());
                        objResumen.setMar(dpCont.getMar());
                        objResumen.setAbr(dpCont.getAbr());
                        objResumen.setMay(dpCont.getMay());
                        objResumen.setJun(dpCont.getJun());
                        objResumen.setJul(dpCont.getJul());
                        objResumen.setAgo(dpCont.getAgo());
                        objResumen.setSep(dpCont.getSep());
                        objResumen.setOct(dpCont.getOct());
                        objResumen.setNov(dpCont.getNov());
                        objResumen.setDec(dpCont.getDec());
                        objResumen.setTotal(dpCont.getTotal());
                        objResumen.setRetiros(dpCont.getRetiros());
                        objResumen.setSaldoFinal(dpCont.getSaldoFinal());
                        objResumen.setActivoFijo(dpCont.getActivoFijo());

                        // RESUMEN - DEPRECIACIÓN TRIBUTARIA
                        objResumen.setInicialTrib(dpTrib.getInicial());
                        objResumen.setEneTrib(dpTrib.getEne());
                        objResumen.setFebTrib(dpTrib.getFeb());
                        objResumen.setMarTrib(dpTrib.getMar());
                        objResumen.setAbrTrib(dpTrib.getAbr());
                        objResumen.setMayTrib(dpTrib.getMay());
                        objResumen.setJunTrib(dpTrib.getJun());
                        objResumen.setJulTrib(dpTrib.getJul());
                        objResumen.setAgoTrib(dpTrib.getAgo());
                        objResumen.setSepTrib(dpTrib.getSep());
                        objResumen.setOctTrib(dpTrib.getOct());
                        objResumen.setNovTrib(dpTrib.getNov());
                        objResumen.setDecTrib(dpTrib.getDec());
                        objResumen.setTotalTrib(dpTrib.getTotal());
                        objResumen.setRetirosTrib(dpTrib.getRetiros());
                        objResumen.setSaldoFinalTrib(dpTrib.getSaldoFinal());
                        objResumen.setActivoFijoTrib(dpTrib.getActivoFijo());

                        // RESUMEN - ANÁLISIS
                        if (activo.getPorcentajeDepreciacionContable() != 0
                                && activo.getPorcentajeDepreciacionTributaria() != 0) {
                            if (dpCont.getTotal() != 0 || dpTrib.getTotal() != 0) {
                                if (dpCont.getTotal() > dpTrib.getTotal()) {
                                    objResumen.setAdicion(dpCont.getTotal() - dpTrib.getTotal());
                                    objResumen.setTotalAnalisis(dpCont.getTotal() - dpTrib.getTotal());
                                }

                                if (dpCont.getTotal() < dpTrib.getTotal()) {
                                    objResumen.setDeduccion(dpTrib.getTotal() - dpCont.getTotal());
                                    objResumen.setTotalAnalisis(dpTrib.getTotal() - dpCont.getTotal());
                                }
                            }
                        }

                        listResumen.add(objResumen);
                        // </editor-fold>
                    } else {
                        // <editor-fold defaultstate="collapsed" desc="SI EL TIPO DEL BIEN SÍ EXISTE EN
                        // EL ARREGLO DE RESÚMENES">
                        ActivoDepreciacionDTO objResumen = listResumen.stream()
                                .filter(o -> o.getIdBienTipo().equals(activo.getIdTipo()))
                                .findFirst().get();
                        int index = listResumen.indexOf(objResumen);

                        if (dpCont.getIdCliente() != null) {
                            objResumen.setActivoSaldoInicial(
                                    objResumen.getActivoSaldoInicial() + dpCont.getActivoSaldoInicial());
                            objResumen.setActivoCompras(objResumen.getActivoCompras() + dpCont.getActivoCompras());
                            objResumen.setActivoRetiros(objResumen.getActivoRetiros() + dpCont.getActivoRetiros());
                            objResumen.setActivoSaldoFinal(
                                    objResumen.getActivoSaldoFinal() + dpCont.getActivoSaldoFinal());
                        } else if (dpTrib.getIdCliente() != null) {
                            objResumen.setActivoSaldoInicial(
                                    objResumen.getActivoSaldoInicial() + dpTrib.getActivoSaldoInicial());
                            objResumen.setActivoCompras(objResumen.getActivoCompras() + dpTrib.getActivoCompras());
                            objResumen.setActivoRetiros(objResumen.getActivoRetiros() + dpTrib.getActivoRetiros());
                            objResumen.setActivoSaldoFinal(
                                    objResumen.getActivoSaldoFinal() + dpTrib.getActivoSaldoFinal());
                        }

                        // RESUMEN - DEPRECIACIÓN CONTABLE
                        objResumen.setInicial(objResumen.getInicial() + dpCont.getInicial());
                        objResumen.setEne(objResumen.getEne() + dpCont.getEne());
                        objResumen.setFeb(objResumen.getFeb() + dpCont.getFeb());
                        objResumen.setMar(objResumen.getMar() + dpCont.getMar());
                        objResumen.setAbr(objResumen.getAbr() + dpCont.getAbr());
                        objResumen.setMay(objResumen.getMay() + dpCont.getMay());
                        objResumen.setJun(objResumen.getJun() + dpCont.getJun());
                        objResumen.setJul(objResumen.getJul() + dpCont.getJul());
                        objResumen.setAgo(objResumen.getAgo() + dpCont.getAgo());
                        objResumen.setSep(objResumen.getSep() + dpCont.getSep());
                        objResumen.setOct(objResumen.getOct() + dpCont.getOct());
                        objResumen.setNov(objResumen.getNov() + dpCont.getNov());
                        objResumen.setDec(objResumen.getDec() + dpCont.getDec());
                        objResumen.setTotal(objResumen.getTotal() + dpCont.getTotal());
                        objResumen.setRetiros(objResumen.getRetiros() + dpCont.getRetiros());
                        objResumen.setSaldoFinal(objResumen.getSaldoFinal() + dpCont.getSaldoFinal());
                        objResumen.setActivoFijo(objResumen.getActivoFijo() + dpCont.getActivoFijo());

                        // RESUMEN - DEPRECIACIÓN TRIBUTARIA
                        objResumen.setInicialTrib(objResumen.getInicialTrib() + dpTrib.getInicial());
                        objResumen.setEneTrib(objResumen.getEneTrib() + dpTrib.getEne());
                        objResumen.setFebTrib(objResumen.getFebTrib() + dpTrib.getFeb());
                        objResumen.setMarTrib(objResumen.getMarTrib() + dpTrib.getMar());
                        objResumen.setAbrTrib(objResumen.getAbrTrib() + dpTrib.getAbr());
                        objResumen.setMayTrib(objResumen.getMayTrib() + dpTrib.getMay());
                        objResumen.setJunTrib(objResumen.getJunTrib() + dpTrib.getJun());
                        objResumen.setJulTrib(objResumen.getJulTrib() + dpTrib.getJul());
                        objResumen.setAgoTrib(objResumen.getAgoTrib() + dpTrib.getAgo());
                        objResumen.setSepTrib(objResumen.getSepTrib() + dpTrib.getSep());
                        objResumen.setOctTrib(objResumen.getOctTrib() + dpTrib.getOct());
                        objResumen.setNovTrib(objResumen.getNovTrib() + dpTrib.getNov());
                        objResumen.setDecTrib(objResumen.getDecTrib() + dpTrib.getDec());
                        objResumen.setTotalTrib(objResumen.getTotalTrib() + dpTrib.getTotal());
                        objResumen.setRetirosTrib(objResumen.getRetirosTrib() + dpTrib.getRetiros());
                        objResumen.setSaldoFinalTrib(objResumen.getSaldoFinalTrib() + dpTrib.getSaldoFinal());
                        objResumen.setActivoFijoTrib(objResumen.getActivoFijoTrib() + dpTrib.getActivoFijo());

                        // RESUMEN - ANÁLISIS
                        if (activo.getPorcentajeDepreciacionContable() != 0
                                && activo.getPorcentajeDepreciacionTributaria() != 0) {
                            if (dpCont.getTotal() != 0 || dpTrib.getTotal() != 0) {
                                if (dpCont.getTotal() > dpTrib.getTotal()) {
                                    objResumen.setAdicion(
                                            objResumen.getAdicion() + (dpCont.getTotal() - dpTrib.getTotal()));
                                    objResumen.setTotalAnalisis(objResumen.getTotalAnalisis()
                                            + (dpCont.getTotal() - dpTrib.getTotal()));
                                }

                                if (dpCont.getTotal() < dpTrib.getTotal()) {
                                    objResumen.setDeduccion(
                                            objResumen.getDeduccion() + (dpTrib.getTotal() - dpCont.getTotal()));
                                    objResumen.setTotalAnalisis(objResumen.getTotalAnalisis()
                                            + (dpTrib.getTotal() - dpCont.getTotal()));
                                }
                            }
                        }

                        listResumen.set(index, objResumen);
                        // </editor-fold>
                    }

                    Row data = sheetDp.createRow(rowNumDp);
                    colNumDp = 0;

                    // <editor-fold defaultstate="collapsed" desc="Activo datos generales">
                    Cell cell = data.createCell(colNumDp);
                    cell.setCellValue(activo.getId());
                    cell.setCellStyle(styleLeft);
                    colNumDp++;

                    Cell cellDepCont = data.createCell(colNumDp);
                    cellDepCont.setCellValue(activo.getPorcentajeDepreciacionContable());
                    cellDepCont.setCellStyle(styleLeft);
                    colNumDp++;

                    Cell cellDepTrib = data.createCell(colNumDp);
                    cellDepTrib.setCellValue(activo.getPorcentajeDepreciacionTributaria());
                    cellDepTrib.setCellStyle(styleLeft);
                    colNumDp++;

                    Cell cell1 = data.createCell(colNumDp);
                    cell1.setCellValue(activo.getDescripcion());
                    cell1.setCellStyle(styleLeft);
                    colNumDp++;

                    Cell cellMarca = data.createCell(colNumDp);
                    cellMarca.setCellValue(activo.getMarca());
                    cellMarca.setCellStyle(styleLeft);
                    colNumDp++;

                    Cell cellModelo = data.createCell(colNumDp);
                    cellModelo.setCellValue(activo.getModelo());
                    cellModelo.setCellStyle(styleLeft);
                    colNumDp++;

                    Cell cellSeriePlaca = data.createCell(colNumDp);
                    cellSeriePlaca.setCellValue(activo.getSeriePlaca());
                    cellSeriePlaca.setCellStyle(styleLeft);
                    colNumDp++;

                    Cell cellFechaCompra = data.createCell(colNumDp);
                    cellFechaCompra.setCellStyle(styleLeft);
                    if (activo.getFechaCompra() != null) {
                        if (!activo.getFechaCompra().equals("")) {
                            cellFechaCompra.setCellValue(stringToDate(activo.getFechaCompra()));
                            cellFechaCompra.setCellStyle(styleDate);
                        }
                    }
                    colNumDp++;

                    Cell cellFechaInicio = data.createCell(colNumDp);
                    cellFechaInicio.setCellValue(stringToDate(activo.getFechaInicio()));
                    cellFechaInicio.setCellStyle(styleDate);
                    colNumDp++;

                    Cell cellFechaFinCont = data.createCell(colNumDp);
                    cellFechaFinCont.setCellValue(stringToDate(activo.getFechaFinContable()));
                    cellFechaFinCont.setCellStyle(styleDate);
                    colNumDp++;

                    Cell cellTiempoCont = data.createCell(colNumDp);
                    cellTiempoCont.setCellValue(activo.getPorcentajeDepreciacionContable() != 0
                            ? 100.0 / activo.getPorcentajeDepreciacionContable()
                            : 0);
                    cellTiempoCont.setCellStyle(styleLeft);
                    colNumDp++;

                    Cell cellFechaFinTrib = data.createCell(colNumDp);
                    cellFechaFinTrib.setCellValue(stringToDate(activo.getFechaFinTributaria()));
                    cellFechaFinTrib.setCellStyle(styleDate);
                    colNumDp++;

                    Cell cellTiempoTrib = data.createCell(colNumDp);
                    cellTiempoTrib.setCellValue(activo.getPorcentajeDepreciacionTributaria() != 0
                            ? 100.0 / activo.getPorcentajeDepreciacionTributaria()
                            : 0);
                    cellTiempoTrib.setCellStyle(styleLeft);
                    colNumDp++;

                    Cell cellFechaBaja = data.createCell(colNumDp);
                    cellFechaBaja.setCellStyle(styleLeft);
                    colNumDp++;

                    if (activo.getIdEstado() == 2) {
                        if (activo.getFechaBaja() != null) {
                            if (!activo.getFechaBaja().equals("")) {
                                cellFechaBaja.setCellValue(stringToDate(activo.getFechaBaja()));
                                cellFechaBaja.setCellStyle(styleDate);
                            }
                        }
                    }

                    // Columna en blanco
                    colNumDp++;

                    // ACTIVO
                    Cell cellSaldoInicial = data.createCell(colNumDp);
                    cellSaldoInicial.setCellStyle(styleCenter2);
                    colNumDp++;

                    Cell cellComprasAct = data.createCell(colNumDp);
                    cellComprasAct.setCellStyle(styleCenter2);
                    colNumDp++;

                    Cell cellRetirosAct = data.createCell(colNumDp);
                    cellRetirosAct.setCellStyle(styleCenter2);
                    colNumDp++;

                    Cell cellSaldoFinalAct = data.createCell(colNumDp);
                    cellSaldoFinalAct.setCellStyle(styleCenter2);
                    colNumDp++;

                    // <editor-fold defaultstate="collapsed" desc="Activo set values">
                    // DECIDIR QUÉ DEPRECIACIÓN (CONTABLE O TRIBUTARIA) VA A LLENAR LA PARTE DE
                    // ACTIVOS
                    if (dpCont.getIdCliente() != null) {
                        if (dpCont.getActivoSaldoInicial() != 0) {
                            cellSaldoInicial.setCellValue(dpCont.getActivoSaldoInicial());
                            cellSaldoInicial.setCellStyle(styleMoneyTotal);
                        }
                        saldoInicialTotal += dpCont.getActivoSaldoInicial();

                        if (dpCont.getActivoCompras() != 0) {
                            cellComprasAct.setCellValue(dpCont.getActivoCompras());
                            cellComprasAct.setCellStyle(styleMoneyTotal);
                        }
                        comprasTotal += dpCont.getActivoCompras();

                        if (dpCont.getActivoRetiros() != 0) {
                            cellRetirosAct.setCellValue(dpCont.getActivoRetiros());
                            cellRetirosAct.setCellStyle(styleMoneyTotal);
                        }
                        retirosTotal += dpCont.getActivoRetiros();

                        if (dpCont.getActivoSaldoFinal() != 0) {
                            cellSaldoFinalAct.setCellValue(dpCont.getActivoSaldoFinal());
                            cellSaldoFinalAct.setCellStyle(styleMoneyTotal);
                        }
                        saldoFinalTotal += dpCont.getActivoSaldoFinal();

                    } else if (dpTrib.getIdCliente() != null) {
                        if (dpTrib.getActivoSaldoInicial() != 0) {
                            cellSaldoInicial.setCellValue(dpTrib.getActivoSaldoInicial());
                            cellSaldoInicial.setCellStyle(styleMoneyTotal);
                        }
                        saldoInicialTotal += dpTrib.getActivoSaldoInicial();

                        if (dpTrib.getActivoCompras() != 0) {
                            cellComprasAct.setCellValue(dpTrib.getActivoCompras());
                            cellComprasAct.setCellStyle(styleMoneyTotal);
                        }
                        comprasTotal += dpTrib.getActivoCompras();

                        if (dpTrib.getActivoRetiros() != 0) {
                            cellRetirosAct.setCellValue(dpTrib.getActivoRetiros());
                            cellRetirosAct.setCellStyle(styleMoneyTotal);
                        }
                        retirosTotal += dpTrib.getActivoRetiros();

                        if (dpTrib.getActivoSaldoFinal() != 0) {
                            cellSaldoFinalAct.setCellValue(dpTrib.getActivoSaldoFinal());
                            cellSaldoFinalAct.setCellStyle(styleMoneyTotal);
                        }
                        saldoFinalTotal += dpTrib.getActivoSaldoFinal();

                    }
                    // </editor-fold>
                    // </editor-fold>
                    // Columna en blanco
                    colNumDp++;

                    // <editor-fold defaultstate="collapsed" desc="Depreciaciones contables - data">
                    Cell cAcumuladoDc = data.createCell(colNumDp);
                    if (dpCont.getInicial() == 0) {
                        cAcumuladoDc.setCellStyle(styleCenter2);
                    } else {
                        cAcumuladoDc.setCellValue(dpCont.getInicial());
                        cAcumuladoDc.setCellStyle(styleMoneyTotal);

                        // Acumulado Totales
                        acumuladoDpContTotal += dpCont.getInicial();
                    }
                    colNumDp++;

                    Cell cEneDc = data.createCell(colNumDp);
                    if (dpCont.getEne() == 0) {
                        cEneDc.setCellStyle(styleCenter2);
                    } else {
                        cEneDc.setCellValue(dpCont.getEne());
                        cEneDc.setCellStyle(styleMoney);

                        // Ene Totales
                        eneDpContTotal += dpCont.getEne();
                    }
                    colNumDp++;

                    Cell cFebDc = data.createCell(colNumDp);
                    if (dpCont.getFeb() == 0) {
                        cFebDc.setCellStyle(styleCenter2);
                    } else {
                        cFebDc.setCellValue(dpCont.getFeb());
                        cFebDc.setCellStyle(styleMoney);

                        // Feb Totales
                        febDpContTotal += dpCont.getFeb();
                    }
                    colNumDp++;

                    Cell cMarDc = data.createCell(colNumDp);
                    if (dpCont.getMar() == 0) {
                        cMarDc.setCellStyle(styleCenter2);
                    } else {
                        cMarDc.setCellValue(dpCont.getMar());
                        cMarDc.setCellStyle(styleMoney);

                        // Mar Totales
                        marDpContTotal += dpCont.getMar();
                    }
                    colNumDp++;

                    Cell cAbrDc = data.createCell(colNumDp);
                    if (dpCont.getAbr() == 0) {
                        cAbrDc.setCellStyle(styleCenter2);
                    } else {
                        cAbrDc.setCellValue(dpCont.getAbr());
                        cAbrDc.setCellStyle(styleMoney);

                        // Abr Totales
                        abrDpContTotal += dpCont.getAbr();
                    }
                    colNumDp++;

                    Cell cMayDc = data.createCell(colNumDp);
                    if (dpCont.getMay() == 0) {
                        cMayDc.setCellStyle(styleCenter2);
                    } else {
                        cMayDc.setCellValue(dpCont.getMay());
                        cMayDc.setCellStyle(styleMoney);

                        // May Totales
                        mayDpContTotal += dpCont.getMay();
                    }
                    colNumDp++;

                    Cell cJunDc = data.createCell(colNumDp);
                    if (dpCont.getJun() == 0) {
                        cJunDc.setCellStyle(styleCenter2);
                    } else {
                        cJunDc.setCellValue(dpCont.getJun());
                        cJunDc.setCellStyle(styleMoney);

                        // Jun Totales
                        junDpContTotal += dpCont.getJun();
                    }
                    colNumDp++;

                    Cell cJulDc = data.createCell(colNumDp);
                    if (dpCont.getJul() == 0) {
                        cJulDc.setCellStyle(styleCenter2);
                    } else {
                        cJulDc.setCellValue(dpCont.getJul());
                        cJulDc.setCellStyle(styleMoney);

                        // Jul Totales
                        julDpContTotal += dpCont.getJul();
                    }
                    colNumDp++;

                    Cell cAgoDc = data.createCell(colNumDp);
                    if (dpCont.getAgo() == 0) {
                        cAgoDc.setCellStyle(styleCenter2);
                    } else {
                        cAgoDc.setCellValue(dpCont.getAgo());
                        cAgoDc.setCellStyle(styleMoney);

                        // Ago Totales
                        agoDpContTotal += dpCont.getAgo();
                    }
                    colNumDp++;

                    Cell cSepDc = data.createCell(colNumDp);
                    if (dpCont.getSep() == 0) {
                        cSepDc.setCellStyle(styleCenter2);
                    } else {
                        cSepDc.setCellValue(dpCont.getSep());
                        cSepDc.setCellStyle(styleMoney);

                        // Sep Totales
                        sepDpContTotal += dpCont.getSep();
                    }
                    colNumDp++;

                    Cell cOctDc = data.createCell(colNumDp);
                    if (dpCont.getOct() == 0) {
                        cOctDc.setCellStyle(styleCenter2);
                    } else {
                        cOctDc.setCellValue(dpCont.getOct());
                        cOctDc.setCellStyle(styleMoney);

                        // Oct Totales
                        octDpContTotal += dpCont.getOct();
                    }
                    colNumDp++;

                    Cell cNovDc = data.createCell(colNumDp);
                    if (dpCont.getNov() == 0) {
                        cNovDc.setCellStyle(styleCenter2);
                    } else {
                        cNovDc.setCellValue(dpCont.getNov());
                        cNovDc.setCellStyle(styleMoney);

                        // Nov Totales
                        novDpContTotal += dpCont.getNov();
                    }
                    colNumDp++;

                    Cell cDecDc = data.createCell(colNumDp);
                    if (dpCont.getDec() == 0) {
                        cDecDc.setCellStyle(styleCenter2);
                    } else {
                        cDecDc.setCellValue(dpCont.getDec());
                        cDecDc.setCellStyle(styleMoney);

                        // Dec Totales
                        decDpContTotal += dpCont.getDec();
                    }
                    colNumDp++;

                    Cell cTotalDc = data.createCell(colNumDp);
                    if (dpCont.getTotal() == 0) {
                        cTotalDc.setCellStyle(styleCenter2);
                    } else {
                        cTotalDc.setCellValue(dpCont.getTotal());
                        cTotalDc.setCellStyle(styleMoneyTotal);

                        // Total Totales
                        totalDpContTotal += dpCont.getTotal();
                    }
                    colNumDp++;

                    Cell cRetirosDc = data.createCell(colNumDp);
                    if (dpCont.getRetiros() == 0) {
                        cRetirosDc.setCellStyle(styleCenter2);
                    } else {
                        cRetirosDc.setCellValue(dpCont.getRetiros());
                        cRetirosDc.setCellStyle(styleMoneyTotal);

                        // Retiros Totales
                        retirosDpContTotal += dpCont.getRetiros();
                    }
                    colNumDp++;

                    Cell cFinalDc = data.createCell(colNumDp);
                    if (dpCont.getSaldoFinal() == 0) {
                        cFinalDc.setCellStyle(styleCenter2);
                    } else {
                        cFinalDc.setCellValue(dpCont.getSaldoFinal());
                        cFinalDc.setCellStyle(styleMoneyTotal);

                        // Saldo Final Totales
                        finalDpContTotal += dpCont.getSaldoFinal();
                    }
                    colNumDp++;

                    Cell cActivoFijoDc = data.createCell(colNumDp);
                    if (dpCont.getActivoFijo() == 0) {
                        cActivoFijoDc.setCellStyle(styleCenter2);
                    } else {
                        cActivoFijoDc.setCellValue(dpCont.getActivoFijo());
                        cActivoFijoDc.setCellStyle(styleMoneyTotal);

                        // Activo Fijo Totales
                        activoFijoDpContTotal += dpCont.getActivoFijo();
                    }
                    colNumDp++;

                    // </editor-fold>
                    // Columna en blanco
                    colNumDp++;

                    // <editor-fold defaultstate="collapsed" desc="Depreciaciones tributarias -
                    // data">
                    Cell cAcumuladoDt = data.createCell(colNumDp);
                    if (dpTrib.getInicial() == 0) {
                        cAcumuladoDt.setCellStyle(styleCenter2);
                    } else {
                        cAcumuladoDt.setCellValue(dpTrib.getInicial());
                        cAcumuladoDt.setCellStyle(styleMoneyTotal);
                        // Acumulado Totales
                        acumuladoDpTribTotal += dpTrib.getInicial();
                    }
                    colNumDp++;

                    Cell cEneDt = data.createCell(colNumDp);
                    if (dpTrib.getEne() == 0) {
                        cEneDt.setCellStyle(styleCenter2);
                    } else {
                        cEneDt.setCellValue(dpTrib.getEne());
                        cEneDt.setCellStyle(styleMoney);
                        // Ene Totales
                        eneDpTribTotal += dpTrib.getEne();
                    }
                    colNumDp++;

                    Cell cFebDt = data.createCell(colNumDp);
                    if (dpTrib.getFeb() == 0) {
                        cFebDt.setCellStyle(styleCenter2);
                    } else {
                        cFebDt.setCellValue(dpTrib.getFeb());
                        cFebDt.setCellStyle(styleMoney);

                        // Feb Totales
                        febDpTribTotal += dpTrib.getFeb();
                    }
                    colNumDp++;

                    Cell cMarDt = data.createCell(colNumDp);
                    if (dpTrib.getMar() == 0) {
                        cMarDt.setCellStyle(styleCenter2);
                    } else {
                        cMarDt.setCellValue(dpTrib.getMar());
                        cMarDt.setCellStyle(styleMoney);

                        // Mar Totales
                        marDpTribTotal += dpTrib.getMar();
                    }
                    colNumDp++;

                    Cell cAbrDt = data.createCell(colNumDp);
                    if (dpTrib.getAbr() == 0) {
                        cAbrDt.setCellStyle(styleCenter2);
                    } else {
                        cAbrDt.setCellValue(dpTrib.getAbr());
                        cAbrDt.setCellStyle(styleMoney);

                        // Abr Totales
                        abrDpTribTotal += dpTrib.getAbr();
                    }
                    colNumDp++;

                    Cell cMayDt = data.createCell(colNumDp);
                    if (dpTrib.getMay() == 0) {
                        cMayDt.setCellStyle(styleCenter2);
                    } else {
                        cMayDt.setCellValue(dpTrib.getMay());
                        cMayDt.setCellStyle(styleMoney);

                        // May Totales
                        mayDpTribTotal += dpTrib.getMay();
                    }
                    colNumDp++;

                    Cell cJunDt = data.createCell(colNumDp);
                    if (dpTrib.getJun() == 0) {
                        cJunDt.setCellStyle(styleCenter2);
                    } else {
                        cJunDt.setCellValue(dpTrib.getJun());
                        cJunDt.setCellStyle(styleMoney);

                        // Jun Totales
                        junDpTribTotal += dpTrib.getJun();
                    }
                    colNumDp++;

                    Cell cJulDt = data.createCell(colNumDp);
                    if (dpTrib.getJul() == 0) {
                        cJulDt.setCellStyle(styleCenter2);
                    } else {
                        cJulDt.setCellValue(dpTrib.getJul());
                        cJulDt.setCellStyle(styleMoney);

                        // Jul Totales
                        julDpTribTotal += dpTrib.getJul();
                    }
                    colNumDp++;

                    Cell cAgoDt = data.createCell(colNumDp);
                    if (dpTrib.getAgo() == 0) {
                        cAgoDt.setCellStyle(styleCenter2);
                    } else {
                        cAgoDt.setCellValue(dpTrib.getAgo());
                        cAgoDt.setCellStyle(styleMoney);

                        // Ago Totales
                        agoDpTribTotal += dpTrib.getAgo();
                    }
                    colNumDp++;

                    Cell cSepDt = data.createCell(colNumDp);
                    if (dpTrib.getSep() == 0) {
                        cSepDt.setCellStyle(styleCenter2);
                    } else {
                        cSepDt.setCellValue(dpTrib.getSep());
                        cSepDt.setCellStyle(styleMoney);

                        // Sep Totales
                        sepDpTribTotal += dpTrib.getSep();
                    }
                    colNumDp++;

                    Cell cOctDt = data.createCell(colNumDp);
                    if (dpTrib.getOct() == 0) {
                        cOctDt.setCellStyle(styleCenter2);
                    } else {
                        cOctDt.setCellValue(dpTrib.getOct());
                        cOctDt.setCellStyle(styleMoney);

                        // Oct Totales
                        octDpTribTotal += dpTrib.getOct();
                    }
                    colNumDp++;

                    Cell cNovDt = data.createCell(colNumDp);
                    if (dpTrib.getNov() == 0) {
                        cNovDt.setCellStyle(styleCenter2);
                    } else {
                        cNovDt.setCellValue(dpTrib.getNov());
                        cNovDt.setCellStyle(styleMoney);

                        // Nov Totales
                        novDpTribTotal += dpTrib.getNov();
                    }
                    colNumDp++;

                    Cell cDecDt = data.createCell(colNumDp);
                    if (dpTrib.getDec() == 0) {
                        cDecDt.setCellStyle(styleCenter2);
                    } else {
                        cDecDt.setCellValue(dpTrib.getDec());
                        cDecDt.setCellStyle(styleMoney);

                        // Dec Totales
                        decDpTribTotal += dpTrib.getDec();
                    }
                    colNumDp++;

                    Cell cTotalDt = data.createCell(colNumDp);
                    if (dpTrib.getTotal() == 0) {
                        cTotalDt.setCellStyle(styleCenter2);
                    } else {
                        cTotalDt.setCellValue(dpTrib.getTotal());
                        cTotalDt.setCellStyle(styleMoneyTotal);

                        // Total Totales
                        totalDpTribTotal += dpTrib.getTotal();
                    }
                    colNumDp++;

                    Cell cRetirosDt = data.createCell(colNumDp);
                    if (dpTrib.getRetiros() == 0) {
                        cRetirosDt.setCellStyle(styleCenter2);
                    } else {
                        cRetirosDt.setCellValue(dpTrib.getRetiros());
                        cRetirosDt.setCellStyle(styleMoneyTotal);

                        // Retiros Totales
                        retirosDpTribTotal += dpTrib.getRetiros();
                    }
                    colNumDp++;

                    Cell cFinalDt = data.createCell(colNumDp);
                    if (dpTrib.getSaldoFinal() == 0) {
                        cFinalDt.setCellStyle(styleCenter2);
                    } else {
                        cFinalDt.setCellValue(dpTrib.getSaldoFinal());
                        cFinalDt.setCellStyle(styleMoneyTotal);

                        // Saldo Final Totales
                        finalDpTribTotal += dpTrib.getSaldoFinal();
                    }
                    colNumDp++;

                    Cell cActivoFijoDt = data.createCell(colNumDp);
                    if (dpTrib.getActivoFijo() == 0) {
                        cActivoFijoDt.setCellStyle(styleCenter2);
                    } else {
                        cActivoFijoDt.setCellValue(dpTrib.getActivoFijo());
                        cActivoFijoDt.setCellStyle(styleMoneyTotal);

                        // Activo Fijo Totales
                        activoFijoDpTribTotal += dpTrib.getActivoFijo();
                    }
                    colNumDp++;

                    // </editor-fold>
                    // Columna en blanco
                    colNumDp++;

                    // <editor-fold defaultstate="collapsed" desc="Análisis - data">
                    Cell cAdicion = data.createCell(colNumDp);
                    cAdicion.setCellStyle(styleMoney);
                    colNumDp++;

                    Cell cDeduccion = data.createCell(colNumDp);
                    cDeduccion.setCellStyle(styleMoney);
                    colNumDp++;

                    Cell cTotal = data.createCell(colNumDp);
                    cTotal.setCellStyle(styleMoneyTotal);
                    colNumDp++;

                    if (activo.getPorcentajeDepreciacionContable() != 0
                            && activo.getPorcentajeDepreciacionTributaria() != 0) {
                        if (dpCont.getTotal() != 0 || dpTrib.getTotal() != 0) {
                            if (dpCont.getTotal() > dpTrib.getTotal()) {
                                cAdicion.setCellValue(dpCont.getTotal() - dpTrib.getTotal());
                                cTotal.setCellValue(dpCont.getTotal() - dpTrib.getTotal());

                                // TOTALES
                                adicionTotal += dpCont.getTotal() - dpTrib.getTotal();
                                analisisTotal += dpCont.getTotal() - dpTrib.getTotal();
                            }

                            if (dpCont.getTotal() < dpTrib.getTotal()) {
                                cDeduccion.setCellValue(dpTrib.getTotal() - dpCont.getTotal());
                                cTotal.setCellValue(dpTrib.getTotal() - dpCont.getTotal());

                                // TOTALES
                                deduccionTotal += dpTrib.getTotal() - dpCont.getTotal();
                                analisisTotal += dpTrib.getTotal() - dpCont.getTotal();
                            }
                        }

                    }

                    // </editor-fold>
                    rowNumDp++;

                    // <editor-fold defaultstate="collapsed" desc="SET TOTAL VALUES">
                    // Set totales a las celdas
                    if (saldoInicialTotal != 0.0) {
                        cSaldoInicialTotal.setCellValue(saldoInicialTotal);
                        cSaldoInicialTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cSaldoInicialTotal.setCellStyle(styleCenter2);
                    }
                    if (comprasTotal != 0.0) {
                        cComprasTotal.setCellValue(comprasTotal);
                        cComprasTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cComprasTotal.setCellStyle(styleCenter2);
                    }
                    if (retirosTotal != 0.0) {
                        cRetirosTotal.setCellValue(retirosTotal);
                        cRetirosTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cRetirosTotal.setCellStyle(styleCenter2);
                    }
                    if (saldoFinalTotal != 0.0) {
                        cSaldoFinalTotal.setCellValue(saldoFinalTotal);
                        cSaldoFinalTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cSaldoFinalTotal.setCellStyle(styleCenter2);
                    }

                    // Set totales Dep Cont
                    if (acumuladoDpContTotal != 0.0) {
                        cAcumuladoDpContTotal.setCellValue(acumuladoDpContTotal);
                        cAcumuladoDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cAcumuladoDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (eneDpContTotal != 0.0) {
                        cEneDpContTotal.setCellValue(eneDpContTotal);
                        cEneDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cEneDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (febDpContTotal != 0.0) {
                        cFebDpContTotal.setCellValue(febDpContTotal);
                        cFebDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cFebDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (marDpContTotal != 0.0) {
                        cMarDpContTotal.setCellValue(marDpContTotal);
                        cMarDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cMarDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (abrDpContTotal != 0.0) {
                        cAbrDpContTotal.setCellValue(abrDpContTotal);
                        cAbrDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cAbrDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (mayDpContTotal != 0.0) {
                        cMayDpContTotal.setCellValue(mayDpContTotal);
                        cMayDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cMayDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (junDpContTotal != 0.0) {
                        cJunDpContTotal.setCellValue(junDpContTotal);
                        cJunDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cJunDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (julDpContTotal != 0.0) {
                        cJulDpContTotal.setCellValue(julDpContTotal);
                        cJulDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cJulDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (agoDpContTotal != 0.0) {
                        cAgoDpContTotal.setCellValue(agoDpContTotal);
                        cAgoDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cAgoDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (sepDpContTotal != 0.0) {
                        cSepDpContTotal.setCellValue(sepDpContTotal);
                        cSepDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cSepDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (octDpContTotal != 0.0) {
                        cOctDpContTotal.setCellValue(octDpContTotal);
                        cOctDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cOctDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (novDpContTotal != 0.0) {
                        cNovDpContTotal.setCellValue(novDpContTotal);
                        cNovDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cNovDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (decDpContTotal != 0.0) {
                        cDecDpContTotal.setCellValue(decDpContTotal);
                        cDecDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cDecDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (totalDpContTotal != 0.0) {
                        cTotalDpContTotal.setCellValue(totalDpContTotal);
                        cTotalDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cTotalDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (retirosDpContTotal != 0.0) {
                        cRetirosDpContTotal.setCellValue(retirosDpContTotal);
                        cRetirosDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cRetirosDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (finalDpContTotal != 0.0) {
                        cFinalDpContTotal.setCellValue(finalDpContTotal);
                        cFinalDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cFinalDpContTotal.setCellStyle(styleCenter2);
                    }
                    if (activoFijoDpContTotal != 0.0) {
                        cActivoFijoDpContTotal.setCellValue(activoFijoDpContTotal);
                        cActivoFijoDpContTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cActivoFijoDpContTotal.setCellStyle(styleCenter2);
                    }

                    // Set totales Dep Trib
                    if (acumuladoDpTribTotal != 0.0) {
                        cAcumuladoDpTribTotal.setCellValue(acumuladoDpTribTotal);
                        cAcumuladoDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cAcumuladoDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (eneDpTribTotal != 0.0) {
                        cEneDpTribTotal.setCellValue(eneDpTribTotal);
                        cEneDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cEneDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (febDpTribTotal != 0.0) {
                        cFebDpTribTotal.setCellValue(febDpTribTotal);
                        cFebDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cFebDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (marDpTribTotal != 0.0) {
                        cMarDpTribTotal.setCellValue(marDpTribTotal);
                        cMarDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cMarDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (abrDpTribTotal != 0.0) {
                        cAbrDpTribTotal.setCellValue(abrDpTribTotal);
                        cAbrDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cAbrDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (mayDpTribTotal != 0.0) {
                        cMayDpTribTotal.setCellValue(mayDpTribTotal);
                        cMayDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cMayDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (junDpTribTotal != 0.0) {
                        cJunDpTribTotal.setCellValue(junDpTribTotal);
                        cJunDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cJunDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (julDpTribTotal != 0.0) {
                        cJulDpTribTotal.setCellValue(julDpTribTotal);
                        cJulDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cJulDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (agoDpTribTotal != 0.0) {
                        cAgoDpTribTotal.setCellValue(agoDpTribTotal);
                        cAgoDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cAgoDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (sepDpTribTotal != 0.0) {
                        cSepDpTribTotal.setCellValue(sepDpTribTotal);
                        cSepDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cSepDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (octDpTribTotal != 0.0) {
                        cOctDpTribTotal.setCellValue(octDpTribTotal);
                        cOctDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cOctDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (novDpTribTotal != 0.0) {
                        cNovDpTribTotal.setCellValue(novDpTribTotal);
                        cNovDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cNovDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (decDpTribTotal != 0.0) {
                        cDecDpTribTotal.setCellValue(decDpTribTotal);
                        cDecDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cDecDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (totalDpTribTotal != 0.0) {
                        cTotalDpTribTotal.setCellValue(totalDpTribTotal);
                        cTotalDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cTotalDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (retirosDpTribTotal != 0.0) {
                        cRetirosDpTribTotal.setCellValue(retirosDpTribTotal);
                        cRetirosDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cRetirosDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (finalDpTribTotal != 0.0) {
                        cFinalDpTribTotal.setCellValue(finalDpTribTotal);
                        cFinalDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cFinalDpTribTotal.setCellStyle(styleCenter2);
                    }
                    if (activoFijoDpTribTotal != 0.0) {
                        cActivoFijoDpTribTotal.setCellValue(activoFijoDpTribTotal);
                        cActivoFijoDpTribTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cActivoFijoDpTribTotal.setCellStyle(styleCenter2);
                    }

                    // Set totales análisis
                    if (adicionTotal != 0.0) {
                        cAdicionTotal.setCellValue(adicionTotal);
                        cAdicionTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cAdicionTotal.setCellStyle(styleCenter2);
                    }
                    if (deduccionTotal != 0.0) {
                        cDeduccionTotal.setCellValue(deduccionTotal);
                        cDeduccionTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cDeduccionTotal.setCellStyle(styleCenter2);
                    }
                    if (analisisTotal != 0.0) {
                        cAnalisisTotal.setCellValue(analisisTotal);
                        cAnalisisTotal.setCellStyle(styleMoneyTotal);
                    } else {
                        cAnalisisTotal.setCellStyle(styleCenter2);
                    }
                    // </editor-fold>
                }

            }
            // Filtro que solo toma la parte de depreciaciones!!!!!!
            int finFiltDp = rowNumDp - 1;
            sheetDp.setAutoFilter(new CellRangeAddress(inicioFiltDp, finFiltDp, 0, 54));

            rowNumDp++;
            rowNumDp++;

            // RESUMEN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
            // <editor-fold defaultstate="collapsed" desc="RESUMEN CABECERAS">
            Row hrResumen = sheetDp.createRow(rowNumDp);

            // <editor-fold defaultstate="collapsed" desc="Resumen Tipos de Bienes">
            colNumDp = CellReference.convertColStringToIndex("J");
            Cell hcCod = hrResumen.createCell(colNumDp);
            hcCod.setCellValue("COD");
            hcCod.setCellStyle(styleSubHeader);
            colNumDp++;

            Cell hcDesc = hrResumen.createCell(colNumDp);
            hcDesc.setCellValue("DESCRIPCIÓN");
            hcDesc.setCellStyle(styleSubHeader);
            colNumDp++;

            sheetDp.addMergedRegion(CellRangeAddress.valueOf("K" + (rowNumDp + 1) + ":N" + (rowNumDp + 1)));
            PoiUtils.addBorders(CellRangeAddress.valueOf("K" + (rowNumDp + 1) + ":N" + (rowNumDp + 1)),
                    BorderStyle.THIN, IndexedColors.WHITE, sheetDp);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Resumen Activo Fijo">
            colNumDp = CellReference.convertColStringToIndex("P");
            Cell hcSaldoInicial = hrResumen.createCell(colNumDp);
            hcSaldoInicial.setCellStyle(styleSubHeader);
            hcSaldoInicial.setCellValue("Saldo inicial");
            colNumDp++;

            Cell hcActivoCompras = hrResumen.createCell(colNumDp);
            hcActivoCompras.setCellStyle(styleSubHeader);
            hcActivoCompras.setCellValue("Compras");
            colNumDp++;

            Cell hcActivoRetiros = hrResumen.createCell(colNumDp);
            hcActivoRetiros.setCellStyle(styleSubHeader);
            hcActivoRetiros.setCellValue("Retiros");
            colNumDp++;

            Cell hcActivoSaldoFinal = hrResumen.createCell(colNumDp);
            hcActivoSaldoFinal.setCellStyle(styleSubHeader);
            hcActivoSaldoFinal.setCellValue("Saldo final");
            colNumDp++;
            // </editor-fold>

            // Columna en blanco
            colNumDp++;

            // <editor-fold defaultstate="collapsed" desc="Resumen DpCont">
            Cell chInicialDpCont = hrResumen.createCell(colNumDp);
            chInicialDpCont.setCellStyle(styleSubHeader);
            chInicialDpCont.setCellValue("Inicial");
            colNumDp++;

            Cell chEneDpCont = hrResumen.createCell(colNumDp);
            chEneDpCont.setCellStyle(styleSubHeader);
            chEneDpCont.setCellValue("ene");
            colNumDp++;

            Cell chFebDpCont = hrResumen.createCell(colNumDp);
            chFebDpCont.setCellStyle(styleSubHeader);
            chFebDpCont.setCellValue("feb");
            colNumDp++;

            Cell chMarDpCont = hrResumen.createCell(colNumDp);
            chMarDpCont.setCellStyle(styleSubHeader);
            chMarDpCont.setCellValue("mar");
            colNumDp++;

            Cell chAbrDpCont = hrResumen.createCell(colNumDp);
            chAbrDpCont.setCellStyle(styleSubHeader);
            chAbrDpCont.setCellValue("abr");
            colNumDp++;

            Cell chMayDpCont = hrResumen.createCell(colNumDp);
            chMayDpCont.setCellStyle(styleSubHeader);
            chMayDpCont.setCellValue("may");
            colNumDp++;

            Cell chJunDpCont = hrResumen.createCell(colNumDp);
            chJunDpCont.setCellStyle(styleSubHeader);
            chJunDpCont.setCellValue("jun");
            colNumDp++;

            Cell chJulDpCont = hrResumen.createCell(colNumDp);
            chJulDpCont.setCellStyle(styleSubHeader);
            chJulDpCont.setCellValue("jul");
            colNumDp++;

            Cell chAgoDpCont = hrResumen.createCell(colNumDp);
            chAgoDpCont.setCellStyle(styleSubHeader);
            chAgoDpCont.setCellValue("ago");
            colNumDp++;

            Cell chSepDpCont = hrResumen.createCell(colNumDp);
            chSepDpCont.setCellStyle(styleSubHeader);
            chSepDpCont.setCellValue("sep");
            colNumDp++;

            Cell chOctDpCont = hrResumen.createCell(colNumDp);
            chOctDpCont.setCellStyle(styleSubHeader);
            chOctDpCont.setCellValue("oct");
            colNumDp++;

            Cell chNovDpCont = hrResumen.createCell(colNumDp);
            chNovDpCont.setCellStyle(styleSubHeader);
            chNovDpCont.setCellValue("nov");
            colNumDp++;

            Cell chDecDpCont = hrResumen.createCell(colNumDp);
            chDecDpCont.setCellStyle(styleSubHeader);
            chDecDpCont.setCellValue("dec");
            colNumDp++;

            Cell chTotalDpCont = hrResumen.createCell(colNumDp);
            chTotalDpCont.setCellStyle(styleSubHeader);
            chTotalDpCont.setCellValue("Total");
            colNumDp++;

            Cell chRetirosDpCont = hrResumen.createCell(colNumDp);
            chRetirosDpCont.setCellStyle(styleSubHeader);
            chRetirosDpCont.setCellValue("Retiros");
            colNumDp++;

            Cell chFinalDpCont = hrResumen.createCell(colNumDp);
            chFinalDpCont.setCellStyle(styleSubHeader);
            chFinalDpCont.setCellValue("Final");
            colNumDp++;

            Cell chActivoFijoDpCont = hrResumen.createCell(colNumDp);
            chActivoFijoDpCont.setCellStyle(styleSubHeader);
            chActivoFijoDpCont.setCellValue("Activo fijo");
            colNumDp++;
            // </editor-fold>

            // Columna en blanco
            colNumDp++;

            // <editor-fold defaultstate="collapsed" desc="Resument DpTrib">
            // SUB CABECERAS DEPRECIACIONES TRIBUTARIAS
            Cell chInicialDpTrib = hrResumen.createCell(colNumDp);
            chInicialDpTrib.setCellStyle(styleSubHeader);
            chInicialDpTrib.setCellValue("Inicial");
            colNumDp++;

            Cell chEneDpTrib = hrResumen.createCell(colNumDp);
            chEneDpTrib.setCellStyle(styleSubHeader);
            chEneDpTrib.setCellValue("ene");
            colNumDp++;

            Cell chFebDpTrib = hrResumen.createCell(colNumDp);
            chFebDpTrib.setCellStyle(styleSubHeader);
            chFebDpTrib.setCellValue("feb");
            colNumDp++;

            Cell chMarDpTrib = hrResumen.createCell(colNumDp);
            chMarDpTrib.setCellStyle(styleSubHeader);
            chMarDpTrib.setCellValue("mar");
            colNumDp++;

            Cell chAbrDpTrib = hrResumen.createCell(colNumDp);
            chAbrDpTrib.setCellStyle(styleSubHeader);
            chAbrDpTrib.setCellValue("abr");
            colNumDp++;

            Cell chMayDpTrib = hrResumen.createCell(colNumDp);
            chMayDpTrib.setCellStyle(styleSubHeader);
            chMayDpTrib.setCellValue("may");
            colNumDp++;

            Cell chJunDpTrib = hrResumen.createCell(colNumDp);
            chJunDpTrib.setCellStyle(styleSubHeader);
            chJunDpTrib.setCellValue("jun");
            colNumDp++;

            Cell chJulDpTrib = hrResumen.createCell(colNumDp);
            chJulDpTrib.setCellStyle(styleSubHeader);
            chJulDpTrib.setCellValue("jul");
            colNumDp++;

            Cell chAgoDpTrib = hrResumen.createCell(colNumDp);
            chAgoDpTrib.setCellStyle(styleSubHeader);
            chAgoDpTrib.setCellValue("ago");
            colNumDp++;

            Cell chSepDpTrib = hrResumen.createCell(colNumDp);
            chSepDpTrib.setCellStyle(styleSubHeader);
            chSepDpTrib.setCellValue("sep");
            colNumDp++;

            Cell chOctDpTrib = hrResumen.createCell(colNumDp);
            chOctDpTrib.setCellStyle(styleSubHeader);
            chOctDpTrib.setCellValue("oct");
            colNumDp++;

            Cell chNovDpTrib = hrResumen.createCell(colNumDp);
            chNovDpTrib.setCellStyle(styleSubHeader);
            chNovDpTrib.setCellValue("nov");
            colNumDp++;

            Cell chDecDpTrib = hrResumen.createCell(colNumDp);
            chDecDpTrib.setCellStyle(styleSubHeader);
            chDecDpTrib.setCellValue("dec");
            colNumDp++;

            Cell chTotalDpTrib = hrResumen.createCell(colNumDp);
            chTotalDpTrib.setCellStyle(styleSubHeader);
            chTotalDpTrib.setCellValue("Total");
            colNumDp++;

            Cell chRetirosDpTrib = hrResumen.createCell(colNumDp);
            chRetirosDpTrib.setCellStyle(styleSubHeader);
            chRetirosDpTrib.setCellValue("Retiros");
            colNumDp++;

            Cell chFinalDpTrib = hrResumen.createCell(colNumDp);
            chFinalDpTrib.setCellStyle(styleSubHeader);
            chFinalDpTrib.setCellValue("Final");
            colNumDp++;

            Cell chActivoFijoDpTrib = hrResumen.createCell(colNumDp);
            chActivoFijoDpTrib.setCellStyle(styleSubHeader);
            chActivoFijoDpTrib.setCellValue("Activo fijo");
            colNumDp++;
            // </editor-fold>

            // Columna en blanco
            colNumDp++;

            // <editor-fold defaultstate="collapsed" desc="Resumen Análisis">
            // SUB CABECERAS ANÁLISIS
            Cell chAdicionAna = hrResumen.createCell(colNumDp);
            chAdicionAna.setCellStyle(styleSubHeader);
            chAdicionAna.setCellValue("Adición");
            colNumDp++;
            Cell chDeduccionAna = hrResumen.createCell(colNumDp);
            chDeduccionAna.setCellStyle(styleSubHeader);
            chDeduccionAna.setCellValue("Deducción");
            colNumDp++;
            Cell chTotalAna = hrResumen.createCell(colNumDp);
            chTotalAna.setCellStyle(styleSubHeader);
            chTotalAna.setCellValue("Total");
            colNumDp++;
            // </editor-fold>

            rowNumDp++;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Llenado de data resumen">
            for (ActivoDepreciacionDTO bienDepreciacionTO : listResumen) {
                Row rResumen = sheetDp.createRow(rowNumDp);

                colNumDp = CellReference.convertColStringToIndex("J");
                Cell cCod = rResumen.createCell(colNumDp);
                cCod.setCellValue(bienDepreciacionTO.getIdBienTipo());
                cCod.setCellStyle(styleSubHeader);
                colNumDp++;

                Cell cDescripcion = rResumen.createCell(colNumDp);
                cDescripcion.setCellValue(bienDepreciacionTO.getIdBienTipoDescripcion());
                cDescripcion.setCellStyle(styleSubHeaderLeft);
                sheetDp.addMergedRegion(CellRangeAddress.valueOf("K" + (rowNumDp + 1) + ":N" + (rowNumDp + 1)));
                PoiUtils.addBorders(CellRangeAddress.valueOf("K" + (rowNumDp + 1) + ":N" + (rowNumDp + 1)),
                        BorderStyle.THIN, IndexedColors.WHITE, sheetDp);
                colNumDp++;

                colNumDp = CellReference.convertColStringToIndex("P");
                Cell cSaldoInicial = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getActivoSaldoInicial(), cSaldoInicial, styleMoneyTotal,
                        styleCenter2);
                colNumDp++;

                Cell cActivoCompras = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getActivoCompras(), cActivoCompras, styleMoneyTotal, styleCenter2);
                colNumDp++;

                Cell cActivoRetiros = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getActivoRetiros(), cActivoRetiros, styleMoneyTotal, styleCenter2);
                colNumDp++;

                Cell cActivoSaldoFinal = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getActivoSaldoFinal(), cActivoSaldoFinal, styleMoneyTotal,
                        styleCenter2);
                colNumDp++;

                colNumDp = CellReference.convertColStringToIndex("U");
                Cell cInicialDpCont = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getInicial(), cInicialDpCont, styleMoneyTotal, styleCenter2);
                colNumDp++;

                Cell cEne = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getEne(), cEne, styleMoney, styleCenter);
                colNumDp++;

                Cell cFeb = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getFeb(), cFeb, styleMoney, styleCenter);
                colNumDp++;

                Cell cMar = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getMar(), cMar, styleMoney, styleCenter);
                colNumDp++;

                Cell cAbr = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getAbr(), cAbr, styleMoney, styleCenter);
                colNumDp++;

                Cell cMay = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getMay(), cMay, styleMoney, styleCenter);
                colNumDp++;

                Cell cJun = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getJun(), cJun, styleMoney, styleCenter);
                colNumDp++;

                Cell cJul = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getJul(), cJul, styleMoney, styleCenter);
                colNumDp++;

                Cell cAgo = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getAgo(), cAgo, styleMoney, styleCenter);
                colNumDp++;

                Cell cSep = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getSep(), cSep, styleMoney, styleCenter);
                colNumDp++;

                Cell cOct = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getOct(), cOct, styleMoney, styleCenter);
                colNumDp++;

                Cell cNov = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getNov(), cNov, styleMoney, styleCenter);
                colNumDp++;

                Cell cDec = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getDec(), cDec, styleMoney, styleCenter);
                colNumDp++;

                Cell cTotal = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getTotal(), cTotal, styleMoneyTotal, styleCenter2);
                colNumDp++;

                Cell cRetiros = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getRetiros(), cRetiros, styleMoneyTotal, styleCenter2);
                colNumDp++;

                Cell cFinal = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getSaldoFinal(), cFinal, styleMoneyTotal, styleCenter2);
                colNumDp++;

                Cell cActivoFijo = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getActivoFijo(), cActivoFijo, styleMoneyTotal, styleCenter2);
                colNumDp++;

                // Columna vacía
                colNumDp++;

                colNumDp = CellReference.convertColStringToIndex("AM");
                Cell cInicialDpTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getInicialTrib(), cInicialDpTrib, styleMoneyTotal, styleCenter2);
                colNumDp++;

                Cell cEneTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getEneTrib(), cEneTrib, styleMoney, styleCenter);
                colNumDp++;

                Cell cFebTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getFebTrib(), cFebTrib, styleMoney, styleCenter);
                colNumDp++;

                Cell cMarTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getMarTrib(), cMarTrib, styleMoney, styleCenter);
                colNumDp++;

                Cell cAbrTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getAbrTrib(), cAbrTrib, styleMoney, styleCenter);
                colNumDp++;

                Cell cMayTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getMayTrib(), cMayTrib, styleMoney, styleCenter);
                colNumDp++;

                Cell cJunTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getJunTrib(), cJunTrib, styleMoney, styleCenter);
                colNumDp++;

                Cell cJulTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getJulTrib(), cJulTrib, styleMoney, styleCenter);
                colNumDp++;

                Cell cAgoTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getAgoTrib(), cAgoTrib, styleMoney, styleCenter);
                colNumDp++;

                Cell cSepTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getSepTrib(), cSepTrib, styleMoney, styleCenter);
                colNumDp++;

                Cell cOctTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getOctTrib(), cOctTrib, styleMoney, styleCenter);
                colNumDp++;

                Cell cNovTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getNovTrib(), cNovTrib, styleMoney, styleCenter);
                colNumDp++;

                Cell cDecTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getDecTrib(), cDecTrib, styleMoney, styleCenter);
                colNumDp++;

                Cell cTotalTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getTotalTrib(), cTotalTrib, styleMoneyTotal, styleCenter2);
                colNumDp++;

                Cell cRetirosTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getRetirosTrib(), cRetirosTrib, styleMoneyTotal, styleCenter2);
                colNumDp++;

                Cell cFinalTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getSaldoFinalTrib(), cFinalTrib, styleMoneyTotal, styleCenter2);
                colNumDp++;

                Cell cActivoFijoTrib = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getActivoFijoTrib(), cActivoFijoTrib, styleMoneyTotal,
                        styleCenter2);
                colNumDp++;

                // Columna vacía
                colNumDp++;

                Cell cAdicion = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getAdicion(), cAdicion, styleMoneyTotal, styleCenter2);
                colNumDp++;

                Cell cDeduccion = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getDeduccion(), cDeduccion, styleMoneyTotal, styleCenter2);
                colNumDp++;

                Cell cTotalAnalisis = rResumen.createCell(colNumDp);
                setCellValue(bienDepreciacionTO.getTotalAnalisis(), cTotalAnalisis, styleMoneyTotal, styleCenter2);
                colNumDp++;

                rowNumDp++;

            }
            // </editor-fold>

            sheetDp.createFreezePane(9, 5, 9, 5);

            // <editor-fold defaultstate="collapsed" desc=" A N C H O D E C O L U M N A S ">
            for (int contCol = 0; contCol < colNumDp; contCol++) {
                sheetDp.autoSizeColumn(contCol);
            }
            // Col Descripción = doble de la col Id (col 0)
            sheetDp.setColumnWidth(3, sheetDp.getColumnWidth(0) * 2);
            // </editor-fold>

            hojaAnioI++;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return out.toByteArray();
    }

    private void setCellValue(double value, Cell celda, CellStyle styleMoneyTotal, CellStyle styleCenter2) {
        if (value != 0) {
            celda.setCellValue(value);
            celda.setCellStyle(styleMoneyTotal);
        } else {
            celda.setCellStyle(styleCenter2);
        }
    }

    private ActivoDepreciacionDTO getDepContableByYear(ActivoDTO activo, int anio) {
        for (ActivoDepreciacionDTO ad : activo.getDepreciacionesContables()) {
            if (ad.getAnio().equals(String.valueOf(anio)))
                return ad;
        }
        return createSectDep(); // fallback
    }

    private ActivoDepreciacionDTO getDepTributariaByYear(ActivoDTO activo, int anio) {
        for (ActivoDepreciacionDTO ad : activo.getDepreciacionesTributarias()) {
            if (ad.getAnio().equals(String.valueOf(anio)))
                return ad;
        }
        return createSectDep(); // fallback
    }

    private ActivoDepreciacionDTO createSectDep() {
        return ActivoDepreciacionDTO.builder()
                .inicial(0.0).ene(0.0).feb(0.0).mar(0.0).abr(0.0)
                .may(0.0).jun(0.0).jul(0.0).ago(0.0).sep(0.0).oct(0.0).nov(0.0).dec(0.0)
                .total(0.0).retiros(0.0).saldoFinal(0.0).activoFijo(0.0)
                .activoSaldoInicial(0.0).activoCompras(0.0).activoRetiros(0.0).activoSaldoFinal(0.0)
                .build();
    }

    /**
     * Devuelve 0 si la fecha es nula/vacía. Usar para comparaciones opcionales (ej:
     * fechaBaja).
     */
    private int getAnioFromDateString(String dateStr) {
        if (dateStr == null || dateStr.length() < 4)
            return 0;
        return Integer.parseInt(dateStr.substring(0, 4));
    }

    /**
     * Devuelve el año actual si la fecha es nula/vacía. Usar para rangos
     * obligatorios (ej: hojaAnioI, hojaAnioF).
     */
    private int getAnioRequired(String dateStr) {
        if (dateStr == null || dateStr.length() < 4)
            return java.time.LocalDate.now().getYear();
        return Integer.parseInt(dateStr.substring(0, 4));
    }

    private Date stringToDate(String dateStr) {
        if (dateStr == null || dateStr.length() < 10)
            return new Date();
        LocalDate ld = LocalDate.parse(dateStr.substring(0, 10));
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
