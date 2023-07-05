package com.xunmo.rabbitmq.entity;

import com.xunmo.rabbitmq.enums.ExchangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder(builderMethodName = "of")
public class MqConfig {

    private String title;
    private String changeName;
    private String queueName;
    private String routingKey;
    private Boolean durable;
    private ExchangeType exchangeType;
    private Long ttl;
    private Long max;
    private DeadConfig deadConfig;

    private Boolean isAutoClose;
    private Boolean isDelay;
    private Long delayTime;

    public String getKey() {
        return changeName + queueName + routingKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MqConfig mqConfig = (MqConfig) o;
        return Objects.equals(changeName, mqConfig.changeName) && Objects.equals(queueName, mqConfig.queueName) && Objects.equals(routingKey, mqConfig.routingKey) && Objects.equals(durable, mqConfig.durable) && exchangeType == mqConfig.exchangeType && Objects.equals(ttl, mqConfig.ttl) && Objects.equals(max, mqConfig.max) && Objects.equals(deadConfig, mqConfig.deadConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(changeName, queueName, routingKey, durable, exchangeType, ttl, max, deadConfig);
    }
}
