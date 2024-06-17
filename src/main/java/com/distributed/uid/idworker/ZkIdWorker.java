package com.distributed.uid.idworker;


import com.distributed.uid.manager.ISequenceManager;

/**
 * 依赖zk中心化的发号器
 *
 * @author typhoon
 **/
public class ZkIdWorker extends AbstractIdWorker {

    private String seqKey = "/id-worker-seq-";

    private ISequenceManager iSequenceManager;

    public ZkIdWorker(ISequenceManager iSequenceManager) {
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
