package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CorootDataDTO {
    private Context context;
    private Data data;

    // Getters and Setters
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Context {
        private Status status;
        private Search search;

        // Getters and Setters
        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public Search getSearch() {
            return search;
        }

        public void setSearch(Search search) {
            this.search = search;
        }
    }

    public static class Status {
        private String status;
        private String error;
        private Prometheus prometheus;
        private NodeAgent nodeAgent;
        private KubeStateMetrics kubeStateMetrics;

        // Getters and Setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Prometheus getPrometheus() {
            return prometheus;
        }

        public void setPrometheus(Prometheus prometheus) {
            this.prometheus = prometheus;
        }

        public NodeAgent getNodeAgent() {
            return nodeAgent;
        }

        public void setNodeAgent(NodeAgent nodeAgent) {
            this.nodeAgent = nodeAgent;
        }

        public KubeStateMetrics getKubeStateMetrics() {
            return kubeStateMetrics;
        }

        public void setKubeStateMetrics(KubeStateMetrics kubeStateMetrics) {
            this.kubeStateMetrics = kubeStateMetrics;
        }
    }

    public static class Prometheus {
        private String status;
        private String message;
        private String error;
        private String action;

        // Getters and Setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }

    public static class NodeAgent {
        private String status;
        private int nodes;

        // Getters and Setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getNodes() {
            return nodes;
        }

        public void setNodes(int nodes) {
            this.nodes = nodes;
        }
    }

    public static class KubeStateMetrics {
        private String status;
        private int applications;

        // Getters and Setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getApplications() {
            return applications;
        }

        public void setApplications(int applications) {
            this.applications = applications;
        }
    }

    public static class Search {
        private List<Application> applications;
        private List<Node> nodes;

        // Getters and Setters
        public List<Application> getApplications() {
            return applications;
        }

        public void setApplications(List<Application> applications) {
            this.applications = applications;
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public void setNodes(List<Node> nodes) {
            this.nodes = nodes;
        }
    }

    public static class Application {
        private String id;
        private String status;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class Node {
        private String name;
        private String status;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class Data {
        private Traces traces;

        // Getters and Setters
        public Traces getTraces() {
            return traces;
        }

        public void setTraces(Traces traces) {
            this.traces = traces;
        }
    }

    public static class Traces {
        private Heatmap heatmap;
        private Summary summary;

        // Getters and Setters
        public Heatmap getHeatmap() {
            return heatmap;
        }

        public void setHeatmap(Heatmap heatmap) {
            this.heatmap = heatmap;
        }

        public Summary getSummary() {
            return summary;
        }

        public void setSummary(Summary summary) {
            this.summary = summary;
        }
    }

    public static class Heatmap {
        private Ctx ctx;
        private String title;
        private List<Series> series;

        // Getters and Setters
        public Ctx getCtx() {
            return ctx;
        }

        public void setCtx(Ctx ctx) {
            this.ctx = ctx;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Series> getSeries() {
            return series;
        }

        public void setSeries(List<Series> series) {
            this.series = series;
        }
    }

    public static class Ctx {
        private long from;
        private long to;
        private int step;

        // Getters and Setters
        public long getFrom() {
            return from;
        }

        public void setFrom(long from) {
            this.from = from;
        }

        public long getTo() {
            return to;
        }

        public void setTo(long to) {
            this.to = to;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }
    }

    public static class Series {
        private String name;
        private String title;
        private List<Float> data;
        private String value;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Float> getData() {
            return data;
        }

        public void setData(List<Float> data) {
            this.data = data;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Summary {
        private List<Stats> stats;
        private Overall overall;

        // Getters and Setters
        public List<Stats> getStats() {
            return stats;
        }

        public void setStats(List<Stats> stats) {
            this.stats = stats;
        }

        public Overall getOverall() {
            return overall;
        }

        public void setOverall(Overall overall) {
            this.overall = overall;
        }
        public static class Overall {
            private String serviceName;
            private String spanName;
            private double total;
            private double failed;
            private List<Double> durationQuantiles;

            // Getters and Setters
        }
    }

    public static class Stats {
        private String serviceName;
        private String spanName;
        private float total;
        private float failed;
        private List<Float> durationQuantiles;

        // Getters and Setters
        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getSpanName() {
            return spanName;
        }

        public void setSpanName(String spanName) {
            this.spanName = spanName;
        }

        public float getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public float getFailed() {
            return failed;
        }

        public void setFailed(float failed) {
            this.failed = failed;
        }
    }
}