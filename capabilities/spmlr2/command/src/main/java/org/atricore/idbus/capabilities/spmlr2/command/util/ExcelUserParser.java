package org.atricore.idbus.capabilities.spmlr2.command.util;

import oasis.names.tc.spml._2._0.atricore.AttributeValueType;
import oasis.names.tc.spml._2._0.atricore.UserType;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUserParser implements UserParser {

    private static final Log logger = LogFactory.getLog(ExcelUserParser.class);

    private String sheetName;

    private String datePattern = "MM/dd/yyyy hh:mm:ss aa";

    private DateFormat dateFormat;

    @Override
    public String getName() {
        return "excel-user-parser";
    }

    @Override
    public String getSchema() {
        PropertyDescriptor[] properties = BeanUtilsBean.getInstance().getPropertyUtils().getPropertyDescriptors(UserType.class);

        StringBuffer schema = new StringBuffer();

        for (PropertyDescriptor property : properties) {
            schema.append(property.getName()).append(",");
        }
        schema.append("\n");
        for (PropertyDescriptor property : properties) {
            schema.append(property.getPropertyType().getSimpleName()).append(",");
        }

        return schema.toString();
    }

    @Override
    public Set<UserType> fromStream(InputStream is) throws UserParseException {
        return fromStream(is, true);
    }

    @Override
    public Set<UserType> fromStream(InputStream is, boolean unknownPropertiesAsExtendedAttributes) throws UserParseException {

        try {

            // Get user properties
            PropertyDescriptor[] properties = BeanUtilsBean.getInstance().getPropertyUtils().getPropertyDescriptors(UserType.class);

            // Reade excel file
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            int sheetsCount = workbook.getNumberOfSheets();

            if (logger.isDebugEnabled())
                logger.debug("Workbook has " + sheetsCount + " sheets");

            if (sheetsCount < 1)
                throw new UserParseException("No sheets found in workbook!" + workbook);

            int sheetIdx = 0;

            XSSFSheet sheet = workbook.getSheetAt(sheetIdx);

            if (sheet == null)
                throw new UserParseException("No sheet found at index " + sheetIdx);

            if (logger.isDebugEnabled())
                logger.debug("Using sheet " + sheet.getSheetName() + " [" + sheetIdx + "]" + sheet.getPhysicalNumberOfRows());


            int rowIdx = sheet.getTopRow();
            // Find columns

            List<UserProperty> userProperties = new ArrayList<UserProperty>();

            XSSFRow headerRow = sheet.getRow(rowIdx);
            if (headerRow == null)
                throw new UserParseException("No row (TOP) found for index " + rowIdx);

            for (int colIdx = 0; colIdx < headerRow.getPhysicalNumberOfCells(); colIdx++) {

                XSSFCell cell = headerRow.getCell(colIdx);
                if (cell == null) {
                    logger.debug("Ignoring cell [" + rowIdx + "," + colIdx+"]");
                    continue;
                }

                // Get property name from header column value

                String headerColName = headerRow.getCell(colIdx).getStringCellValue();

                // Nao header cell value to user property
                boolean found = false;

                for (PropertyDescriptor property : properties) {
                    if (headerColName.equalsIgnoreCase(property.getName())) {
                        UserProperty userProperty = new UserProperty(colIdx, headerColName, property);
                        userProperties.add(userProperty);
                        found = true;
                        if (logger.isDebugEnabled())
                            logger.debug("Cell [" + rowIdx + "," + colIdx + "] mapped to user property " +
                                    userProperty.getColumnName());
                        break;
                    }
                }

                if (!found) {
                    // Add custom attribute using current column ?! (optional ?!)
                    UserProperty userProperty = new UserProperty(colIdx, headerColName, null);
                    userProperties.add(userProperty);

                }

            }

            // Get users

            Set<UserType> users = new HashSet<UserType>();

            XSSFRow row;
            Iterator<Row> rowIterator = sheet.rowIterator();
            rowIterator.next();  // skip header


            while (rowIterator.hasNext()) {
                row = (XSSFRow) rowIterator.next();

                if (row != null) {

                    rowIdx = row.getRowNum();

                    if (logger.isDebugEnabled())
                        logger.debug("Processing row [" + rowIdx + "]");

                    UserType user = new UserType();

                    for (UserProperty userProperty : userProperties) {

                        int colIdx = userProperty.getColumnIdx();

                        if (logger.isDebugEnabled())
                            logger.debug("Processing cell [" + rowIdx + "," + colIdx + "] " +
                                    userProperty.getColumnName());


                        if (userProperty.getDescriptor() != null) {

                            PropertyDescriptor pd = userProperty.getDescriptor();

                            Class type = pd.getPropertyType();
                            Object value = null;
                            boolean mapped = false;

                            if (type.getSimpleName().equals("String")) {
                                // Force cell to be a string
                                row.getCell(userProperty.getColumnIdx()).setCellType(Cell.CELL_TYPE_STRING);
                                value = row.getCell(userProperty.getColumnIdx()).getStringCellValue().trim();
                                mapped = true;
                            } else if (type.getSimpleName().equals("Boolean")) {
                                row.getCell(userProperty.getColumnIdx()).setCellType(Cell.CELL_TYPE_STRING);
                                value = Boolean.parseBoolean(row.getCell(userProperty.getColumnIdx()).getStringCellValue().trim());
                                mapped = true;
                            } else if (type.getSimpleName().equals("Integer")) {
                                row.getCell(userProperty.getColumnIdx()).setCellType(Cell.CELL_TYPE_STRING);
                                value = Integer.parseInt(row.getCell(userProperty.getColumnIdx()).getStringCellValue().trim());
                                mapped = true;
                            } else if (type.getSimpleName().equals("Long")) {
                                row.getCell(userProperty.getColumnIdx()).setCellType(Cell.CELL_TYPE_STRING);
                                value = Long.parseLong(row.getCell(userProperty.getColumnIdx()).getStringCellValue().trim());
                                mapped = true;
                            } else if (type.getSimpleName().equals("Float")) {
                                row.getCell(userProperty.getColumnIdx()).setCellType(Cell.CELL_TYPE_STRING);
                                value = Float.parseFloat(row.getCell(userProperty.getColumnIdx()).getStringCellValue().trim());
                                mapped = true;
                            } else if (type.getSimpleName().equals("Double")) {
                                row.getCell(userProperty.getColumnIdx()).setCellType(Cell.CELL_TYPE_STRING);
                                value = Double.parseDouble(row.getCell(userProperty.getColumnIdx()).getStringCellValue().trim());
                                mapped = true;
                            } else if (type.getSimpleName().equals("XMLGregorianCalendar")) {

                                try {

                                    Date date = row.getCell(userProperty.getColumnIdx()).getDateCellValue();
                                    GregorianCalendar gCalendar = new GregorianCalendar();
                                    gCalendar.setTime(date);
                                    value = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);

                                } catch (DatatypeConfigurationException e) {
                                    throw new UserParseException(e);
                                }
                                mapped = true;
                            }

                            if (mapped) {
                                if (logger.isDebugEnabled())
                                    logger.debug("Value for cell [" + rowIdx + "," + colIdx + "] " +
                                            userProperty.getColumnName() + " [" + value + "]");

                                if (logger.isDebugEnabled())
                                    logger.debug("Setting property [" + userProperty.getDescriptor().getName() + "] to [" + value + "]");
                                // This is a built-in property
                                BeanUtils.setProperty(user, userProperty.getDescriptor().getName(), value);
                            } else {
                                if (logger.isDebugEnabled())
                                    logger.debug("Value for cell [" + rowIdx + "," + colIdx + "] " +
                                            userProperty.getColumnName() + " NOT mapped!");
                            }

                        } else if (unknownPropertiesAsExtendedAttributes) {

                            // Force cell to be a string
                            row.getCell(userProperty.getColumnIdx()).setCellType(Cell.CELL_TYPE_STRING);
                            String value = row.getCell(userProperty.getColumnIdx()).getStringCellValue().trim();

                            if (logger.isDebugEnabled())
                                logger.debug("Value for cell [" + rowIdx + "," + colIdx + "] " +
                                        userProperty.getColumnName() + " [" + value + "]");

                            if (logger.isDebugEnabled())
                                logger.debug("Setting attribute [" + userProperty.getColumnName() + "] to [" + value + "]");

                            // This is a user attribute
                            AttributeValueType attr = new AttributeValueType();
                            attr.setName(userProperty.getColumnName());
                            attr.setValue(value);

                            user.getAttributeValue().add(attr);

                        }
                    }

                    users.add(user);

                }
            }

            workbook.close();

            return users;

        } catch (IllegalAccessException e) {
            throw new UserParseException(e);
        } catch (IOException e) {
            throw new UserParseException(e);
        } catch (InvocationTargetException e) {
            throw new UserParseException(e);
        }

    }


    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public DateFormat getDateFormat() {

        if (dateFormat == null)
            dateFormat = new SimpleDateFormat(datePattern);

        return dateFormat;

    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public class UserProperty {

        int columnIdx;

        String columnName;

        PropertyDescriptor descriptor;

        public UserProperty(int columnIdx, String columnName, PropertyDescriptor descriptor) {
            this.columnIdx = columnIdx;
            this.columnName = columnName;
            this.descriptor = descriptor;
        }

        public int getColumnIdx() {
            return columnIdx;
        }

        public String getColumnName() {
            return columnName;
        }

        public PropertyDescriptor getDescriptor() {
            return descriptor;
        }
    }
}


