package com.voyagrr.common.constant;

public final class ExceptionConstant {

    private ExceptionConstant() {

    }

    public enum RESOURCES {
        FILE,
        DIRECTORY,
        GROUP
    }

    public static final String ENTITY_DOES_NOT_EXISTS = "%s does not exist";

    public static final String ACCESS_DENIED_FOR_RESOURCE = "User does not have permission to %s this %s.";

}
