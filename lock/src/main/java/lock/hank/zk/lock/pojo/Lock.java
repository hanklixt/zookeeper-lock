package lock.hank.zk.lock.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author lxt
 * @date 2020-01-15-14:39
 */
@Getter
@Setter
@NoArgsConstructor
public class Lock {

    private String lockId;
    private String path;
    private boolean active;

    public Lock(String lockId, String path) {
        this.lockId = lockId;
        this.path = path;
    }
}
