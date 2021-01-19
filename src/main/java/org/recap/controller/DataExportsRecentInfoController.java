package org.recap.controller;

import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.dataexportinfo.S3RecentDataExportInfoList;
import org.recap.util.HelperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class DataExportsRecentInfoController {

    private static final Logger logger = LoggerFactory.getLogger(DataExportsRecentInfoController.class);

    @Value("${scsb.etl.url}")
    private String scsbEtlUrl;

    @GetMapping("/getRecentDataExportsInfo")
    public S3RecentDataExportInfoList getRecentDataExportsInfo() {
        S3RecentDataExportInfoList s3RecentDataExportInfoList = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = HelperUtil.getSwaggerHeaders();
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<S3RecentDataExportInfoList> responseEntity = restTemplate.exchange(scsbEtlUrl + RecapConstants.SCSB_DATA_EXPORT_RECENT_INFO_URL, HttpMethod.GET, httpEntity, S3RecentDataExportInfoList.class);
            if (responseEntity.getBody() != null && responseEntity.getStatusCode().is2xxSuccessful()) {
                s3RecentDataExportInfoList = responseEntity.getBody();
            }
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR, e);
        }
        return s3RecentDataExportInfoList;
    }
}
