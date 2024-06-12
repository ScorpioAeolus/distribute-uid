package com.distributed.uid.idworker;


import com.distributed.uid.manager.ISequenceManager;

/**
 * 依赖redis中心化的发号器
 *
 * @author typhoon
 * @date 2022-11-17 14:3 Thursday
 **/
public class RedisIdWorker extends AbstractIdWorker {

    private String seqKey = "_id_worker_seq";
    private ISequenceManager iSequenceManager;

    public RedisIdWorker(ISequenceManager iSequenceManager) {
        super();
        this.iSequenceManager = iSequenceManager;
    }

    public String getSeqKey() {
        return this.seqKey;
    }

    public void setSeqKey(String seqKey) {
        this.seqKey = seqKey;
    }

    @Override
    protected long getWorkerId() {
        long seq = this.iSequenceManager.borrowSeq(seqKey);
        return seq % this.workerIdCount;
    }

    @Override
    protected void doDestroyWorkerId() {
        this.iSequenceManager.returnSeq(seqKey);
    }

}
