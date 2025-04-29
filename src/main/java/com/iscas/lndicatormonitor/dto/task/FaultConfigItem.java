package com.iscas.lndicatormonitor.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Map;

@Data
@AllArgsConstructor
public class FaultConfigItem {
    private String service_name;
    private String file_id;
    private ArrayList<Object> fault_list;
}
