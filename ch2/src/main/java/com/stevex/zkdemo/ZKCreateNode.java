package com.stevex.zkdemo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.acl.Acl;
import java.util.concurrent.CountDownLatch;

/**
 * Created by stevex on 12/29/15.
 */
public class ZKCreateNode implements Watcher {

    public final static Logger logger = LoggerFactory.getLogger(ZkCreateConnection.class);

    public static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String args[]){
        try{
            ZooKeeper zk = new ZooKeeper(Constants.connectionString, Constants.sessionTimeout, new ZKCreateNode());
            latch.await();
            zk.create("/test1", "testdata1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new ZKCallBack(), "I am Context");
            zk.create("/test2", "testdata2".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new ZKCallBack(), "I am Context");
            zk.create("/test3", "testdata3".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, new ZKCallBack(), "I am Context");
            zk.create("/test4", "testdata4".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL, new ZKCallBack(), "I am Context");
            Thread.sleep(10000);
            Stat stat = new Stat();
            System.out.println(new String(zk.getData("/test2", new ZKCreateNode(), stat)));
            System.out.println(stat.getCzxid()+","+stat.getMzxid()+","+stat.getVersion());
        }
        catch(IOException ex){
            logger.error("create node to zk error",ex);
        } catch (InterruptedException e) {

        } catch (KeeperException e) {
            logger.error("create node to zk error", e);
        }
    }

    public void process(WatchedEvent watchedEvent) {
        logger.info("received state:"+watchedEvent.getState());
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            latch.countDown();
        }
    }

}


