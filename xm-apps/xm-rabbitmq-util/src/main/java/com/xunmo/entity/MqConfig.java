package com.xunmo.entity;

import com.xunmo.enums.ExchangeType;
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
public class MqConfig {

    private String changeName;
    private String queueName;
    private String routingKey;
    private Boolean durable;
    private ExchangeType exchangeType;
    private Long delayTime;
    private Long ttl;
    private Long max;
    private Boolean isDelay;
    private Boolean isAutoClose;

    private DeadConfig deadConfig;

}
