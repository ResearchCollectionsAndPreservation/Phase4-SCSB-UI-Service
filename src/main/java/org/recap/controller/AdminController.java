package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.jpa.BulkCustomerCodeEntity;
import org.recap.model.jpa.FileUploadEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.LocationEntity;
import org.recap.model.jpa.OwnerCodeEntity;
import org.recap.model.jpa.SCSBProprtiesEntity;
import org.recap.repository.jpa.BulkCustomerCodeDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.LocationDetailsRepository;
import org.recap.repository.jpa.OwnerCodeDetailsRepository;
import org.recap.repository.jpa.SCSBPropertiesDetailRepository;
import org.recap.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dinakar on 24/11/20.
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;
    @Autowired
    OwnerCodeDetailsRepository ownerCodeDetailsRepository;
    @Autowired
    BulkCustomerCodeDetailsRepository bulkCustomerCodeDetailsRepository;
    @Autowired
    LocationDetailsRepository locationDetailsRepository;
    @Autowired
    SCSBPropertiesDetailRepository scsbPropertiesDetailRepository;
    Map<String, String> fileuploadResponse = null;
    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) {
        fileuploadResponse = new LinkedHashMap<>();
        try {
            fileuploadResponse = loadInitialData(file);
        } catch (Exception e) {
            log.error(ScsbCommonConstants.LOG_ERROR, e);
        }
        return fileuploadResponse;
    }

    @PostMapping("/uploadIms")
    public Map<String, String> uploadIMSFile(@RequestParam("file") MultipartFile file) {
        fileuploadResponse = new LinkedHashMap<>();
        try {
            fileuploadResponse = loadIMSData(file);
        } catch (Exception e) {
            log.error(ScsbCommonConstants.LOG_ERROR, e);
        }
        return fileuploadResponse;
    }

    private Map<String, String> loadIMSData(MultipartFile multipartFile) {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        try {
            File file = convertMultiPartToFile(multipartFile);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            NodeList institutionNodeList = document.getElementsByTagName("Institution");
            for (int temp = 0; temp < institutionNodeList.getLength(); temp++) {
                Node node = institutionNodeList.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    institutionEntity.setInstitutionCode(eElement.getElementsByTagName("InstitutionCode").item(0).getTextContent());
                    institutionEntity.setInstitutionName(eElement.getElementsByTagName("InstitutionName").item(0).getTextContent());
                    institutionEntity.setIlsProtocol(eElement.getElementsByTagName("IlsProtocol").item(0).getTextContent());
                }
            }
            institutionEntity = institutionDetailsRepository.findByInstitutionCode(institutionEntity.getInstitutionCode());
            NodeList locationNodeList = document.getElementsByTagName("IMS-Location");
            loadLocationData(institutionEntity, locationNodeList);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error(ScsbCommonConstants.LOG_ERROR, e);
        }
        return fileuploadResponse;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
            return convFile;
        }
    }

    private Map<String, String> loadInitialData(MultipartFile multipartFile) {
        boolean status = true;
        InstitutionEntity institutionEntity = new InstitutionEntity();
        try {
            File file = convertMultiPartToFile(multipartFile);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            NodeList institutionNodeList = document.getElementsByTagName("Institution");
            institutionEntity = loadInstitutionData(institutionNodeList);
            FileUploadEntity fileUpload = new FileUploadEntity();
            fileUpload.setNewInstitutionname(institutionEntity.getInstitutionName());
            fileUpload.setOnBoardxmlfile(multipartFile.getBytes());
            fileUpload.setInstType(institutionEntity.getIlsProtocol());
            fileUpload.setOnBoardstatus("PENDING");
            fileUpload.setOnBoardcomments("");
            fileUpload.setCreatedBy("");
            fileUpload.setCreatedDate(new Date());
            fileUpload.setUpdatedBy("");
            fileUpload.setUpdatedDate(new Date());
            try {
                fileUploadService.uploadFile(fileUpload);
                fileuploadResponse.put("Upload XML File Status", ScsbConstants.SUCCESSED);
            } catch (Exception e) {
                fileuploadResponse.put("Upload XML File Status", ScsbConstants.FAILED);
                log.error(ScsbCommonConstants.LOG_ERROR, e);
            }
            NodeList configNodeList = document.getElementsByTagName("entry");
            loadConfigData(institutionEntity, configNodeList);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error(ScsbCommonConstants.LOG_ERROR, e);
        }

        for (Map.Entry<String, String> entry : fileuploadResponse.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(ScsbConstants.FAILED)) {
                status = false;
                break;
            }
        }
        if (status) {
            try {
                fileUploadService.updateFileUploadEntity(institutionEntity.getInstitutionName(), "Test", "COMPLETED", new Date(), "Testing comments");
                fileuploadResponse.put(ScsbConstants.ON_BOARD_INSTITUTION_STATUS, ScsbConstants.SUCCESSED);
            } catch (Exception e) {
                fileuploadResponse.put(ScsbConstants.ON_BOARD_INSTITUTION_STATUS, ScsbConstants.FAILED);
                log.error(ScsbCommonConstants.LOG_ERROR, e);
            }
        } else {
            fileuploadResponse.put(ScsbConstants.ON_BOARD_INSTITUTION_STATUS, ScsbConstants.FAILED);
        }
        return fileuploadResponse;
    }


    private InstitutionEntity loadInstitutionData(NodeList institutionNodeList) {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        for (int temp = 0; temp < institutionNodeList.getLength(); temp++) {
            Node node = institutionNodeList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                institutionEntity.setInstitutionCode(eElement.getElementsByTagName("InstitutionCode").item(0).getTextContent());
                institutionEntity.setInstitutionName(eElement.getElementsByTagName("InstitutionName").item(0).getTextContent());
                institutionEntity.setIlsProtocol(eElement.getElementsByTagName("IlsProtocol").item(0).getTextContent());
                try {
                    institutionDetailsRepository.save(institutionEntity);
                    fileuploadResponse.put("Institution Added Status", ScsbConstants.SUCCESSED);
                } catch (Exception e) {
                    fileuploadResponse.put("Institution Added Status", ScsbConstants.FAILED);
                    log.error(ScsbCommonConstants.LOG_ERROR, e);
                }

            }
        }
        return institutionEntity;
    }

    private void loadConfigData(InstitutionEntity institutionEntity, NodeList configNodeList) {
        for (int temp = 0; temp < configNodeList.getLength(); temp++) {
            Node configNode = configNodeList.item(temp);
            if (configNode.getNodeType() == Node.ELEMENT_NODE) {
                SCSBProprtiesEntity scsbProprtiesEntity = new SCSBProprtiesEntity();
                Element configElement = (Element) configNode;

                scsbProprtiesEntity.setP_key(configElement.getAttribute("key"));
                scsbProprtiesEntity.setP_value(configNode.getTextContent());
                scsbProprtiesEntity.setDescription("");
                scsbProprtiesEntity.setInstitutionCode(institutionEntity.getInstitutionCode());
                scsbProprtiesEntity.setImsLocationCode("");
                scsbProprtiesEntity.setActive("Y");
                scsbProprtiesEntity.setCreatedDate(new Date());
                scsbProprtiesEntity.setLastUpdatedDate(new Date());
                scsbProprtiesEntity.setCreatedBy(ScsbConstants.ADMIN_TEXT);
                scsbProprtiesEntity.setLastUpdatedBy(ScsbConstants.ADMIN_TEXT);
                try {
                    scsbPropertiesDetailRepository.saveAndFlush(scsbProprtiesEntity);
                    fileuploadResponse.put("SCSB Properties Added Status", ScsbConstants.SUCCESSED);
                } catch (Exception e) {
                    fileuploadResponse.put("SCSB Properties Added Status", ScsbConstants.FAILED);
                    log.error(ScsbCommonConstants.LOG_ERROR, e);
                }
            }
        }
    }

    private void loadLocationData(InstitutionEntity institutionEntity, NodeList locationNodeList) {
        for (int temp = 0; temp < locationNodeList.getLength(); temp++) {
            LocationEntity locationEntity = new LocationEntity();
            Node locationNode = locationNodeList.item(temp);
            if (locationNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) locationNode;

                locationEntity.setLocationCode(eElement.getElementsByTagName("IMS-LocationCode").item(0).getTextContent());
                locationEntity.setLocationName(eElement.getElementsByTagName("IMS-LocationName").item(0).getTextContent());
                locationEntity.setDescription(eElement.getElementsByTagName(ScsbConstants.DESCRIPTION).item(0).getTextContent());
                locationEntity.setActive("Y");
                locationEntity.setCreatedBy(ScsbConstants.ADMIN_TEXT);
                locationEntity.setCreatedDate(new Date());
                locationEntity.setLastUpdatedBy(ScsbConstants.ADMIN_TEXT);
                locationEntity.setLastUpdatedDate(new Date());
                try {
                    locationDetailsRepository.saveAndFlush(locationEntity);
                    fileuploadResponse.put("Locations Added Status", ScsbConstants.SUCCESSED);
                } catch (Exception e) {
                    fileuploadResponse.put("Locations Added Status", ScsbConstants.FAILED);
                    log.error(ScsbCommonConstants.LOG_ERROR, e);
                }
                if (locationNode.hasChildNodes()) {
                    NodeList nodeList = locationNode.getChildNodes();
                    for (int tempnode = 0; tempnode < nodeList.getLength(); tempnode++) {
                        Node node = nodeList.item(tempnode);
                        if (node.getNodeName().equals("CustomerCodes")) {
                            if (node.hasChildNodes()) {
                                NodeList customerNodeList = node.getChildNodes();
                                for (int custnode = 0; custnode < customerNodeList.getLength(); custnode++) {
                                    Node customerNode = customerNodeList.item(custnode);
                                    if (customerNode.getNodeName().equals("CustomerCode")) {
                                        loadCustomerData(institutionEntity, customerNode);
                                    }
                                }
                            }
                        }
                        if (node.getNodeName().equals("BulkCustomerCodes")) {
                            if (node.hasChildNodes()) {
                                NodeList bulkCustomerNodeList = node.getChildNodes();
                                for (int bulkCustNode = 0; bulkCustNode < bulkCustomerNodeList.getLength(); bulkCustNode++) {
                                    Node bulkCustomerNode = bulkCustomerNodeList.item(bulkCustNode);
                                    if (bulkCustomerNode.getNodeName().equals("BulkCustomerCode")) {
                                        loadBulkCustomerData(institutionEntity, bulkCustomerNode);
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private void loadCustomerData(InstitutionEntity institutionEntity, Node customerNode) {
        if (customerNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) customerNode;

            OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
            ownerCodeEntity.setInstitutionId(institutionEntity.getId());
            ownerCodeEntity.setOwnerCode(eElement.getElementsByTagName("Code").item(0).getTextContent());
         //   ownerCodeEntity.setDeliveryRestrictions(eElement.getElementsByTagName("DeliveryRestrictions").item(0).getTextContent());
            ownerCodeEntity.setDescription(eElement.getElementsByTagName("Description").item(0).getTextContent());
           // ownerCodeEntity.setPwdDeliveryRestrictions(eElement.getElementsByTagName("PWDDeliveryRestrictions").item(0).getTextContent());
           // ownerCodeEntity.setRecapDeliveryRestrictions(eElement.getElementsByTagName("IMS-LocationDeliveryRestrictions").item(0).getTextContent());
       //     ownerCodeEntity.setPickupLocation(eElement.getElementsByTagName("CircdeskLocation").item(0).getTextContent());
            try {
                ownerCodeDetailsRepository.saveAndFlush(ownerCodeEntity);
                fileuploadResponse.put("Customer Code Added Status", ScsbConstants.SUCCESSED);
            } catch (Exception e) {
                fileuploadResponse.put("Customer Code Added Status", ScsbConstants.FAILED);
                log.error(ScsbCommonConstants.LOG_ERROR, e);
            }
        }
    }

    private void loadBulkCustomerData(InstitutionEntity institutionEntity, Node customerNode) {
        if (customerNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) customerNode;

            BulkCustomerCodeEntity bulkCustomerCodeEntity = new BulkCustomerCodeEntity();
            bulkCustomerCodeEntity.setOwningInstitutionId(institutionEntity.getId());
            bulkCustomerCodeEntity.setCustomerCode(eElement.getElementsByTagName("Code").item(0).getTextContent());
            bulkCustomerCodeEntity.setDescription(eElement.getElementsByTagName("Description").item(0).getTextContent());
            try {
                bulkCustomerCodeDetailsRepository.saveAndFlush(bulkCustomerCodeEntity);
                fileuploadResponse.put(" Bulk Customer Code Added Status", ScsbConstants.SUCCESSED);
            } catch (Exception e) {
                fileuploadResponse.put(" Bulk Customer Code Added Status", ScsbConstants.FAILED);
                log.error(ScsbCommonConstants.LOG_ERROR, e);
            }
        }
    }
}
