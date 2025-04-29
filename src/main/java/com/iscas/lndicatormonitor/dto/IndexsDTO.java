package com.iscas.lndicatormonitor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Yukun Hou
 * @create 2023-10-12 16:27
 */

@Data
@NoArgsConstructor
public class IndexsDTO {

    private String name;

    private Integer type;

    private Integer faultConfigId;
}
