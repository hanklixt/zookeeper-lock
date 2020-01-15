package lock.hank.zk.lock;

import lock.hank.zk.lock.lock.ZKLockFactory;
import lock.hank.zk.lock.lock.ZookeeperLock;
import lock.hank.zk.lock.pojo.Lock;
import org.junit.jupiter.api.Test;

class LockApplicationTests {

    @Test
    void contextLoads() {
        final ZookeeperLock zkLock = ZKLockFactory.getZkLock();
        final Lock lock = zkLock.lock("test", 3000);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(lock.isActive());

    }

}
