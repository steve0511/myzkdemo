package com.stevex.zkdemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xuhui on 2016/2/16.
 */
public class CuratorTest {

    public final static Logger logger = LoggerFactory.getLogger(CuratorTest.class);

    CuratorFramework client = null;

    public static void main(String args[]){
        CuratorTest curatorTest = new CuratorTest();
        curatorTest.init();
        try{
            String path = "/stevetest/ch4";
            curatorTest.addNodeDataWatcher(path);
            curatorTest.addChildWatcher("/stevetest");
            curatorTest.createNode(path, "learning".getBytes());
            curatorTest.readNode(path);
            curatorTest.updateNode(path, "learningupdates".getBytes(), 0);
            curatorTest.deleteNode(path);
            Thread.sleep(10000);
        }
        catch(Exception ex){
            logger.error("failed to connect",ex);
        }
        finally {
            curatorTest.closeClient();
        }
    }

    private void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, Constants.maxRetry);
        client = CuratorFrameworkFactory.builder()
                .connectString(Constants.connectionString)
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy)
                .namespace("base").build();
        client.start();
    }

    private void closeClient() {
        if (client != null)
            this.client.close();
    }

    public void createNode(String path, byte[] data) throws Exception {
        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(path, data);
    }

    public void readNode(String path) throws Exception {
        Stat stat = new Stat();
        byte[] data = client.getData().storingStatIn(stat).forPath(path);
        logger.info("读取节点" + path + "的数据:" + new String(data));
        logger.info(stat.toString());
    }

    public void deleteNode(String path) throws Exception {
        client.delete().guaranteed().deletingChildrenIfNeeded()
                .inBackground(new DeleteCallBack()).forPath(path);

    }

    public void deleteNode(String path, int version) throws Exception {
        client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(version)
                .inBackground(new DeleteCallBack()).forPath(path);
    }

    public void updateNode(String path, byte[] data, int version)
            throws Exception {
        client.setData().withVersion(version).forPath(path, data);
    }

    public void addNodeDataWatcher(String path) throws Exception {
        final NodeCache nodeC = new NodeCache(client, path);
        nodeC.start(true);
        nodeC.getListenable().addListener(new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                String data = new String(nodeC.getCurrentData().getData());
                logger.info("path=" + nodeC.getCurrentData().getPath()
                        + ":data=" + data);
            }
        });
    }

    public void addChildWatcher(String path) throws Exception {
        final PathChildrenCache cache = new PathChildrenCache(this.client,
                path, true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);//ppt中需要讲StartMode
        System.out.println(cache.getCurrentData().size());
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework client,
                                   PathChildrenCacheEvent event) throws Exception {
                if (event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {
                    logger.info("客户端子节点cache初始化数据完成");
                    logger.info("size=" + cache.getCurrentData().size());
                } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    logger.info("添加子节点:" + event.getData().getPath());
                    logger.info("修改子节点数据:" + new String(event.getData().getData()));
                } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                    logger.info("删除子节点:" + event.getData().getPath());
                } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                    logger.info("修改子节点数据:" + event.getData().getPath());
                    logger.info("修改子节点数据:" + new String(event.getData().getData()));
                }
            }
        });
    }



}
