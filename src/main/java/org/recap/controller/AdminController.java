package org.recap.controller;

import org.recap.model.jpa.BulkCustomerCodeEntity;
import org.recap.model.jpa.CustomerCodeEntity;
import org.recap.model.jpa.DeliveryRestrictionEntity;
import org.recap.model.jpa.FileUploadEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.LocationEntity;
import org.recap.model.jpa.SCSBProprtiesEntity;
import org.recap.repository.jpa.BulkCustomerCodeDetailsRepository;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.LocationDetailsRepository;
import org.recap.repository.jpa.SCSBPropertiesDetailRepository;
import org.recap.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by dinakar on 24/11/20.
 */
@RestController
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;
    @Autowired
    CustomerCodeDetailsRepository customerCodeDetailsRepository;
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
        fileuploadResponse= new LinkedHashMap<>();
        try {
            fileuploadResponse = loadInitialData(file);
            String content = new String(file.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileuploadResponse;
    }

    @PostMapping("/uploadIms")
    public Map<String, String> uploadIMSFile(@RequestParam("file") MultipartFile file) {
        fileuploadResponse = new LinkedHashMap<>();
        try {
            fileuploadResponse = loadIMSData(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileuploadResponse;
    }

    private Map<String, String> loadIMSData(MultipartFile multipartFile) {
        logger.info("Load IMS Locations");
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
                    institutionEntity.setIls_protocol(eElement.getElementsByTagName("ils_protocol").item(0).getTextContent());
                    institutionEntity.setProtocol(eElement.getElementsByTagName("protocol").item(0).getTextContent());
                }
            }
            institutionEntity = institutionDetailsRepository.findByInstitutionCode(institutionEntity.getInstitutionCode());
            NodeList locationNodeList = document.getElementsByTagName("IMS-Location");
            loadLocationData(document, institutionEntity, locationNodeList);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileuploadResponse;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private Map<String, String> loadInitialData(MultipartFile multipartFile) {
        boolean status = true;
        InstitutionEntity institutionEntity = new InstitutionEntity();
        logger.info("loadInitialData -->");
        try {
            //URL resource = LoginController.class.getClassLoader().getResource("onboard-xmlscript.xml");
            File file = convertMultiPartToFile(multipartFile);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            NodeList institutionNodeList = document.getElementsByTagName("Institution");
            institutionEntity = loadInstitutionData(institutionNodeList);
            FileUploadEntity fileUpload = new FileUploadEntity();
            fileUpload.setNewInstitutionname(institutionEntity.getInstitutionName());
            fileUpload.setOnBoardxmlfile(multipartFile.getBytes());
            fileUpload.setInstType(institutionEntity.getProtocol());
            fileUpload.setOnBoardstatus("PENDING");
            fileUpload.setOnBoardcomments("");
            fileUpload.setCreatedBy("");
            fileUpload.setCreatedDate(new Date());
            fileUpload.setUpdatedBy("");
            fileUpload.setUpdatedDate(new Date());
            try {
                fileUploadService.uploadFile(fileUpload);
                fileuploadResponse.put("Upload XML File Status", "Successed");
            } catch (Exception e) {
                fileuploadResponse.put("Upload XML File Status", "Failed");
                e.printStackTrace();
            }
            /*try {
                FileUploadEntity fileUploadEntity= fileUploadService.getFileUploadEntity(institutionEntity.getInstitutionName());
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            /*NodeList locationNodeList = document.getElementsByTagName("IMS-Location");
            loadLocationData(document, institutionEntity, locationNodeList);*/

           /* NodeList crosspartnerNodeList = document.getElementsByTagName("crosspartner");
            System.out.println("crosspartnerList size now  >>> " + crosspartnerNodeList.getLength());
            loadCrossPartnerData(document, institutionEntity, crosspartnerNodeList);*/

            NodeList configNodeList = document.getElementsByTagName("entry");
            loadConfigData(document, institutionEntity, configNodeList);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
       /* fileuploadResponse.forEach((k,v) ->{
            if(v.equalsIgnoreCase("Failed")){
                status.set(false);
            }
        });*/
        for (Map.Entry<String, String> entry : fileuploadResponse.entrySet()) {
            if (entry.getValue().equalsIgnoreCase("Failed")) {
                status = false;
            }
        }
        if (status == true) {
            try {
                fileUploadService.updateFileUploadEntity(institutionEntity.getInstitutionName(), "Test", "COMPLETED", new Date(), "Testing comments");
                fileuploadResponse.put("On-Board Institution Status", "Successed");
            } catch (Exception e) {
                fileuploadResponse.put("On-Board Institution Status", "Failed");
                e.printStackTrace();
            }
        } else {
            fileuploadResponse.put("On-Board Institution Status", "Failed");
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
                institutionEntity.setIls_protocol(eElement.getElementsByTagName("ils_protocol").item(0).getTextContent());
                institutionEntity.setProtocol(eElement.getElementsByTagName("protocol").item(0).getTextContent());
                try {
                    institutionDetailsRepository.save(institutionEntity);
                    fileuploadResponse.put("Institution Added Status", "Successed");
                } catch (Exception e) {
                    fileuploadResponse.put("Institution Added Status", "Failed");
                    e.printStackTrace();
                }

            }
        }
        return institutionEntity;
    }

    private void loadConfigData(Document document, InstitutionEntity institutionEntity, NodeList configNodeList) {
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
                scsbProprtiesEntity.setCreatedBy("Admin");
                scsbProprtiesEntity.setLastUpdatedBy("Admin");
                try {
                    scsbPropertiesDetailRepository.saveAndFlush(scsbProprtiesEntity);
                    fileuploadResponse.put("SCSB Properties Added Status", "Successed");
                } catch (Exception e) {
                    fileuploadResponse.put("SCSB Properties Added Status", "Failed");
                    e.printStackTrace();
                }
            }

        }

    }

    private void loadLocationData(Document document, InstitutionEntity institutionEntity, NodeList locationNodeList) {
        for (int temp = 0; temp < locationNodeList.getLength(); temp++) {
            LocationEntity locationEntity = new LocationEntity();
            Node locationNode = locationNodeList.item(temp);
            if (locationNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) locationNode;

                locationEntity.setLocationCode(eElement.getElementsByTagName("IMS-LocationCode").item(0).getTextContent());
                locationEntity.setLocationName(eElement.getElementsByTagName("IMS-LocationName").item(0).getTextContent());
                locationEntity.setDescription(eElement.getElementsByTagName("Description").item(0).getTextContent());
                locationEntity.setActive("Y");
                locationEntity.setCreatedBy("Admin");
                locationEntity.setCreatedDate(new Date());
                locationEntity.setLastUpdatedBy("Admin");
                locationEntity.setLastUpdatedDate(new Date());
                try {
                    locationDetailsRepository.saveAndFlush(locationEntity);
                    fileuploadResponse.put("Locations Added Status", "Successed");
                } catch (Exception e) {
                    fileuploadResponse.put("Locations Added Status", "Failed");
                    e.printStackTrace();
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
                                        loadCustomerData(document, institutionEntity, locationNode, locationEntity, customerNode);
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
                                        loadBulkCustomerData(document, institutionEntity, locationNode, locationEntity, bulkCustomerNode);
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }


    private void loadCustomerData(Document document, InstitutionEntity institutionEntity, Node locationNode, LocationEntity locationEntity, Node customerNode) {
        if (customerNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) customerNode;

            CustomerCodeEntity customerCodeEntity = new CustomerCodeEntity();
            customerCodeEntity.setOwningInstitutionId(institutionEntity.getId());
            customerCodeEntity.setCustomerCode(eElement.getElementsByTagName("Code").item(0).getTextContent());
            customerCodeEntity.setDeliveryRestrictions(eElement.getElementsByTagName("DeliveryRestrictions").item(0).getTextContent());
            customerCodeEntity.setDescription(eElement.getElementsByTagName("Description").item(0).getTextContent());
            customerCodeEntity.setPwdDeliveryRestrictions(eElement.getElementsByTagName("PWDDeliveryRestrictions").item(0).getTextContent());
            customerCodeEntity.setRecapDeliveryRestrictions(eElement.getElementsByTagName("IMS-LocationDeliveryRestrictions").item(0).getTextContent());
            customerCodeEntity.setPickupLocation(eElement.getElementsByTagName("CircdeskLocation").item(0).getTextContent());
            try {
                customerCodeDetailsRepository.saveAndFlush(customerCodeEntity);
                fileuploadResponse.put("Customer Code Added Status", "Successed");
            } catch (Exception e) {
                fileuploadResponse.put("Customer Code Added Status", "Failed");
                e.printStackTrace();
            }
        }
    }

    private void loadBulkCustomerData(Document document, InstitutionEntity institutionEntity, Node locationNode, LocationEntity locationEntity, Node customerNode) {
        if (customerNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) customerNode;

            BulkCustomerCodeEntity bulkCustomerCodeEntity = new BulkCustomerCodeEntity();
            bulkCustomerCodeEntity.setOwningInstitutionId(institutionEntity.getId());
            bulkCustomerCodeEntity.setCustomerCode(eElement.getElementsByTagName("Code").item(0).getTextContent());
            bulkCustomerCodeEntity.setDescription(eElement.getElementsByTagName("Description").item(0).getTextContent());
            try {
                bulkCustomerCodeDetailsRepository.saveAndFlush(bulkCustomerCodeEntity);
                fileuploadResponse.put(" Bulk Customer Code Added Status", "Successed");
            } catch (Exception e) {
                fileuploadResponse.put(" Bulk Customer Code Added Status", "Failed");
                e.printStackTrace();
            }
        }
    }

    private void loadCrossPartnerData(Document document, InstitutionEntity institutionEntity, NodeList crosspartnerNodeList) {
        for (int temp = 0; temp < crosspartnerNodeList.getLength(); temp++) {
            DeliveryRestrictionEntity deliveryRestrictionEntity = new DeliveryRestrictionEntity();
            Node crossPartnerNode = crosspartnerNodeList.item(temp);
            if (crossPartnerNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) crossPartnerNode;
                deliveryRestrictionEntity.setInstitutionEntity(institutionEntity);
                deliveryRestrictionEntity.setDeliveryRestriction(eElement.getElementsByTagName("deliveryrestrictions").item(0).getTextContent());
                //      deliveryRestrictionDetailsRepository.saveAndFlush(deliveryRestrictionEntity);
                //        bulkCustomerCodeEntity.setCustomerCode(eElement.getElementsByTagName("Code").item(0).getTextContent());

            }
        }
    }
}
