package com.stevex.zkdemo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xuhui on 2016/2/16.
 */
public class DeleteCallBack implements BackgroundCallback {

    public final static Logger logger = LoggerFactory.getLogger(DeleteCallBack.class);

    public void processResult(CuratorFramework client, CuratorEvent event)
            throws Exception {
        // TODO Auto-generated method stub
        logger.info(event.getPath() + ",data=" + event.getData());
        logger.info("event type=" + event.getType());
        logger.info("event code="+event.getResultCode());

    }

}
