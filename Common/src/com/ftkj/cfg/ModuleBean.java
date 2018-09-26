package com.ftkj.cfg;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.enums.EModuleCode;
import com.ftkj.util.excel.RowData;

/** 模块配置 */
public class ModuleBean {
    /** 模块 */
    private final EModuleCode module;
    /** 开启模块需要的球队等级 */
    private final int lev;

    public ModuleBean(EModuleCode module, int lev) {
        this.module = module;
        this.lev = lev;
    }

    public EModuleCode getModule() {
        return module;
    }

    public int getLev() {
        return lev;
    }

    public static final class Builder extends AbstractExcelBean {
        /** 模块 */
        private EModuleCode module;
        /** 开启模块需要的球队等级 */
        private int lev;

        @Override
        public void initExec(RowData row) {
            module = EModuleCode.convert(getStr(row, "module_"));
            lev = getInt(row, "Level");
        }

        public EModuleCode getModule() {
            return module;
        }

        public int getLev() {
            return lev;
        }

        public ModuleBean build() {
            return new ModuleBean(module, lev);
        }
    }

}
