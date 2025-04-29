package com.iscas.lndicatormonitor.dto;

import com.iscas.lndicatormonitor.domain.Address;
import lombok.Data;

import java.util.HashMap;
import java.util.stream.Collectors;

@Data
public class AddressDTO {
    private Integer id;
    private HashMap<String, String> address;
    private Integer faultConfigId;
    public Address toAddress() {
        Address address = new Address();
        address.setId(this.getId());
        address.setFaultConfigId(this.getFaultConfigId());

        // 将HashMap转换为字符串
        String addressStr = this.address.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(", "));
        address.setAddress(addressStr);

        return address;
    }

}
