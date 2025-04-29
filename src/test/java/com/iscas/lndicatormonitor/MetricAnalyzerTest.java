//package com.iscas.lndicatormonitor;
//
//import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
//import org.junit.jupiter.api.Test;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.TimeZone;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//public class MetricAnalyzerTest {
//
//    /**
//     * 生成指定格式的日期字符串
//     * @param timestamp 时间戳
//     * @return 格式化后的日期字符串
//     */
//    private String formatTimestamp(long timestamp) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS毫秒");
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//        return sdf.format(new Date(timestamp));
//    }
//
//    /**
//     * 分析指标并生成带有时间区间的文字描述
//     * @param metricName 指标名称
//     * @param data 数据值数组
//     * @param startTime 开始时间戳
//     * @param interval 每个数据点的时间间隔（毫秒）
//     * @return 文字描述
//     */
//    public String analyzeMetric(String metricName, double[] data, long startTime, long interval) {
//        // 使用 DescriptiveStatistics 进行统计分析
//        DescriptiveStatistics stats = new DescriptiveStatistics();
//
//        for (double value : data) {
//            stats.addValue(value);
//        }
//
//        // 计算统计指标
//        double mean = stats.getMean();
//        double max = stats.getMax();
//        double min = stats.getMin();
//        double stdDev = stats.getStandardDeviation();
//        double range = max - min;
//
//        // 计算开始和结束时间
//        long endTime = startTime + (data.length - 1) * interval;
//        String startTimeFormatted = formatTimestamp(startTime);
//        String endTimeFormatted = formatTimestamp(endTime);
//
//        // 生成文字描述
//        StringBuilder report = new StringBuilder();
//        report.append("指标名称：").append(metricName).append("\n");
//        report.append("时间区间：").append(startTimeFormatted).append(" 至 ").append(endTimeFormatted).append("\n");
//        report.append("在这段时间区间内的分析结果如下：\n");
//        report.append("平均值：").append(String.format("%.5f", mean)).append("\n");
//        report.append("最大值：").append(String.format("%.5f", max)).append("\n");
//        report.append("最小值：").append(String.format("%.5f", min)).append("\n");
//        report.append("波动幅度（最大-最小）：").append(String.format("%.5f", range)).append("\n");
//        report.append("标准差：").append(String.format("%.5f", stdDev)).append("\n");
//
//        // 进行波动性分析
//        if (stdDev / mean > 0.1) {
//            report.append("波动分析：该指标波动较大，在此时间区间内存在显著的变化。\n");
//        } else {
//            report.append("波动分析：该指标较为稳定，在此时间区间内变化不大。\n");
//        }
//
//        if (max > mean * 1.5) {
//            report.append("峰值分析：该指标存在较高峰值，最大值显著高于平均值。\n");
//        }
//
//        return report.toString();
//    }
//
//    @Test
//    public void testAnalyzeMetric() {
//        double[] data = {
//                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.003027182, 0.0033991297, 0.0030174248,
//                0.0031951447, 0.0032014267, 0.003118107, 0.003129023, 0.0029829605, 0.0029586686,
//                0.0029168164, 0.0029498902, 0.003008312, 0.0032066496, 0.0029952214, 0.002831358,
//                0.0030119363, 0.002896782, 0.00299247, 0.0031096044, 0.0029889564, 0.0030378406,
//                0.0030289188, 0.0030967346, 0.0030124513, 0.0030326403, 0.0032530604, 0.0029861922,
//                0.0031774112, 0.0029472793, 0.0029496117, 0.0032004516, 0.003047183, 0.0030950317,
//                0.0029010486, 0.0031231907, 0.0059808716, 0.0029578772, 0.0033407602, 0.0029475342,
//                0.0031738477, 0.003115051, 0.0030523539, 0.0031481867, 0.0031910017, 0.0031113846,
//                0.0028695425, 0.0031703678, 0.003049822, 0.0029774634, 0.0031138698, 0.0030414665,
//                0.0030584387, 0.0030818027, 0.0030520048, 0.0031803572, 0.0031397506, 0.0030738027,
//                0.003291061, 0.002974332, 0.0031609521, 0.006115448, 0.0031011216, 0.0032390095,
//                0.0031893197, 0.0029890835, 0.003183045, 0.0030164248, 0.0031981606, 0.0031217537,
//                0.0033350047, 0.00298472, 0.0033786034, 0.0029211259, 0.003303269, 0.0030265294,
//                0.003447047, 0.0030524111, 0.0033953695, 0.0032621722, 0.0032124375, 0.003228813,
//                0.0030612168, 0.0031114102, 0.003072651, 0.0031998442, 0.0029678487, 0.0030454453,
//                0.003002177, 0.0030913542, 0.00320509, 0.0029357197, 0.0029406522
//        };
//
//        // 假设数据开始的时间戳
//        long startTime = 1730376295149L; // 示例时间戳
//        long interval = 1000L; // 每个数据点的时间间隔为 1 秒（1000 毫秒）
//
//        String report = analyzeMetric("cpu_usage", data, startTime, interval);
//        assertNotNull(report);
//        System.out.println(report);
//    }
//}
