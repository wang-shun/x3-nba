package com.ftkj.cfg.battle;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.util.StringUtil;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.Objects;

/** 配置分组 */
public final class GroupBean implements Serializable {
    private static final long serialVersionUID = -5751838789049792569L;
    /** 配置分组id */
    private final int id;
    /** id列表 */
    private final ImmutableList<Integer> elements;

    public GroupBean(int id, ImmutableList<Integer> elements) {
        this.id = id;
        this.elements = elements;
    }

    public int getId() {
        return id;
    }

    public ImmutableList<Integer> getElements() {
        return elements;
    }

    public int size() {
        return elements.size();
    }

    public static abstract class Builder extends AbstractExcelBean {
        /** 配置分组id */
        private int groupId;
        /** 配置id列表 */
        private ImmutableList<Integer> elements;

        public int getGroupId() {
            return groupId;
        }

        @Override
        public int hashCode() {

            return Objects.hash(groupId, elements);
        }

        @Override
        public void initExec(RowData row) {
            elements = ImmutableList.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, getColName())));
        }

        public abstract String getColName();

        public GroupBean build() {
            return new GroupBean(groupId, elements);
        }
    }
}
