package com.iscas.lndicatormonitor.entity;

import java.util.List;

public class TaskPage {
    private Integer currentPage;
    private Integer pageSize;
    private long totalOrders;
    private Integer totalPages;
    private List<Task> tasks;

    // 构造函数、getter和setter

    public TaskPage(int currentPage, int pageSize, long totalOrders, int totalPages, List<Task> tasks) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalOrders = totalOrders;
        this.totalPages = totalPages;
        this.tasks = tasks;
    }

    public TaskPage() {
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
