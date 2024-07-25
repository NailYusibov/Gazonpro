package com.gitlab.util;

import com.gitlab.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtils {

    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    public static boolean isAdmin(User user) {
        return user.getRolesSet().toString().contains(ADMIN_ROLE);
    }
}
