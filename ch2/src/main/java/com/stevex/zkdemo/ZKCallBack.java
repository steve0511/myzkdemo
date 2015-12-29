package com.stevex.zkdemo;

import org.apache.zookeeper.AsyncCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by stevex on 12/29/15.
 */
public class ZKCallBack implements AsyncCallback.StringCallback {

    public final static Logger logger = LoggerFactory.getLogger(ZKCallBack.class);

    public void processResult(int rc, String path, Object ctx, String name) {
         logger.info("create path result: rc="+rc+",path="+path+",ctx="+ctx+",real path name:"+name);
    }
}
