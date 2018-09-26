package com.ftkj.console;

import com.ftkj.cfg.ModuleBean;
import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.enums.EModuleCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * 模块配置.
 *
 * @author luch
 */
public class ModuleConsole extends AbstractConsole implements ValidateBean {
    private static Map<EModuleCode, ModuleBean> modules = Collections.emptyMap();

    public static void init() {
        Map<EModuleCode, ModuleBean> modules = new EnumMap<>(EModuleCode.class);
        for (ModuleBean.Builder b : CM.modules) {
            if (b.getModule() == null) {
                continue;
            }
            modules.put(b.getModule(), b.build());
        }
        ModuleConsole.modules = modules;
    }

    public static boolean isDisabled(int teamLev, EModuleCode module) {
        ModuleBean b = modules.get(module);
        return b != null && b.getLev() > teamLev;
    }

    public static int getEnableLev(EModuleCode module) {
        ModuleBean b = modules.get(module);
        return b != null ? b.getLev() : 0;
    }

    @Override
    public void validate() {
    }
}
