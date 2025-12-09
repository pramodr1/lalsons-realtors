package com.lalsons.backend.service;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.lalsons.backend.entity.Location;
import com.lalsons.backend.entity.Property;
import com.lalsons.backend.enums.AreaUnit;
import com.lalsons.backend.enums.PropertyType;
import com.lalsons.backend.repository.LocationRepository;

@Service
public class ExcelParserService {

    private final LocationRepository locationRepository;

    // DataFormatter handles all cell types (String, Numeric, Boolean) gracefully
    private final DataFormatter dataFormatter = new DataFormatter();

    public ExcelParserService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Property> parseExcel(InputStream inputStream) {
        List<Property> properties = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                String title = getStringVal(row, 0);
                if(title.isEmpty()) continue;

                Property p = new Property();
                p.setTitle(title);
                
                String locName = getStringVal(row, 1);
                Location loc = locationRepository.findByName(locName).orElse(null);
                if(loc != null) p.setLocation(loc);
                
                try {
                    String typeStr = getStringVal(row, 2).toUpperCase().trim();
                    p.setPropertyType(PropertyType.valueOf(typeStr));
                } catch(Exception e) { p.setPropertyType(PropertyType.RESIDENTIAL); }
                
                p.setSubType(getStringVal(row, 3));
                p.setPrice(BigDecimal.valueOf(getDoubleVal(row, 4)));
                p.setAreaValue(getDoubleVal(row, 5));
                
                try {
                    String unitStr = getStringVal(row, 6).toUpperCase().trim();
                    p.setAreaUnit(AreaUnit.valueOf(unitStr));
                } catch(Exception e) { p.setAreaUnit(AreaUnit.CENTS); }
                
                double bhkVal = getDoubleVal(row, 7);
                if(bhkVal > 0) p.setBhk((int) bhkVal);

                p.setStatus("AVAILABLE");
                properties.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }
    
    private String getStringVal(Row row, int index) {
        Cell cell = row.getCell(index);
        return dataFormatter.formatCellValue(cell);
    }
    
    private Double getDoubleVal(Row row, int index) {
        String val = getStringVal(row, index);
        try { return Double.parseDouble(val.replace(",", "")); } catch (Exception e) { return 0.0; }
    }

    
    private BigDecimal getBigDecimalVal(Row row, int index) {
         return BigDecimal.valueOf(getDoubleVal(row, index));
    }
}