package com.iscas.lndicatormonitor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yukun Hou
 * @create 2023-10-11 15:19
 */

@Data
@NoArgsConstructor
public class FaultConfigNodes {
    private Integer nodeIndex;
    private String nodeType;
    private String name;
    private String content;
}