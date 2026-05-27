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
public class TechnicianView extends BaseView {

    @Override
    protected String[] getMenuItems() {
        return new String[]{
                "Tổng quan",
                "📍Quản lý khu vực",
                "🔗Quản lý tuyến đường",
                "Quản lý đoạn đường",
                "Quản lý nút giao",
                "Quản lý lưu lượng"
        };
    }
    
    @Override
    protected List<DashboardCardInfo> getOverviewCards() {
        return List.of(
                new DashboardCardInfo(
                        "Tổng khu vực",
                        String.valueOf(baseController.getTotalAreas()),
                        "Dữ liệu từ bảng AREA",
                        "📍",
                        BLUE
                ),
                new DashboardCardInfo(
                        "Tổng tuyến đường",
                        String.valueOf(baseController.getTotalStreets()),
                        "Dữ liệu từ bảng STREET",
                        "🛣",
                        GREEN
                ),
                new DashboardCardInfo(
                        "Tổng đoạn đường",
                        String.valueOf(baseController.getTotalRoadSegments()),
                        "Dữ liệu từ bảng SEGMENT",
                        "🔗",
                        ORANGE
                ),
                new DashboardCardInfo(
                        "Tổng nút giao",
                        String.valueOf(baseController.getTotalNodes()),
                        "Dữ liệu từ bảng NODE",
                        "🚦",
                        "#6366F1"
                ),
                new DashboardCardInfo(
                        "Tổng dữ liệu lưu lượng",
                        String.valueOf(baseController.getTotalTrafficVolume()),
                        "Dữ liệu từ bảng TRAFFIC",
                        "📊",
                        RED
                )
        );
    }

    @Override
    protected String getOverviewIntro() {
        return "Kỹ thuật viên quản lý dữ liệu hạ tầng giao thông: khu vực, tuyến, đoạn đường, nút giao và lưu lượng.";
    }

    @Override
    protected List<String[]> getOverviewFocusItems() {
        return List.of(
                new String[]{"📍", "Khu vực", "Phân chia phạm vi quản lý cho toàn bộ dữ liệu giao thông."},
                new String[]{"🛣", "Tuyến và đoạn đường", "Cập nhật tuyến, đoạn đường và liên kết trong mạng lưới."},
                new String[]{"🚦", "Nút giao và lưu lượng", "Theo dõi điểm giao cắt và dữ liệu lưu lượng theo đoạn."}
        );
    }

    @Override
    protected List<String[]> getOverviewActionItems() {
        return List.of(
                new String[]{"Giữ dữ liệu hạ tầng đồng bộ", "Khu vực, tuyến, đoạn và nút giao cần khớp nhau trước khi nhập lưu lượng."},
                new String[]{"Bổ sung điểm còn thiếu", "Thêm nút giao hoặc đoạn đường mới khi hệ thống mở rộng phạm vi."},
                new String[]{"Kiểm tra dữ liệu lưu lượng", "Đối chiếu số bản ghi lưu lượng sau khi cập nhật hạ tầng."}
        );
    }

    @Override
    protected List<String> getOverviewWorkflowSteps() {
        return List.of(
                "Cập nhật khu vực và tuyến đường trước.",
                "Khai báo đoạn đường, nút giao liên quan.",
                "Nhập hoặc chỉnh dữ liệu lưu lượng cho các đoạn đã hoàn chỉnh."
        );
    }

}
