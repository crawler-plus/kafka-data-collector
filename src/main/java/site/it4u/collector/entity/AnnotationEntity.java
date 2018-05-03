package site.it4u.collector.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationEntity {

    private Annotation annotation;

    private Long time;

    private String title;
}
