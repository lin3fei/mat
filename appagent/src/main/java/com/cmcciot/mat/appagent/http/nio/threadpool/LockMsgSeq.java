package com.cmcciot.mat.appagent.http.nio.threadpool;

public class LockMsgSeq
{
    /**
     * 消息序列
     */
    private Integer nMsgSeq = 0;
    
    public Integer getnMsgSeq()
    {
        return nMsgSeq;
    }
    
    public void setnMsgSeq(Integer nMsgSeq)
    {
        this.nMsgSeq = nMsgSeq;
    }
    
}
