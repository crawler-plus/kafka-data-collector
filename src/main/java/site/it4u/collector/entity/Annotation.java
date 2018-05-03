package site.it4u.collector.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Annotation {

    private String name;

    private boolean enabled;

    private String datasource;

    private boolean showLine;
}