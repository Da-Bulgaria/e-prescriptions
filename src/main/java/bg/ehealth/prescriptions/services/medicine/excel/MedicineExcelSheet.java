package bg.ehealth.prescriptions.services.medicine.excel;

import com.google.common.base.Strings;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static bg.ehealth.prescriptions.services.medicine.excel.MedicineExcelColumn.*;
import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND;
import static org.apache.poi.ss.util.CellUtil.getCell;

@Service
public class MedicineExcelSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicineExcelSheet.class);

    private int rowIndex;

    public @NotNull List<MedicineExcelRow> medicines(@NotNull List<InputStream> inputStream,
                                                     @NotNull List<String> columnNames,
                                                     int rowNumberScan) {
        checkArgument(inputStream != null, "Cannot read medicines: inputStream cannot be null!");
        LOGGER.debug("Parsing input stream for column names:{}", columnNames);
        List<MedicineExcelRow> rows = new ArrayList<>();
        inputStream.forEach(input -> rows.addAll(medicines(columnNames, input, rowNumberScan)));
        LOGGER.debug("Parsed {} rows.", rows.size());
        return rows;
    }

    private List<MedicineExcelRow> medicines(List<String> columnNames, InputStream input, int rowNumberScan) {
        Sheet sheet = sheet(input, 0);
        Map<MedicineExcelColumn, Integer> columns = columns(sheet, columnNames, rowNumberScan);
        return medicineExcelRows(sheet, columns);
    }

    //TODO better add skipping to the header row directly
    private List<MedicineExcelRow> medicineExcelRows(Sheet sheet,
                                                     Map<MedicineExcelColumn, Integer> columns) {
        return StreamSupport.stream(sheet.spliterator(), false)
                .skip(rowIndex)
                .map(row -> new MedicineExcelRow(
                        cellStringValue(getCell(row, columns.get(IDENTIFIER))),
                        cellStringValue(getCell(row, columns.get(ATC_CODE))),
                        cellStringValue(getCell(row, columns.get(INN))),
                        cellStringValue(getCell(row, columns.get(NAME))),
                        cellStringValue(getCell(row, columns.get(ICD_CODE))),
                        cellStringValue(getCell(row, columns.get(STATUS)))))
                .collect(Collectors.toList());
    }

    private Sheet sheet(InputStream inputStream, int sheetNumber) {
        HSSFWorkbook sheets = null;
        try {
            sheets = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            LOGGER.error("Failed to parse excel file!", e);
        }
        return Objects.requireNonNull(sheets, "Unable to read sheetNumber" +
                sheetNumber + " from excel").getSheetAt(sheetNumber);
    }

    private Map<MedicineExcelColumn, Integer> columns(Sheet sheet, List<String> columnNames, int rowNumberScan) {
        rowIndex = forwardScanForColumnHeaders(sheet, columnNames, rowNumberScan);
        LOGGER.debug("Column headers located at row: {}", rowIndex);
        return StreamSupport.stream(sheet.getRow(rowIndex).spliterator(), false)
                .filter(cell -> SOLID_FOREGROUND.equals(cell.getCellStyle().getFillPatternEnum()))
                .filter(cell -> !Strings.isNullOrEmpty(cell.getStringCellValue()))
                .filter(cell -> columnNames.contains(cell.getStringCellValue().trim()))
                .collect(Collectors.toUnmodifiableMap(cell ->
                        fromString(cell.getStringCellValue()), Cell::getColumnIndex));
    }

    private int forwardScanForColumnHeaders(Sheet sheet, List<String> columnNames, int rowNumberScan) {
        return StreamSupport.stream(sheet.spliterator(), false)
                .limit(rowNumberScan)
                .filter(row -> row.getFirstCellNum() >= 0)
                .filter(row -> !(row.getPhysicalNumberOfCells() < sheet.getNumMergedRegions()))
                .filter(row -> rowContainColumnHeaders(row, columnNames))
                .findFirst()
                .map(Row::getRowNum)
                .orElseThrow(() -> new RuntimeException("Unable to find columns with column names: " + columnNames
                        + " in the first " + rowNumberScan + " rows"));
    }

    private boolean rowContainColumnHeaders(Row row, List<String> columnNames) {
        return StreamSupport.stream(row.spliterator(), false)
                .map(this::cellStringValue)
                .filter(val -> !Strings.isNullOrEmpty(val))
                .collect(Collectors.toList())
                .containsAll(columnNames);
    }

    private String cellStringValue(Cell cell) {
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

}
