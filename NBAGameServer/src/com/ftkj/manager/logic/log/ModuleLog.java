package com.ftkj.manager.logic.log;

import com.ftkj.enums.EModuleCode;

public class ModuleLog {

    private EModuleCode code;
    /**
     * 模块明细说明
     */
    private String detail;

    private ModuleLog(EModuleCode code, String detail) {
        super();
        this.code = code;
        this.detail = detail;
    }

    /**
     * 创建模块日志
     *
     * @param code
     * @param detail
     * @return
     */
    public static ModuleLog getModuleLog(EModuleCode code, String detail) {
        return new ModuleLog(code, detail == null || detail.equals("") ? "null" : detail);
    }

    public int getId() {
        return this.code.getId();
    }

    public String getDetail() {
        if (this.detail == null || this.detail.equals("")) {
            return "null";
        }
        return this.detail;
    }

    public String getName() {
        return this.code.getName();
    }
}
