package com.ftkj.console;

import com.ftkj.cfg.ItemConvertBean;
import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.db.domain.PropPO;
import com.ftkj.db.domain.bean.PropBeanVO;
import com.ftkj.enums.EPropType;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.bean.PropAutoBoxBean;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.manager.prop.bean.PropBoxBean;
import com.ftkj.manager.prop.bean.PropCoachBean;
import com.ftkj.manager.prop.bean.PropExtPlayerBean;
import com.ftkj.manager.prop.bean.PropMoneyBean;
import com.ftkj.manager.prop.bean.PropPKBean;
import com.ftkj.manager.prop.bean.PropPlayerBean;
import com.ftkj.manager.prop.bean.PropPlayerGradeBean;
import com.ftkj.manager.prop.bean.PropSimpleBean;
import com.ftkj.manager.prop.bean.PropWrapPlayerBean;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年3月21日
 */
public class PropConsole extends AbstractConsole implements ValidateBean {
    private static Map<Integer, PropBean> propMap;
    /**
     * 道具兑换，合成的配置
     */
    private static Map<Integer, ItemConvertBean> convertMap;

    public static void init(List<PropBeanVO> list, Collection<PlayerBean> collection, List<PropBeanVO> list2, List<ItemConvertBean> convertList) {
        Map<Integer, PropBean> propMap = Maps.newHashMap();
        list.forEach(po -> propMap.put(po.getPropId(), instantPropBean(po)));
        list2.forEach(po -> propMap.put(po.getPropId(), instantPropBean(po)));
        initPlayerProp(propMap, collection);
        //
        convertMap = convertList.stream().collect(Collectors.toMap(ItemConvertBean::getcId, (bean) -> bean));
        PropConsole.propMap = propMap;
    }

    private static void initPlayerProp(Map<Integer, PropBean> propMap, Collection<PlayerBean> collection) {
        collection.stream()
            .map(player -> new PropPlayerBean(player.getPlayerRid(), player.getName()))
            .forEach(prop -> propMap.put(prop.getPropId(), prop));
    }

    public static void initAbility() {
        propMap.values().forEach(pb -> pb.initAbility(pb.getConfig()));
    }

    private static PropBean instantPropBean(PropBeanVO po) {
        EPropType type = EPropType.getEPropType(po.getPropType());
        if (EPropType.Common == type) {
            return new PropSimpleBean(po);
        } else if (EPropType.Money == type) {
            return new PropMoneyBean(po);
        } else if (EPropType.Player == type) {
            throw new RuntimeException(String.format("道具 %s 类型 %s 不能为常规球员", po.getPropId(), po.getPropType()));
        } else if (EPropType.Package == type) {
            return new PropBoxBean(po);
        } else if (EPropType.Package_Random == type) {
            return new PropAutoBoxBean(po);
        } else if (EPropType.PK == type) {
            return new PropPKBean(po);
        } else if (EPropType.PlayerGrade == type || EPropType.PlayerBasePrice == type) {
            return new PropPlayerGradeBean(po);
        } else if (EPropType.Coach == type) {
            return new PropCoachBean(po);
        } else if (EPropType.Wrap_Player == type) {
            return new PropWrapPlayerBean(po);
        } else {
            return new PropSimpleBean(po);
        }
    }

    /**
     * 是否是货币类型道具
     *
     * @param prop
     * @return
     */
    public static boolean isMoney(int propId) {
        PropBean bean = PropConsole.getProp(propId);
        return bean != null && bean.getType() == EPropType.Money;
    }

    @SuppressWarnings("unchecked")
    public static <T extends PropBean> T getProp(int propId) {
        PropBean s = propMap.get(propId);
        return s != null ? (T) s : null;
    }

    /** 转换道具为球员道具 */
    public static PropExtPlayerBean getPlayerProp(int propId) {
        return getPlayerProp(propMap.get(propId));
    }

    /** 转换道具为球员道具 */
    public static PropExtPlayerBean getPlayerProp(PropBean pb) {
        if (pb.getType() == EPropType.Wrap_Player) {
            PropWrapPlayerBean wpb = (PropWrapPlayerBean) pb;
            PropPlayerBean prb = PropConsole.getProp(wpb.getPlayerPropRid());
            return new PropExtPlayerBean(prb, wpb.isBind());
        } else if (pb.getType() == EPropType.Player) {
            return new PropExtPlayerBean((PropPlayerBean) pb, false);
        }
        return null;
    }

    public static boolean checkIsProp(int propId) {
        return propMap.containsKey(propId);
    }

    public static ItemConvertBean getConvertBean(int cid) {
        return convertMap.get(cid);
    }

    public static List<ItemConvertBean> getConverViewItems(int view) {
        return convertMap.values().stream().filter(s -> s.getView() == view).collect(Collectors.toList());
    }

    public static Map<Integer, PropBean> getPropMap() {
        return propMap;
    }

    public static Map<Integer, ItemConvertBean> getConvertMap() {
        return convertMap;
    }

    public static String getPropStr(List<PropPO> props) {
        if (props == null || props.size() <= 0) {
            return "";
        }
        String result = props.stream()
            .map(prop -> prop.getPropId() + "=" + prop.getNum())
            .collect(Collectors.joining(","));
        return result;
    }

    @Override
    public void validate() {
        for (PropBean pb : propMap.values()) {
            validate(pb);
        }
    }

    private void validate(PropBean pb) {
        int id = pb.getPropId();
        if (pb.getType() == EPropType.Wrap_Player) {
            PropWrapPlayerBean wpb = (PropWrapPlayerBean) pb;
            if (propMap.get(wpb.getPlayerPropRid()) == null) {
                throw exception("道具 %s 类型为包装球员. 但是球员 %s 不存在", id, wpb.getPlayerPropRid());
            }
        }
    }

    public static void validate(List<PropSimple> props, String msg, Object... msgArgs) {
        for (PropSimple ps : props) {
            validate(ps, msg, msgArgs);
        }
    }

    public static void validate(PropSimple prop, String msg, Object... msgArgs) {
        if (getProp(prop.getPropId()) == null) {
            String preMsg = String.format(msg, msgArgs);
            throw exception(String.format(preMsg + "道具 %s 没有配置", prop.getPropId()));
        }
    }
}
