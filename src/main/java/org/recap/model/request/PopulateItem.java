package org.recap.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PopulateItem {
    String itemTitle;
    String itemOwningInstitution;
    List<String> requestTypes;
    boolean showEDD;
    boolean multipleBarcodes;
    String notAvailableErrorMessage;
    List<DeliveryLocations> deliveryLocation;
}

