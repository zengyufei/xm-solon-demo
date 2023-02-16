package com.xunmo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PageParam {
    private Integer pageNo;
    private Integer pageSize;
    private String orderBy;

    public boolean hasPageParam() {
        return null != pageNo && null != pageSize && pageNo > 0 && pageSize > 0;
    }
}
