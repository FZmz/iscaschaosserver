package com.iscas.lndicatormonitor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yukun Hou
 * @create 2023-10-11 15:17
 */

@Data
@NoArgsConstructor
public class IndexesArrDTO {
    private String[] systemIndexes;
    private String[] pressIndexes;
}
