package bg.ehealth.prescriptions.services.pharmacy;

import com.google.common.base.Strings;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static bg.ehealth.prescriptions.services.pharmacy.PharmacyRegistryExcelColumn.*;
import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.poi.ss.util.CellUtil.getCell;

@Service
public class DefaultPharmacyRegistry implements PharmacyRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPharmacyRegistry.class);

    @Override
    public @NotNull List<PharmacyRegistryExcelRow> pharmacies(@NotNull InputStream inputStream, List<String> columnNames) {
        checkArgument(inputStream != null, "Cannot read pharmacies: inputStream cannot be null!");
        LOGGER.debug("Parsing input stream for column names:{}", columnNames);
        HSSFSheet sheet = sheet(inputStream, 0);
        Map<PharmacyRegistryExcelColumn, Integer> columns = columns(sheet, columnNames);
        List<PharmacyRegistryExcelRow> rows = pharmacyExcelRows(sheet, columns);
        LOGGER.debug("Parsed {} rows.", rows.size());
        return rows;
    }

    private List<PharmacyRegistryExcelRow> pharmacyExcelRows(HSSFSheet sheet, Map<PharmacyRegistryExcelColumn, Integer> columns) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next(); //skip column names
        List<PharmacyRegistryExcelRow> rows = new ArrayList<>();
        rowIterator.forEachRemaining(row -> rows.add(
                new PharmacyRegistryExcelRow(
                        cell(row, columns, IDENTIFIER).getStringCellValue(),
                        cell(row, columns, NAME).getStringCellValue(),
                        cell(row, columns, ADDRESS_CITY).getStringCellValue(),
                        cell(row, columns, ADDRESS).getStringCellValue())
        ));

        return rows;
    }

    private Cell cell(Row row, Map<PharmacyRegistryExcelColumn, Integer> columns, PharmacyRegistryExcelColumn column) {
        Cell cell = getCell(row, columns.get(column));
        cell.setCellType(CellType.STRING);
        return cell;
    }

    private HSSFSheet sheet(InputStream inputStream, int sheetNumber) {
        HSSFWorkbook sheets = null;
        try {
            sheets = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            LOGGER.error("Failed to parse excel file!", e);
        }
        return Objects.requireNonNull(sheets, "Unable to read sheetNumber" +
                sheetNumber + " from excel").getSheetAt(sheetNumber);
    }

    private Map<PharmacyRegistryExcelColumn, Integer> columns(HSSFSheet sheet, List<String> columnNames) {
        HSSFRow row = sheet.getRow(0);
        return IntStream.range(row.getFirstCellNum(), row.getLastCellNum())
                    .mapToObj(row::getCell)
                    .filter(cell -> cell.getCellStyle().getFillPatternEnum().getCode() == 1)
                    .filter(cell -> !Strings.isNullOrEmpty(cell.getStringCellValue()))
                    .filter(cell -> columnNames.contains(cell.getStringCellValue().trim()))
                    .collect(Collectors.toUnmodifiableMap(cell -> fromString(cell.getStringCellValue()),
                            HSSFCell::getColumnIndex));
    }
}
