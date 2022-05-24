package org.recap.controller;

import org.apache.commons.compress.utils.IOUtils;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.FileUploadEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.repository.jpa.BulkCustomerCodeDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.LocationDetailsRepository;
import org.recap.repository.jpa.OwnerCodeDetailsRepository;
import org.recap.repository.jpa.SCSBPropertiesDetailRepository;
import org.recap.service.FileUploadService;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public class AdminControllerUT extends BaseTestCaseUT {

    @InjectMocks
    AdminController adminController;

    @Mock
    FileUploadService fileUploadService;

    @Mock
    MultipartFile multipartFile;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    SCSBPropertiesDetailRepository scsbPropertiesDetailRepository;

    @Mock
    LocationDetailsRepository locationDetailsRepository;

    @Mock
    OwnerCodeDetailsRepository ownerCodeDetailsRepository;

    @Mock
    BulkCustomerCodeDetailsRepository bulkCustomerCodeDetailsRepository;

    @Mock
    NodeList nodeList;

    @Mock
    Element element;

    @Test
    public void uploadFile() throws IOException, URISyntaxException {
        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        URL resource = getClass().getResource("OnBoardInstitution.xml");
        assertNotNull(resource);
        File file = new File(resource.toURI());
        assertNotNull(file);
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        Mockito.when(fileUploadService.uploadFile(any())).thenReturn(fileUploadEntity);
        Map<String, String> uploadFile = adminController.uploadFile(multipartFile);
        assertNotNull(uploadFile);
    }
    @Test
    public void uploadFileFailed() throws IOException, URISyntaxException {
        URL resource = getClass().getResource("BibContent.xml");
        assertNotNull(resource);
        File file = new File(resource.toURI());
        assertNotNull(file);
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        Mockito.doThrow(new NullPointerException()).when(fileUploadService).uploadFile(any());
        Map<String, String> uploadFile = adminController.uploadFile(multipartFile);
        assertNotNull(uploadFile);
    }
    @Test
    public void uploadFileInnerException() throws IOException, URISyntaxException {
        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        URL resource = getClass().getResource("BibContent.xml");
        assertNotNull(resource);
        File file = new File(resource.toURI());
        assertNotNull(file);
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        Mockito.when(fileUploadService.uploadFile(any())).thenReturn(fileUploadEntity);
        Mockito.doThrow(new NullPointerException()).when(fileUploadService).updateFileUploadEntity(any(), anyString(), anyString(), any(), anyString());
        Map<String, String> uploadFile = adminController.uploadFile(multipartFile);
        assertNotNull(uploadFile);
    }
    @Test
    public void uploadFileException() throws IOException, URISyntaxException {
        Map<String, String> uploadFile = adminController.uploadFile(null);
        assertNotNull(uploadFile);
    }
    @Test
    public void uploadFileParserConfigurationException() throws IOException, URISyntaxException {
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("AdminDetails");
        Mockito.when(multipartFile.getBytes()).thenReturn(new byte[1]);
        Map<String, String> uploadFile = adminController.uploadFile(multipartFile);
        assertNotNull(uploadFile);
    }
    @Test
    public void uploadIMSFile() throws IOException, URISyntaxException {
        URL resource = getClass().getResource("OnBoardInstitution.xml");
        assertNotNull(resource);
        File file = new File(resource.toURI());
        assertNotNull(file);
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Map<String, String> uploadFile = adminController.uploadIMSFile(multipartFile);
        assertNotNull(uploadFile);
    }
    @Test
    public void uploadIMSFileException() {
        Map<String, String> uploadFile = adminController.uploadIMSFile(null);
        assertNotNull(uploadFile);
    }
    @Test
    public void uploadIMSFileParserConfigurationException() throws IOException {
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("AdminDetails");
        Mockito.when(multipartFile.getBytes()).thenReturn(new byte[1]);
        Map<String, String> uploadFile = adminController.uploadIMSFile(multipartFile);
        assertNotNull(uploadFile);
    }
    @Test
    public void loadInstitutionData(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        Mockito.when(nodeList.getLength()).thenReturn(1);
        Mockito.when(nodeList.item(0)).thenReturn(element);
        Mockito.when(element.getNodeType()).thenReturn(Node.ELEMENT_NODE);
        Mockito.when(element.getElementsByTagName("InstitutionCode")).thenReturn(nodeList);
        Mockito.when(element.getElementsByTagName("InstitutionName")).thenReturn(nodeList);
        Mockito.when(element.getElementsByTagName("IlsProtocol")).thenReturn(nodeList);
        Mockito.when(nodeList.item(0).getTextContent()).thenReturn("PUL");
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadInstitutionData", nodeList);
    }

    @Test
    public void loadInstitutionDataExceptionTest(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        Mockito.when(nodeList.getLength()).thenReturn(1);
        Mockito.when(nodeList.item(0)).thenReturn(element);
        Mockito.when(element.getElementsByTagName("InstitutionCode")).thenReturn(nodeList);
        Mockito.when(element.getElementsByTagName("InstitutionName")).thenReturn(nodeList);
        Mockito.when(element.getElementsByTagName("IlsProtocol")).thenReturn(nodeList);
        Mockito.when(nodeList.item(0).getTextContent()).thenReturn("PUL");
        Mockito.doThrow(new NullPointerException()).when(institutionDetailsRepository).save(any());
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadInstitutionData", nodeList);
    }

    @Test
    public void loadInstitutionDataException(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        Mockito.when(nodeList.getLength()).thenReturn(1);
        Mockito.when(nodeList.item(0)).thenReturn(element);
        Mockito.when(element.getNodeType()).thenReturn(Node.ELEMENT_NODE);
        Mockito.when(element.getElementsByTagName("InstitutionCode")).thenReturn(nodeList);
        Mockito.when(element.getElementsByTagName("InstitutionName")).thenReturn(nodeList);
        Mockito.when(element.getElementsByTagName("IlsProtocol")).thenReturn(nodeList);
        Mockito.when(nodeList.item(0).getTextContent()).thenReturn("PUL");
        Mockito.doThrow(new NullPointerException()).when(institutionDetailsRepository).save(any());
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadInstitutionData", nodeList);
    }
    @Test
    public void loadConfigData(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(nodeList.getLength()).thenReturn(1);
        Mockito.when(nodeList.item(0)).thenReturn(element);
        Mockito.when(element.getNodeType()).thenReturn(Node.ELEMENT_NODE);
        Mockito.when(element.getAttribute(anyString())).thenReturn("test");
        Mockito.when(element.getTextContent()).thenReturn("test");
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadConfigData",institutionEntity, nodeList);
    }
    @Test
    public void loadConfigDataException(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(nodeList.getLength()).thenReturn(1);
        Mockito.when(nodeList.item(0)).thenReturn(element);
        Mockito.when(element.getNodeType()).thenReturn(Node.ELEMENT_NODE);
        Mockito.when(element.getAttribute(anyString())).thenReturn("test");
        Mockito.when(element.getTextContent()).thenReturn("test");
        Mockito.doThrow(new NullPointerException()).when(scsbPropertiesDetailRepository).saveAndFlush(any());
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadConfigData",institutionEntity, nodeList);
    }

    @Test
    public void loadConfigDataExceptionTest(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(nodeList.getLength()).thenReturn(1);
        Mockito.when(nodeList.item(0)).thenReturn(element);
        Mockito.when(element.getAttribute(anyString())).thenReturn("test");
        Mockito.when(element.getTextContent()).thenReturn("test");
        Mockito.doThrow(new NullPointerException()).when(scsbPropertiesDetailRepository).saveAndFlush(any());
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadConfigData",institutionEntity, nodeList);
    }
    @Test
    public void loadLocationData(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(nodeList.getLength()).thenReturn(1);
        Mockito.when(nodeList.item(0)).thenReturn(element);
        Mockito.when(element.getNodeType()).thenReturn(Node.ELEMENT_NODE);
        Mockito.when(element.hasChildNodes()).thenReturn(Boolean.TRUE);
        Mockito.when(element.getChildNodes()).thenReturn(nodeList);
        Mockito.when(element.getElementsByTagName(any())).thenReturn(nodeList);
        Mockito.when(nodeList.item(0).getTextContent()).thenReturn("Recap");
        Mockito.when(element.getNodeName()).thenReturn("CustomerCodes");
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadLocationData",institutionEntity, nodeList);
    }
    @Test
    public void loadLocationDataForBulkCustomerCodes(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(nodeList.getLength()).thenReturn(1);
        Mockito.when(nodeList.item(anyInt())).thenReturn(element);
        Mockito.when(element.getNodeType()).thenReturn(Node.ELEMENT_NODE);
        Mockito.when(element.hasChildNodes()).thenReturn(Boolean.TRUE);
        Mockito.when(element.getChildNodes()).thenReturn(nodeList);
        Mockito.when(element.getElementsByTagName(any())).thenReturn(nodeList);
        Mockito.when(nodeList.item(0).getTextContent()).thenReturn("Recap");
        Mockito.when(element.getNodeName()).thenReturn("BulkCustomerCodes");
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadLocationData",institutionEntity, nodeList);
    }
    @Test
    public void loadLocationDataException(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(nodeList.getLength()).thenReturn(1);
        Mockito.when(nodeList.item(anyInt())).thenReturn(element);
        Mockito.when(element.getNodeType()).thenReturn(Node.ELEMENT_NODE);
        Mockito.when(element.hasChildNodes()).thenReturn(Boolean.TRUE);
        Mockito.when(element.getChildNodes()).thenReturn(nodeList);
        Mockito.when(element.getElementsByTagName(any())).thenReturn(nodeList);
        Mockito.when(nodeList.item(0).getTextContent()).thenReturn("Recap");
        Mockito.when(element.getNodeName()).thenReturn("BulkCustomerCodes");
        Mockito.doThrow(new NullPointerException()).when(locationDetailsRepository).saveAndFlush(any());
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadLocationData",institutionEntity, nodeList);
    }

    @Test
    public void loadLocationDataExceptionTest(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(nodeList.getLength()).thenReturn(1);
        Mockito.when(nodeList.item(anyInt())).thenReturn(element);
        Mockito.when(element.hasChildNodes()).thenReturn(Boolean.TRUE);
        Mockito.when(element.getChildNodes()).thenReturn(nodeList);
        Mockito.when(element.getElementsByTagName(any())).thenReturn(nodeList);
        Mockito.when(nodeList.item(0).getTextContent()).thenReturn("Recap");
        Mockito.when(element.getNodeName()).thenReturn("BulkCustomerCodes");
        Mockito.doThrow(new NullPointerException()).when(locationDetailsRepository).saveAndFlush(any());
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadLocationData",institutionEntity, nodeList);
    }

    @Test
    public void loadCustomerData(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(nodeList.item(anyInt())).thenReturn(element);
        Mockito.when(element.getElementsByTagName(any())).thenReturn(nodeList);
        Mockito.when(element.getNodeType()).thenReturn(Node.ELEMENT_NODE);
        Mockito.when(element.getTextContent()).thenReturn("test");
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadCustomerData",institutionEntity, element);
    }
    @Test
    public void loadCustomerDataTest(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadCustomerData",institutionEntity, element);
    }
    @Test
    public void loadCustomerDataException(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(nodeList.item(anyInt())).thenReturn(element);
        Mockito.when(element.getElementsByTagName(any())).thenReturn(nodeList);
        Mockito.when(element.getNodeType()).thenReturn(Node.ELEMENT_NODE);
        Mockito.when(element.getTextContent()).thenReturn("test");
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        Mockito.doThrow(new NullPointerException()).when(ownerCodeDetailsRepository).saveAndFlush(any());
        ReflectionTestUtils.invokeMethod(adminController,"loadCustomerData",institutionEntity, element);
    }
    @Test
    public void loadBulkCustomerData(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(nodeList.item(anyInt())).thenReturn(element);
        Mockito.when(element.getElementsByTagName(any())).thenReturn(nodeList);
        Mockito.when(element.getNodeType()).thenReturn(Node.ELEMENT_NODE);
        Mockito.when(element.getTextContent()).thenReturn("test");
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadBulkCustomerData",institutionEntity, element);
    }

    @Test
    public void loadBulkCustomerDataTest(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        ReflectionTestUtils.invokeMethod(adminController,"loadBulkCustomerData",institutionEntity, element);
    }
    @Test
    public void loadBulkCustomerDataException(){
        Map<String, String> fileuploadResponse = new HashMap<>();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(nodeList.item(anyInt())).thenReturn(element);
        Mockito.when(element.getElementsByTagName(any())).thenReturn(nodeList);
        Mockito.when(element.getNodeType()).thenReturn(Node.ELEMENT_NODE);
        Mockito.when(element.getTextContent()).thenReturn("test");
        ReflectionTestUtils.setField(adminController,"fileuploadResponse",fileuploadResponse);
        Mockito.doThrow(new NullPointerException()).when(bulkCustomerCodeDetailsRepository).saveAndFlush(any());
        ReflectionTestUtils.invokeMethod(adminController,"loadBulkCustomerData",institutionEntity, element);
    }
    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        return institutionEntity;
    }
}
