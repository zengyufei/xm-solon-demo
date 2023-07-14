package com.xunmo.rabbitmq.entity;

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
public class DeadConfig {

	private String changeName;

	private String queueName;

	private String routingKey;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DeadConfig that = (DeadConfig) o;
		return Objects.equals(changeName, that.changeName) && Objects.equals(queueName, that.queueName)
				&& Objects.equals(routingKey, that.routingKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(changeName, queueName, routingKey);
	}

}
