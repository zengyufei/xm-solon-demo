package com.xunmo.core.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors
@Builder(builderMethodName = "of")
@NoArgsConstructor
@AllArgsConstructor
public class XmLogEvent {

	private String msg;

}
