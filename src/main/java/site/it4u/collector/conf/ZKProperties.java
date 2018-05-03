package site.it4u.collector.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zk")
@Getter
@Setter
public class ZKProperties {

    private String host;

    private int port;
}
