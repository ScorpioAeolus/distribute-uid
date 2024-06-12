package com.distributed.uid.idworker;

import com.distributed.uid.manager.ISequenceManager;

/**
 * @author typhoon
 * @date 2024/6/12 2:47 下午
 **/
public class LocalMachineIdWorker extends AbstractIdWorker{

    private ISequenceManager iSequenceManager;

    private String applicationName;

    public LocalMachineIdWorker(ISequenceManager iSequenceManager,String applicationName) {
        super();
        this.iSequenceManager = iSequenceManager;
        this.applicationName = applicationName;
    }

    @Override
    protected long getWorkerId() {
        long seq = this.iSequenceManager.borrowSeq(applicationName);
        return seq % this.workerIdCount;
    }

    @Override
    protected void doDestroyWorkerId() {
        this.iSequenceManager.returnSeq(applicationName);
    }
}
