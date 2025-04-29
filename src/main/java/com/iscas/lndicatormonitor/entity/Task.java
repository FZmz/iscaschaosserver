package com.iscas.lndicatormonitor.entity;

import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "task")
public class Task {

    /**
     * MongoDB _id
     */
    @Id
    private String _id;
    /**
     * 接口文档
     */
    private String api_doc;
    private List<Document> fault_config;
    /**
     * 测试任务名
     */
    private List<String> file_list;
    private String name;
    /**
     * 测试请求
     */
    private Document selected_req;

    public Task() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getApi_doc() {
        return api_doc;
    }

    public void setApi_doc(String api_doc) {
        this.api_doc = api_doc;
    }



    public List<String> getFile_list() {
        return file_list;
    }

    public void setFile_list(List<String> file_list) {
        this.file_list = file_list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setFault_config(List<Document> fault_config) {
        this.fault_config = fault_config;
    }

    public void setSelected_req(Document selected_req) {
        this.selected_req = selected_req;
    }

    public List<Document> getFault_config() {
        return fault_config;
    }

    public Document getSelected_req() {
        return selected_req;
    }
}
