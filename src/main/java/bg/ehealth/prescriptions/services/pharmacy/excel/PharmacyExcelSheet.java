package bg.ehealth.prescriptions.services.pharmacy.excel;

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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static bg.ehealth.prescriptions.services.pharmacy.excel.PharmacyExcelColumn.*;
import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND;
import static org.apache.poi.ss.util.CellUtil.getCell;

@Service
public class PharmacyExcelSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(PharmacyExcelSheet.class);

    public @NotNull List<PharmacyExcelRow> pharmacies(@NotNull InputStream inputStream,
                                                      @NotNull List<String> columnNames) {
        checkArgument(inputStream != null, "Cannot read pharmacies: inputStream cannot be null!");
        checkArgument(columnNames != null, "Cannot read pharmacies: columnNames cannot be null!");
        LOGGER.debug("Parsing input stream for column names:{}", columnNames);
        Sheet sheet = sheet(inputStream, 0);
        Map<PharmacyExcelColumn, Integer> columns = columns(sheet, columnNames);
        List<PharmacyExcelRow> rows = pharmacyExcelRows(sheet, columns);
        LOGGER.debug("Parsed {} rows.", rows.size());
        return rows;
    }

    private List<PharmacyExcelRow> pharmacyExcelRows(Sheet sheet,
                                                     Map<PharmacyExcelColumn, Integer> columns) {
        return StreamSupport.stream(sheet.spliterator(), false)
                .skip(1) //skip column headers
                .map(row -> new PharmacyExcelRow(
                        cellStringValue(getCell(row, columns.get(IDENTIFIER))),
                        cellStringValue(getCell(row, columns.get(NAME))),
                        cellStringValue(getCell(row, columns.get(ADDRESS_CITY))),
                        cellStringValue(getCell(row, columns.get(ADDRESS)))))
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

    private Map<PharmacyExcelColumn, Integer> columns(Sheet sheet, List<String> columnNames) {
        Row row = sheet.getRow(0);
        return StreamSupport.stream(row.spliterator(), false)
                .filter(cell -> SOLID_FOREGROUND.equals(cell.getCellStyle().getFillPatternEnum()))
                .filter(cell -> !Strings.isNullOrEmpty(cell.getStringCellValue()))
                .filter(cell -> columnNames.contains(cell.getStringCellValue().trim()))
                .collect(Collectors.toUnmodifiableMap(cell ->
                        fromString(cell.getStringCellValue()), Cell::getColumnIndex));
    }

    private String cellStringValue(Cell cell) {
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
