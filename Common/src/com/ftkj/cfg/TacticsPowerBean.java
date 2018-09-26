package com.ftkj.cfg;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.TacticId;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年9月13日
 */
public class TacticsPowerBean extends ExcelBean implements Serializable {
    private static final long serialVersionUID = -3661735570083810251L;
    private TacticId tid;
    private Map<EPlayerPosition, Float> power;

    @Override
    public void initExec(RowData row) {
        int id = row.get("id");
        this.tid = TacticId.convert(id);
        int pg = row.get("pg");
        int sg = row.get("sg");
        int sf = row.get("sf");
        int pf = row.get("pf");
        int c = row.get("c");
        int nul = row.get("nul");
        power = Maps.newHashMap();
        power.put(EPlayerPosition.C, c / 100f);
        power.put(EPlayerPosition.NULL, nul / 100f);
        power.put(EPlayerPosition.PF, pf / 100f);
        power.put(EPlayerPosition.PG, pg / 100f);
        power.put(EPlayerPosition.SF, sf / 100f);
        power.put(EPlayerPosition.SG, sg / 100f);
    }

    public TacticId getTid() {
        return tid;
    }

    public float getPower(EPlayerPosition position) {
        return this.power.getOrDefault(position, 0f);
    }

}
