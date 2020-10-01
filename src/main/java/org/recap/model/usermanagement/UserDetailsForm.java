package org.recap.model.usermanagement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by dharmendrag on 28/12/16.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsForm {
    private Integer loginInstitutionId;
    private boolean superAdmin;
    private boolean recapUser;
    private boolean recapPermissionAllowed;
}
