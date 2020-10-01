package org.recap.repository.jpa;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.BibliographicPK;
import org.recap.model.jpa.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;

/**
 * Created by pvsubrah on 6/10/16.
 */
@RepositoryRestResource(collectionResourceRel = "bibliographic", path = "bibliographic")
public interface BibliographicDetailsRepository extends JpaRepository<BibliographicEntity, BibliographicPK> {


    /**
     * Gets pageable bibliographic entities based on the given last updated date.
     *
     * @param pageable    the pageable
     * @param createdDate the created date
     * @return the page
     */
    Page<BibliographicEntity> findByLastUpdatedDateAfter(Pageable pageable, Date createdDate);

    /**
     * Gets pageable bibliographic entities for a particular institution based on the given owning institution id.
     *
     * @param pageable      the pageable
     * @param institutionId the institution id
     * @param createdDate   the created date
     * @return the page
     */
    Page<BibliographicEntity> findByOwningInstitutionIdAndLastUpdatedDateAfter(Pageable pageable, Integer institutionId, Date createdDate);

    /**
     * Gets count of bibs which are not deleted in scsb for the given institution.
     *
     * @param owningInstitutionId the owning institution id
     * @return the long
     */
    Long countByOwningInstitutionIdAndIsDeletedFalse(Integer owningInstitutionId);

    /**
     * Gets pageable bibliographic entities which are not deleted in scsb for the given institution.
     *
     * @param pageable      the pageable
     * @param institutionId the institution id
     * @return the page
     */
    Page<BibliographicEntity> findByOwningInstitutionIdAndIsDeletedFalse(Pageable pageable, Integer institutionId);

    /**
     *  Gets bibliographic entity which are not deleted in scsb for the given institution.
     *
     * @param owningInstitutionId    the owning institution id
     * @param owningInstitutionBibId the owning institution bib id
     * @return the bibliographic entity
     */
    BibliographicEntity findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(Integer owningInstitutionId, String owningInstitutionBibId);

    /**
     * Gets bibliographic entity based on the given bibliographic id.
     *
     * @param bibliographicId the bibliographic id
     * @return the bibliographic entity
     */
    BibliographicEntity findByBibliographicId(@Param("bibliographicId") Integer bibliographicId);

    /**
     *  Gets bibliographic entity which is not deleted in scsb and with the given cataloging status.
     *
     * @param bibliographicId  the bibliographic id
     * @param catalogingStatus the cataloging status
     * @return the bibliographic entity
     */
    BibliographicEntity findByBibliographicIdAndCatalogingStatusAndIsDeletedFalse(@Param("bibliographicId") Integer bibliographicId, @Param("catalogingStatus") String catalogingStatus);

    /**
     *  Gets list of bibliographic entities based on the given owning institution bib id.
     *
     * @param owningInstitutionBibId the owning institution bib id
     * @return the list
     */
    List<BibliographicEntity> findByOwningInstitutionBibId(String owningInstitutionBibId);

    /**
     * Gets count of bibs which are not deleted in scsb for the given owning institution.
     *
     * @param institutionCode the institution code
     * @return the long
     */
    @Query(value = "select count(*) from bibliographic_t bib, institution_t inst where bib.OWNING_INST_ID = inst.INSTITUTION_ID AND inst.INSTITUTION_CODE=?1 limit 1", nativeQuery = true)
    Long countByOwningInstitutionCodeAndIsDeletedFalse(String institutionCode);

    /**
     * To get the non deleted items in scsb for the given owning institution along with owning institution bib id and cataloging stat
     *
     * @param owningInstitutionId    the owning institution id
     * @param owningInstitutionBibId the owning institution bib id
     * @param catalogingStatus       the cataloging status
     * @return the non deleted item entities
     */
    List<ItemEntity> getNonDeletedItemEntities(@Param("owningInstitutionId") Integer owningInstitutionId, @Param("owningInstitutionBibId") String owningInstitutionBibId, @Param("catalogingStatus") String catalogingStatus);

}