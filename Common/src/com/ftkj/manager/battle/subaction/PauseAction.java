package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 暂停行为
 *
 * @author luch
 */
public class PauseAction extends BaseSubAction {
    private static final Logger log = LoggerFactory.getLogger(PauseAction.class);

    public PauseAction(EActionType type) {
        super(type);
    }

    @Override
    public void doAction(SubActionContext ctx) {
        log.info("subact pause. bid {} round {} auto pause. resume {}", ctx.bs().getId(),
                ctx.bs().getRound().getCurRound(), ctx.bs().getRound().getResumeType());
        ctx.bs().getRound().setPause(true);
    }
}
