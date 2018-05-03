package site.it4u.collector.utils;

import lombok.AllArgsConstructor;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

@AllArgsConstructor
public class ZKUtils {

    private String connectString;

    public CuratorFramework buildCuratorFramework() {
        // retry 10 times, first retry 1s later
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 10);
        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString(connectString)
                .sessionTimeoutMs(50000).retryPolicy(policy).build();
        return curator;
    }
}
