/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sm.base.common;

/**
 * @author ADMIN
 */
public class Constant {
    public final class PATH_FILE {
        public static final String EXTENSION = ".xlsx";
    }

    public final class CONFIG_DB_REPORT {
        public final static String PACKAGE_MODEL_REPORT = "com.sm.base.model.modelReport";
        public final static String UNIT_NAME_ENTITIES_REPORT = "UNIT_REPORT";
        public final static String PACKAGE_REPO_REPORT = "com.sm.base.repo.repoReport";
    }

    public final class EXECUTION_ERROR {
        public final static String SUCCESS = "0";
        public final static String ERROR = "1";
    }

    public final class CONSTANT_REPORT {
        public final static String PROVICE_CODE = "province_code";

        public final static String SQL_PROVICE_CODE = "and a.province_code=:province_code";
    }
}
