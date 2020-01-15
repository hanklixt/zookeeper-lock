package lock.hank.zk.lock.lock;

/**
 * @author lxt
 * @date 2020-01-15-17:08
 */
public class ZKLockFactory {

    private ZookeeperLock zk;

    public static ZookeeperLock getZkLock() {
        return new ZookeeperLock();
    }
}
