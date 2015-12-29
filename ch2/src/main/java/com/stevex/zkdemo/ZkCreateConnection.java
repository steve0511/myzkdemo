package com.stevex.zkdemo;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by stevex on 12/29/15.
 */
public class ZkCreateConnection implements Watcher {

    public final static Logger logger = LoggerFactory.getLogger(ZkCreateConnection.class);

    public static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String args[]){
        try{
            ZooKeeper zk = new ZooKeeper(Constants.connectionString, Constants.sessionTimeout, new ZkCreateConnection());
            logger.info("current state:"+zk.getState());
            latch.await();
            logger.info("established:"+zk.getState());
        }
        catch(IOException ex){
            logger.error("create connection to zk error",ex);
        } catch (InterruptedException e) {

        }
    }

    public void process(WatchedEvent watchedEvent) {
        logger.info("received state:"+watchedEvent.getState());
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            latch.countDown();
        }
    }
}
