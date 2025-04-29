package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.*;

/**
 * @author Yukun Hou
 * @create 2023-10-10 15:38
 */
public interface PrometheusIndexServicenew {

    Object getPrometheusIndex(PrometheusIndex prometheusIndex) throws Exception;

    Object getNodeIndex(PrometheusIndex prometheusIndex) throws Exception;

}
