package com.typ.bean;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

// 组合注解，包含@ToString、@EqualsAndHashCode、@Getter、@Setter、@RequiredArgsConstructor、@NoArgsConstructor
@Data
// 通过Build构建对象，但是如果重新赋值的话会重新构建对象，不会提供setter方法，需配合Data注解使用
@Builder
// 标准化输出字符串
@ToString
public class ConnectPojo {
    private String host;
    private Integer port;
    private String userName;
    private String password;
    private Integer protocol;
}
