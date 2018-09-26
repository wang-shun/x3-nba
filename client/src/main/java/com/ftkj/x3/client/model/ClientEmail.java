package com.ftkj.x3.client.model;

import com.ftkj.db.domain.EmailPO;
import com.ftkj.proto.PropPB.PropSimpleData;

import java.util.List;

/**
 * @author luch
 */
public class ClientEmail extends EmailPO {
    private List<PropSimpleData> props;

    public List<PropSimpleData> getProps() {
        return props;
    }

    public void setProps(List<PropSimpleData> props) {
        this.props = props;
    }

    public boolean unRead() {
        return getStatus() == 0;
    }

    public boolean isRead() {
        return getStatus() == 1;
    }

    public void read() {
        setStatus(1);
    }

    public boolean isDel() {
        return getStatus() == 2;
    }

    @Override
    public void save() {
    }

    @Override
    public void del() {
    }

    public boolean hasProp() {
        return props != null && props.size() > 0;
    }
}
