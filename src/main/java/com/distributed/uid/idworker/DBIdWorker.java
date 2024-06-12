package com.distributed.uid.idworker;


import com.distributed.uid.manager.ISequenceManager;

/**
 * 依赖db中心化的发号器
 *
 * @author typhoon
 * @date 2022-11-17 14:3 Thursday
 **/
public class DBIdWorker extends AbstractIdWorker {

    private long seq;

    private ISequenceManager iSequenceManager;

    public DBIdWorker(ISequenceManager iSequenceManager) {
        super();
        this.iSequenceManager = iSequenceManager;
    }

    @Override
    protected long getWorkerId() {
        this.seq = this.iSequenceManager.borrowSeq(null);
        return (this.seq % this.workerIdCount);
    }

    @Override
    protected void doDestroyWorkerId() {
        this.iSequenceManager.returnSeq(this.seq);
    }

}
