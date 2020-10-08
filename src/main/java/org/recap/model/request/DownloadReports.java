package org.recap.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DownloadReports {
    private byte [] content;
    private  String fileName;
}
