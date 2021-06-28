package org.recap.repository.jpa;

import org.recap.model.jpa.BulkRequestItemEntity;
import org.recap.model.jpa.RequestItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by akulak on 22/9/17.
 */

public interface BulkRequestDetailsRepository extends BaseRepository<BulkRequestItemEntity> {
    @Query(value = "select br from BulkRequestItemEntity br inner join br.imsLocationEntity status where (br.imsLocation = :imsLocation OR :imsLocation = 0) and (br.id = :id OR :id =0) and (br.bulkRequestName = :bulkRequestName OR :bulkRequestName = '') and (br.patronId = :patronId OR :patronId = '') and (br.requestingInstitutionId = :requestingInstitutionId OR :requestingInstitutionId = 0)")
    Page<BulkRequestItemEntity> findBulkRequestItems(Pageable pageable, Integer id, String bulkRequestName, String patronId, Integer requestingInstitutionId,Integer imsLocation);

    List<BulkRequestItemEntity> findByIdIn(List<Integer> id);

    @Query(value="select bulkRequestStatus from BulkRequestItemEntity br where br.bulkRequestStatus NOT IN ('IN PROCESS')")
    List<String> findAllBulkRequestStatusDescExceptProcessing();

}
