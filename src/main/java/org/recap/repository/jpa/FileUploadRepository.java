package org.recap.repository.jpa;

import org.recap.model.jpa.CustomerCodeEntity;
import org.recap.model.jpa.FileUploadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author dinakar on 01/12/20
 */
@Repository
public interface FileUploadRepository extends BaseRepository<FileUploadEntity> {

    @Query(value="select fileUpload from FileUploadEntity fileUpload where fileUpload.newInstitutionname =:newInstitutionname")
    FileUploadEntity findByInstitutionName(@Param("newInstitutionname") String newInstitutionname);

    @Modifying
    @Transactional
    @Query(value="update FileUploadEntity fileUpload set fileUpload.updatedBy=:updatedBy,fileUpload.onBoardstatus=:onBoardstatus,fileUpload.onBoardcomments=:onBoardcomments,fileUpload.updatedDate=:updatedDate  where fileUpload.newInstitutionname =:newInstitutionname")
    int updateFileUploadEntity(@Param("newInstitutionname") String newInstitutionname,@Param("updatedBy") String updatedBy, @Param("onBoardstatus") String onBoardstatus, @Param("updatedDate") Date updatedDate, @Param("onBoardcomments") String onBoardcomments);
}
