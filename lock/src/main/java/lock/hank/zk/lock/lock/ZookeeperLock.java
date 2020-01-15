package lock.hank.zk.lock.lock;

import lock.hank.zk.lock.pojo.Lock;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lxt
 * @date 2020-01-15-14:49
 */
public class ZookeeperLock {

    private ZkClient zkClient;

    public ZookeeperLock() {
        zkClient = new ZkClient("106.15.194.121:2181", 3000, 20000);
    }

    // 连接zk服务端
    //创建临时序号节点:必须是在持久节点下创建临时节点

    //1.获得锁
    public Lock lock(String lockId, long timeout) {
        Lock lockNode = createLockNode(lockId);
        lockNode = tryActiveLock(lockNode);
        if (!lockNode.isActive()) {
            try {
                synchronized (lockNode) {
                    //挂起线程,等待
                    lockNode.wait(timeout);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("get lock fail");
            }
        }
        return lockNode;
    }

    //2.激活锁
    private Lock tryActiveLock(Lock lockNode) {
        //1.判断是否获得锁
        //获取全部子节点
        final List<String> children = zkClient.getChildren("/lock");
        final List<String> nodePaths = children
                .stream()
                .sorted()
                .map(x -> "/lock/" + x)
                .collect(Collectors.toList());
        final String firstPath = nodePaths.get(0);
        if (lockNode.getPath().equals(firstPath)) {
            lockNode.setActive(true);
            return lockNode;
        } else {
            //取出上一个节点路径
            final String lastPath = nodePaths.get(nodePaths.indexOf(lockNode.getPath()) - 1);
            //监听上一个节点
            zkClient.subscribeDataChanges(lastPath, new IZkDataListener() {
                @Override
                public void handleDataChange(String s, Object o) throws Exception {

                }

                @Override
                public void handleDataDeleted(String dataPath) throws Exception {
                    System.out.println("节点删除" + dataPath);
                    final Lock lock = tryActiveLock(lockNode);
                    synchronized (lockNode) {
                        if (lock.isActive()) {
                            //唤醒线程
                            lockNode.notify();

                        }
                    }
                    //取消监听
                    zkClient.unsubscribeDataChanges(lastPath, this);
                }
            });


        }

        //2.添加上一个节点的监听
        //3.如果失败再次尝试获取锁

        return lockNode;
    }

    //3.释放锁
    public void unLock(Lock lock) {
        //删除节点
        if (lock.isActive()) {
            zkClient.delete(lock.getPath());
        }

    }

    /**
     * 创建临时节点
     *
     * @param lockId
     * @return
     */
    private Lock createLockNode(String lockId) {
        //创建临时节点，标记为读写锁
        final String path = zkClient.createEphemeralSequential("/lock/" + lockId, "rw");
        final List<String> children = zkClient.getChildren("/lock");
        System.out.println("子节点:" + children);
        return new Lock(lockId, path);
    }


}
