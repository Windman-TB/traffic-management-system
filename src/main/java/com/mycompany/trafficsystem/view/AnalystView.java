/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.view;

import java.util.List;

/**
 *
 * @author engineer
 */
public class AnalystView extends BaseView {

    @Override
    protected String[] getMenuItems() {
        return new String[]{
                "Tổng quan",
                "Giám sát giao thông",
                "📄Phân tích dữ liệu"
        };
    }
    
    @Override
    protected List<DashboardCardInfo> getOverviewCards() {
        return List.of(
                new DashboardCardInfo(
                        "Tổng lưu lượng",
                        String.valueOf(baseController.getTotalTrafficVolume()),
                        "Dữ liệu thống kê lưu lượng giao thông",
                        "📊",
                        ORANGE
                )
        );
    }

    @Override
    protected String getOverviewIntro() {
        return "Phân tích viên theo dõi tình hình giao thông và khai thác dữ liệu lưu lượng để phục vụ đánh giá.";
    }

    @Override
    protected List<String[]> getOverviewFocusItems() {
        return List.of(
                new String[]{"📡", "Giám sát giao thông", "Quan sát dữ liệu lưu lượng và trạng thái các đoạn đường."},
                new String[]{"📄", "Phân tích dữ liệu", "Lọc, thống kê và so sánh lưu lượng theo khu vực hoặc thời điểm."},
                new String[]{"📊", "Báo cáo lưu lượng", "Dùng dữ liệu tổng hợp để nhận diện đoạn đường cần chú ý."}
        );
    }

    @Override
    protected List<String[]> getOverviewActionItems() {
        return List.of(
                new String[]{"Theo dõi điểm có lưu lượng cao", "Ưu tiên kiểm tra các đoạn có biến động lớn hoặc mật độ cao."},
                new String[]{"Lọc dữ liệu trước khi phân tích", "Chọn khu vực, đoạn đường hoặc khoảng thời gian để kết quả rõ hơn."},
                new String[]{"Đối chiếu số liệu tổng", "So sánh tổng bản ghi lưu lượng với kết quả phân tích chi tiết."}
        );
    }

    @Override
    protected List<String> getOverviewWorkflowSteps() {
        return List.of(
                "Mở giám sát để xem trạng thái lưu lượng hiện có.",
                "Chuyển sang phân tích dữ liệu và chọn bộ lọc phù hợp.",
                "Ghi nhận các đoạn đường bất thường để báo lại bộ phận kỹ thuật."
        );
    }
}
