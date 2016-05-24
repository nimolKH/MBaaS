package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MbaasUserTable;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/3/16.
 */
public class MBaaSUserLoginValidator extends JooqValidator<String> {

    private String userId;

    public MBaaSUserLoginValidator() {
    }

    public MBaaSUserLoginValidator(String mbaasUserId) {
        this.userId = mbaasUserId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String login = validatable.getValue();
        if (login != null && !"".equals(login)) {
            MbaasUserTable userTable = Tables.MBAAS_USER.as("userTable");
            DSLContext context = getDSLContext();
            int count = 0;
            if (userId == null || "".equals(userId)) {
                count = context.selectCount().from(userTable).where(userTable.LOGIN.eq(login)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(userTable).where(userTable.LOGIN.eq(login)).and(userTable.MBAAS_USER_ID.ne(this.userId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}
