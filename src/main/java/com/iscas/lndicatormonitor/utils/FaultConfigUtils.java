package com.iscas.lndicatormonitor.utils;

import com.iscas.lndicatormonitor.domain.Faultconfig;
import com.iscas.lndicatormonitor.domain.Faultinnernode;
import com.iscas.lndicatormonitor.dto.FaultConfigBasicInfo;
import com.iscas.lndicatormonitor.dto.FaultConfigNodes;
import com.iscas.lndicatormonitor.dto.IndexesArrDTO;
import com.iscas.lndicatormonitor.dto.IndexsDTO;
import com.iscas.lndicatormonitor.processor.IndexRecommend.coroot.CorootApplicationProcessor;
import com.iscas.lndicatormonitor.service.FaultconfigService;
import com.iscas.lndicatormonitor.service.FaultinnernodeService;
import com.iscas.lndicatormonitor.service.ObservedIndexService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FaultConfigUtils {
    @Autowired
    FaultconfigService faultconfigService;
    @Autowired
    ObservedIndexService observedIndexService;
    @Autowired
    FaultinnernodeService faultinnernodeService;

    public Integer addFaultConfig(FaultConfigBasicInfo basicInfo){
        Faultconfig faultconfig = new Faultconfig();

        BeanUtils.copyProperties(basicInfo,faultconfig);
        faultconfig.setNodeTag("fault-" + UUID.randomUUID().toString().substring(0, 8));
        // 异常如果不进行处理，程序就会立即终止运行，并打印出错误信息
        try {
            faultconfigService.insert(faultconfig);
        }catch (NullPointerException e){
            return 0;
        }
        return faultconfig.getId();
    }
    public void addObsevedindexes(IndexesArrDTO indexesArrDTO,int faultConfigId){
        if (indexesArrDTO.getSystemIndexes() != null) {
            for (String index : indexesArrDTO.getSystemIndexes()) {
                IndexsDTO indexsDTO = new IndexsDTO();
                indexsDTO.setName(index);
                indexsDTO.setType(1);
                indexsDTO.setFaultConfigId(faultConfigId);
                observedIndexService.insert(indexsDTO);
            }
        }
        if (indexesArrDTO.getPressIndexes() != null) {
            for (String index : indexesArrDTO.getPressIndexes()) {
                IndexsDTO indexsDTO = new IndexsDTO();
                indexsDTO.setName(index);
                indexsDTO.setType(2);
                indexsDTO.setFaultConfigId(faultConfigId);
                observedIndexService.insert(indexsDTO);
            }
        }
    }
    public void addFaultinnernode(List<FaultConfigNodes> faultConfigNodesList,int faultConfigId){
        for (FaultConfigNodes faultConfigNodes : faultConfigNodesList){
            Faultinnernode faultinnernode = new Faultinnernode();
            faultinnernode.setFaultConfigId(faultConfigId);
            faultinnernode.setNodeStatus(0);
            BeanUtils.copyProperties(faultConfigNodes,faultinnernode);
            faultinnernodeService.insert(faultinnernode);
        }
    }
}
