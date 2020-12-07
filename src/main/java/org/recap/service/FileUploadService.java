package org.recap.service;

import org.recap.model.jpa.FileUploadEntity;
import org.recap.repository.jpa.FileUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
 * @author dinakar on 01/12/20
 */
@Service
public class FileUploadService {
    @Autowired
    private FileUploadRepository fileUploadRepository;

    public FileUploadEntity uploadFile(FileUploadEntity fileUpload) {
        return fileUploadRepository.saveAndFlush(fileUpload);
    }
    public FileUploadEntity getFileUploadEntity(String newInstitutionname) {
        return fileUploadRepository.findByInstitutionName(newInstitutionname);
    }
    public int updateFileUploadEntity(String newInstitutionname,String updatedBy, String onBoardstatus, Date updatedDate, String onBoardcomments) {
        return fileUploadRepository.updateFileUploadEntity(newInstitutionname,updatedBy,onBoardstatus,updatedDate,onBoardcomments);
    }

}
