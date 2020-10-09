package org.recap.repository.jpa;

import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.HoldingsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by hemalathas on 21/6/16.
 */
@RepositoryRestResource(collectionResourceRel = "holdings", path = "holdings")
public interface HoldingsDetailsRepository extends JpaRepository<HoldingsEntity, HoldingsPK> {

    /**
     * To get the holdings entity for the given holdings id.
     *
     * @param holdingsId the holdings id
     * @return the holdings entity
     */
    HoldingsEntity findByHoldingsId(Integer holdingsId);
}
