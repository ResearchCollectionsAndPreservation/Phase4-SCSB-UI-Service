package org.recap.repository.jpa;

import org.recap.model.jpa.LocationEntity;
import org.recap.model.jpa.SCSBProprtiesEntity;
import org.springframework.data.repository.query.Param;


public interface SCSBPropertiesDetailRepository extends BaseRepository<SCSBProprtiesEntity> {

    /**
     *To get the SCSBProperties entity for the given PKey.
     *
     * @param p_key the Parameter Key
     * @return the SCSBProperties entity
     *//*
    SCSBProprtiesEntity findByPKey(String p_key);

    *//**
     *To get the SCSBProperties entity for the given PValue.
     *
     * @param p_value the Property Value
     * @return the SCSBProperties entity *//*

    SCSBProprtiesEntity findByPValue(String p_value);
*/
}
