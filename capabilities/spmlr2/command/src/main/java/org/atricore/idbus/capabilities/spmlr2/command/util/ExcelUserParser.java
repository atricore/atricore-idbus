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
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ExcelUserParser implements UserImporter {

    private static final Log logger = LogFactory.getLog(ExcelUserParser.class);

    private static final UUIDGenerator passwordGenerator = new UUIDGenerator(4);

    private String sheetName;

    @Override
    public Set<UserType> fromStream(InputStream is) throws UserParseException {
        return fromStream(is, true);
    }

    @Override
    public Set<UserType> fromStream(InputStream is, boolean importUnknownColumnsAsAttributes) throws UserParseException {

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheetAt(0);

            // Find columns

            PropertyDescriptor[] properties = BeanUtilsBean.getInstance().getPropertyUtils().getPropertyDescriptors(UserType.class);

            List<UserProperty> userProperties = new ArrayList<UserProperty>();

            XSSFRow headerRow = sheet.getRow(0);
            for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {

                XSSFCell cell = headerRow.getCell(i);
                if (cell == null)
                    continue;

                String headerColName = headerRow.getCell(i).getStringCellValue();
                boolean found = false;

                for (PropertyDescriptor property : properties) {
                    if (headerColName.equalsIgnoreCase(property.getName())) {
                        UserProperty userProperty = new UserProperty(i, headerColName, property);
                        userProperties.add(userProperty);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // Add custom attribute using current column ?! (optional ?!)
                    UserProperty userProperty = new UserProperty(i, headerColName, null);

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

                    UserType user = new UserType();

                    for (UserProperty userProperty : userProperties) {

                        row.getCell(userProperty.getColumnIdx()).setCellType(Cell.CELL_TYPE_STRING);
                        String value = row.getCell(userProperty.getColumnIdx()).getStringCellValue().trim();

                        if (userProperty.getDescriptor() != null) {
                            // This is a built-in property
                            BeanUtils.setProperty(user, userProperty.getDescriptor().getName(), value);

                        } else if (importUnknownColumnsAsAttributes){
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


