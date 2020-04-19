package bg.ehealth.prescriptions.services.medicine.nonproprietary.pd;

import bg.ehealth.prescriptions.persistence.model.NonProprietaryMedicineName;
import bg.ehealth.prescriptions.services.medicine.nonproprietary.MedicineNamesMedia;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MedicineTsvTableMedia implements MedicineNamesMedia {

    private final TsvParser parser;

    public MedicineTsvTableMedia() {
        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        this.parser = new TsvParser(settings);
    }

    @Override
    public Set<NonProprietaryMedicineName> medicineNames(InputStream inputStream) {
        List<Record> records = parser.parseAllRecords(inputStream, StandardCharsets.UTF_8);
        return Stream.concat(
                records.stream()
                        .map(record -> record.getString("common_name").trim().toLowerCase())
                        .skip(1), //skip column header
                records.stream()
                        .map(record -> record.getString("source_id").trim().toLowerCase())
                        .skip(1)) //skip column header
                .map(NonProprietaryMedicineName::new)
                .collect(Collectors.toSet());
    }
}
