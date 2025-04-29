package com.iscas.lndicatormonitor.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@AllArgsConstructor
public class TaskSpec {
    private String id;
    private String createTime;
    private String creator;
    private List<FaultConfigItem> fault_config;
    private List<String> file_list;
    private Object linkGraphData;
    private String name;
    private String namespace;
    private Object selected_req;
    private Object systemGraphData;
    private String testnamespace;
    private ObjectId apiDoc;
}