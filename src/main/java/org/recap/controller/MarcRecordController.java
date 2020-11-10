package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.util.MarcRecordViewUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by rajeshbabuk on 22/7/16.
 */
@RestController
@RequestMapping("/openMarcRecord")
@CrossOrigin
public class MarcRecordController {
    private static final Logger logger = LoggerFactory.getLogger(MarcRecordController.class);
    @Autowired
    private MarcRecordViewUtil marcRecordViewUtil;

    /**
     * Renders the marc record view UI page for the scsb application.
     *
     * @param bibId the bib id
     * @return the string
     */
    @GetMapping("")
    public BibliographicMarcForm openMarcRecord(@RequestParam("bibId") Integer bibId) {
        logger.info("openMarcRecord --> Called");
        BibliographicMarcForm bibliographicMarcForm = marcRecordViewUtil.buildBibliographicMarcForm(bibId, null,null);
        return bibliographicMarcForm;
    }

}
