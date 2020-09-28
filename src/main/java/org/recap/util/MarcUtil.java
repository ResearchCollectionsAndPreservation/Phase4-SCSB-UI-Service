package org.recap.util;

import info.freelibrary.marc4j.impl.ControlFieldImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;
import org.marc4j.marc.DataField;
import org.marc4j.marc.VariableField;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.MarcFactory;
import org.recap.model.marc.BibMarcRecord;
import org.recap.model.marc.HoldingsMarcRecord;
import org.recap.model.marc.ItemMarcRecord;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pvsubrah on 6/15/16.
 */
@Service
public class MarcUtil {

    /**
     *This method converts marc xml to record list.
     *
     * @param marcXml the marc xml
     * @return the list
     */
    public List<Record> convertMarcXmlToRecord(String marcXml) {
        List<Record> records = new ArrayList<>();
        MarcReader reader = new MarcXmlReader(IOUtils.toInputStream(marcXml));
        while (reader.hasNext()) {
            Record record = reader.next();
            records.add(record);
        }
        return records;
    }

    /**
     * This method gets data field value for the given data fields tag starts with.
     *
     * @param record            the record
     * @param dataFieldStartTag the data field start tag
     * @return the data field value starts with
     */
    public String getDataFieldValueStartsWith(Record record, String dataFieldStartTag) {
        StringBuilder fieldValue = new StringBuilder();
        if (record != null) {
            List<VariableField> variableFields = record.getVariableFields();
            if (!CollectionUtils.isEmpty(variableFields)) {
                for (VariableField variableField : variableFields) {
                    if (variableField != null && StringUtils.isNotBlank(variableField.getTag()) && variableField.getTag().startsWith(dataFieldStartTag)) {
                        DataField dataField = (DataField) variableField;
                        List<Subfield> subfields = dataField.getSubfields();
                        for (Subfield subfield : subfields) {
                            if (subfield != null && StringUtils.isNotBlank(subfield.getData())) {
                                fieldValue.append(subfield.getData());
                                fieldValue.append(" ");
                            }
                        }
                    }
                }
            }
        }
        return fieldValue.toString().trim();
    }

    /**
     *This method gets data field value for the given data field tag starts with and list of subfield tags from the marc record.
     *
     * @param record            the record
     * @param dataFieldStartTag the data field start tag
     * @param subFieldTags      the sub field tags
     * @return the data field value starts with
     */
    public String getDataFieldValueStartsWith(Record record, String dataFieldStartTag, List<Character> subFieldTags) {
        StringBuilder fieldValue = new StringBuilder();
        if (record != null) {
            List<VariableField> variableFields = record.getVariableFields();
            if (!CollectionUtils.isEmpty(variableFields)) {
                Subfield subfield;
                for (VariableField variableField : variableFields) {
                    if (variableField != null && StringUtils.isNotBlank(variableField.getTag()) && variableField.getTag().startsWith(dataFieldStartTag)) {
                        DataField dataField = (DataField) variableField;
                        for (Character subFieldTag : subFieldTags){
                            subfield = dataField.getSubfield(subFieldTag);
                            if (subfield != null) {
                                fieldValue.append(subfield.getData());
                                fieldValue.append(" ");
                            }
                        }
                    }
                }
            }
        }
        return fieldValue.toString().trim();
    }

    /**
     * This method gets list of data field values for the given data field tag starts with and list of subfield tags.
     *
     * @param record            the record
     * @param dataFieldStartTag the data field start tag
     * @param subFieldTags      the sub field tags
     * @return the list of data field values starts with
     */
    public List<String> getListOfDataFieldValuesStartsWith(Record record, String dataFieldStartTag, List<Character> subFieldTags) {
        List<String> fieldValues = new ArrayList<>();
        if (record != null) {
            List<VariableField> variableFields = record.getVariableFields();
            if (!CollectionUtils.isEmpty(variableFields)) {
                Subfield subfield;
                for (VariableField variableField : variableFields) {
                    if (variableField != null && StringUtils.isNotBlank(variableField.getTag()) && variableField.getTag().startsWith(dataFieldStartTag)) {
                        DataField dataField = (DataField) variableField;
                        for (Character subFieldTag : subFieldTags){
                            subfield = dataField.getSubfield(subFieldTag);
                            if (subfield != null) {
                                String data = subfield.getData();
                                if (StringUtils.isNotBlank(data)){
                                    fieldValues.add(data);
                                }
                            }
                        }
                    }
                }
            }
        }
        return fieldValues;
    }

    /**
     * Gets data field value.
     *
     * @param marcRecord the marc record
     * @param field      the field
     * @param ind1       the ind 1
     * @param ind2       the ind 2
     * @param subField   the sub field
     * @return the data field value
     */
    public String getDataFieldValue(Record marcRecord, String field, String ind1, String ind2, String subField) {
        List<String> strings = resolveValue(marcRecord, field, ind1, ind2, subField);
        return CollectionUtils.isEmpty(strings)? "" : strings.get(0);
    }

    /**
     * This method gets data field value for the given input fields from marc record.
     *
     * @param marcRecord the marc record
     * @param field      the field
     * @param ind1       the ind 1
     * @param ind2       the ind 2
     * @param subField   the sub field
     * @return the multi data field values
     */
    public List<String> getMultiDataFieldValues(Record marcRecord, String field, String ind1, String ind2, String subField) {
        return resolveValue(marcRecord, field, ind1, ind2, subField);
    }

    private List<String> resolveValue(Record marcRecord, String field, String ind1, String ind2, String subField) {
        List<String> values = new ArrayList<>();
        String indicator1 = StringUtils.isNotBlank(ind1) ? String.valueOf(ind1.charAt(0)) : " ";
        String indicator2 = StringUtils.isNotBlank(ind2) ? String.valueOf(ind2.charAt(0)) : " ";
        List<VariableField> dataFields = marcRecord.getVariableFields(field);

        for (Iterator<VariableField> variableFieldIterator = dataFields.iterator(); variableFieldIterator.hasNext(); ) {
            DataField dataField = (DataField) variableFieldIterator.next();
            if (dataField != null && doIndicatorsMatch(indicator1, indicator2, dataField)) {
                List<Subfield> subFields = dataField.getSubfields(subField);
                for (Iterator<Subfield> subfieldIterator = subFields.iterator(); subfieldIterator.hasNext(); ) {
                    Subfield subfield = subfieldIterator.next();
                    if (subField != null) {
                        String data = subfield.getData();
                        if (StringUtils.isNotBlank(data)) {
                            values.add(data);
                        }
                    }
                }
            }
        }
        return values;
    }

    private boolean doIndicatorsMatch(String indicator1, String indicator2, DataField dataField) {
        boolean result = true;
        if (StringUtils.isNotBlank(indicator1)) {
            result = dataField.getIndicator1() == indicator1.charAt(0);
        }
        if (StringUtils.isNotBlank(indicator2)) {
            result &= dataField.getIndicator2() == indicator2.charAt(0);
        }
        return result;
    }

    /**
     * This method gets control field value from the marRecord.
     *
     * @param marcRecord the marc record
     * @param field      the field
     * @return the control field value
     */
    public String getControlFieldValue(Record marcRecord, String field) {
        List<VariableField> variableFields = marcRecord.getVariableFields(field);
        for (Iterator<VariableField> variableFieldIterator = variableFields.iterator(); variableFieldIterator.hasNext(); ) {
            ControlFieldImpl controlField = (ControlFieldImpl) variableFieldIterator.next();
            if (controlField!=null) {
                return controlField.getData();
            }
        }
        return null;
    }

    /**
     * This method gets second indicator for data field.
     *
     * @param marcRecord the marc record
     * @param field      the field
     * @return the second indicator for data field
     */
    public Integer getSecondIndicatorForDataField(Record marcRecord, String field) {
        List<VariableField> dataFields = marcRecord.getVariableFields(field);
        if (!CollectionUtils.isEmpty(dataFields)) {
            DataField dataField = (DataField) dataFields.get(0);
            char dataFieldIndicator2 = dataField.getIndicator2();
            if (Character.isDigit(dataFieldIndicator2)) {
                return Character.getNumericValue(dataFieldIndicator2);
            }
        }
        return 0;
    }

    /**
     *  This method reads marc xml and gives back the list of record.
     *
     * @param marcXmlString the marc xml string
     * @return the list
     */
    public List<Record> readMarcXml(String marcXmlString) {
        List<Record> recordList = new ArrayList<>();
        InputStream in = new ByteArrayInputStream(marcXmlString.getBytes());
        MarcReader reader = new MarcXmlReader(in);
        while (reader.hasNext()) {
            Record record = reader.next();
            recordList.add(record);
        }
        return recordList;
    }

    /**
     * This method gets data field value for the given data field and sub field.
     *
     * @param record   the record
     * @param field    the field
     * @param subField the sub field
     * @return the data field value
     */
    public String getDataFieldValue(Record record, String field, char subField) {
        DataField dataField = getDataField(record, field);
        if (dataField != null) {
            Subfield subfield = dataField.getSubfield(subField);
            if (subfield != null) {
                return subfield.getData();
            }
        }
        return null;
    }

    private String getDataFieldValue(DataField dataField, char subField) {
        Subfield subfield = dataField.getSubfield(subField);
        if (subfield != null) {
            return subfield.getData();
        }
        return null;
    }

    /**
     * This method is used to check whether sub field exists.
     *
     * @param record the record
     * @param field  the field
     * @return the boolean
     */
    public boolean isSubFieldExists(Record record, String field) {
        DataField dataField = getDataField(record, field);
        if (dataField != null) {
            List<Subfield> subfields = dataField.getSubfields();
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(subfields)) {
                for (Subfield subfield : subfields) {
                    String data = subfield.getData();
                    if (StringUtils.isNotBlank(data)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *  This method gets data field.
     *
     * @param record the record
     * @param field  the field
     * @return the data field
     */
    public DataField getDataField(Record record, String field) {
        VariableField variableField = record.getVariableField(field);
        if (variableField != null) {
            DataField dataField = (DataField) variableField;
            if (dataField != null) {
                return dataField;
            }
        }
        return null;
    }

    /**
     * This method gets ind 1 for the given data field and subfield.
     *
     * @param record   the record
     * @param field    the field
     * @param subField the sub field
     * @return the ind 1
     */
    public Character getInd1(Record record, String field, char subField) {
        DataField dataField = getDataField(record, field);
        if (dataField != null) {
            Subfield subfield = dataField.getSubfield(subField);
            if (subfield != null) {
                return dataField.getIndicator1();
            }
        }
        return null;
    }

    /**
     * This method builds bib marc record for the given record.
     *
     * @param record the record
     * @return the bib marc record
     */
    public BibMarcRecord buildBibMarcRecord(Record record) {
        Record bibRecord = record;
        List<VariableField> holdingsVariableFields = new ArrayList<>();
        List<VariableField> itemVariableFields = new ArrayList<>();
        String[] holdingsTags = {"852"};
        String[] itemTags = {"876"};
        holdingsVariableFields.addAll(bibRecord.getVariableFields(holdingsTags));
        itemVariableFields.addAll(bibRecord.getVariableFields(itemTags));
        setVariableFields(holdingsVariableFields,bibRecord);
        setVariableFields(itemVariableFields,bibRecord);
        BibMarcRecord bibMarcRecord = new BibMarcRecord();
        bibMarcRecord.setBibRecord(bibRecord);

        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(holdingsVariableFields)) {
            MarcFactory marcFactory = MarcFactory.newInstance();
            List<HoldingsMarcRecord> holdingsMarcRecordList = new ArrayList<>();
            for (VariableField holdingsVariableField : holdingsVariableFields) {
                List<ItemMarcRecord> itemMarcRecords = new ArrayList<>();
                DataField holdingsDataField = (DataField) holdingsVariableField;

                String holdingsData = getDataFieldValue(holdingsDataField, '0');
                if (StringUtils.isNotBlank(holdingsData)) {
                    HoldingsMarcRecord holdingsMarcRecord = new HoldingsMarcRecord();
                    Record holdingsRecord = marcFactory.newRecord();
                    holdingsRecord.getDataFields().add(holdingsDataField);
                    holdingsMarcRecord.setHoldingsRecord(holdingsRecord);

                    if (org.apache.commons.collections.CollectionUtils.isNotEmpty(itemVariableFields)) {
                        for (VariableField itemVariableField : itemVariableFields) {
                            DataField itemDataField = (DataField) itemVariableField;
                            String itemData = getDataFieldValue(itemDataField, '0');
                            if ((itemData!=null)&&StringUtils.isNotBlank(itemData) && itemData.equalsIgnoreCase(holdingsData)) {
                                ItemMarcRecord itemMarcRecord = new ItemMarcRecord();
                                Record itemRecord = marcFactory.newRecord();
                                itemRecord.getDataFields().add(itemDataField);
                                itemMarcRecord.setItemRecord(itemRecord);
                                itemMarcRecords.add(itemMarcRecord);
                            }
                        }
                    }
                    holdingsMarcRecord.setItemMarcRecordList(itemMarcRecords);
                    holdingsMarcRecordList.add(holdingsMarcRecord);
                }
            }
            bibMarcRecord.setHoldingsMarcRecords(holdingsMarcRecordList);
        }
        return bibMarcRecord;
    }

    /**
     * This method converst marc record to marc xml content.
     *
     * @param record the record
     * @return the string
     */
    public String writeMarcXml(Record record) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MarcWriter marcWriter = new MarcXmlWriter(byteArrayOutputStream, true);
        marcWriter.write(record);
        marcWriter.close();
        String content = new String(byteArrayOutputStream.toByteArray());
        content = content.replaceAll("marcxml:", "");
        return content;
    }

/*
    private Record getRecordFromContent(byte[] content) {
        MarcReader reader;
        Record record = null;
        InputStream inputStream = new ByteArrayInputStream(content);
        reader = new MarcXmlReader(inputStream);
        while (reader.hasNext()) {
            record = reader.next();
        }
        return record;
    }
*/

    private void setVariableFields(List<VariableField> variableFields, Record bibRecord) {
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(variableFields)) {
            for (VariableField variableField : variableFields) {
                bibRecord.removeVariableField(variableField);
            }
        }

    }
}
