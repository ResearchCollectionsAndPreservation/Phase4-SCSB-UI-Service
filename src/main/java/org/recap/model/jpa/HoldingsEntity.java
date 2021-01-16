package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by pvsubrah on 6/11/16.
 */
@Getter
@Setter
@Entity
@Table(name = "holdings_t", schema = "recap", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "HOLDINGS_ID"))
        @NamedNativeQuery(
                name = "HoldingsEntity.getNonDeletedItemEntities",
                query = "SELECT ITEM_T.* FROM ITEM_T, ITEM_HOLDINGS_T, HOLDINGS_T WHERE " +
                        "HOLDINGS_T.HOLDINGS_ID = ITEM_HOLDINGS_T.HOLDINGS_ID AND ITEM_T.ITEM_ID = ITEM_HOLDINGS_T.ITEM_ID " +
                        "AND ITEM_T.IS_DELETED = 0 AND ITEM_T.CATALOGING_STATUS = :catalogingStatus AND " +
                        "HOLDINGS_T.OWNING_INST_HOLDINGS_ID = :owningInstitutionHoldingsId AND HOLDINGS_T.OWNING_INST_ID = :owningInstitutionId",
                resultClass = ItemEntity.class)
public class HoldingsEntity extends HoldingsAbstractEntity {


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "OWNING_INST_ID", insertable = false, updatable = false)
    private InstitutionEntity institutionEntity;

    @ManyToMany(mappedBy = "holdingsEntities")
    private List<BibliographicEntity> bibliographicEntities;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "item_holdings_t", joinColumns = {
            @JoinColumn(name = "HOLDINGS_ID", referencedColumnName = "HOLDINGS_ID")},
            inverseJoinColumns = {
                    @JoinColumn(name="ITEM_ID", referencedColumnName = "ITEM_ID")})
    private List<ItemEntity> itemEntities;

    /**
     * Instantiates a new Holdings entity object.
     */
    public HoldingsEntity() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        HoldingsEntity holdingsEntity = (HoldingsEntity) o;

        return getOwningInstitutionHoldingsId().equals(holdingsEntity.getOwningInstitutionHoldingsId());

    }

    @Override
    public int hashCode() {
        return getOwningInstitutionHoldingsId().hashCode();
    }
}
