package com.xunmo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder(builderMethodName = "of")
public class DeadConfig {

    private String changeName;
    private String queueName;
    private String routingKey;

}
