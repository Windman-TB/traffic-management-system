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
public class AdminView extends BaseView {

    @Override
    protected String[] getMenuItems() {
        return new String[]{
                "Tổng quan",
                "👥Quản lý nhân viên",
                "Quản lý tài khoản",
                "Nhật ký hệ thống"
        };
    }
    
    @Override
    protected List<DashboardCardInfo> getOverviewCards() {
        return List.of(
                new DashboardCardInfo(
                        "Tổng số nhân viên",
                        String.valueOf(baseController.getTotalEmployees()),
                        "Dữ liệu từ bảng EMPLOYEE",
                        "👥",
                        BLUE
                ),
                new DashboardCardInfo(
                        "Tổng tài khoản",
                        String.valueOf(baseController.getTotalAccounts()),
                        "Dữ liệu từ bảng ACCOUNT",
                        "🔐",
                        GREEN
                )
        );
    }

    @Override
    protected String getOverviewIntro() {
        return "Quản trị viên tập trung vào nhân sự, tài khoản đăng nhập và nhật ký thao tác trong hệ thống.";
    }

    @Override
    protected List<String[]> getOverviewFocusItems() {
        return List.of(
                new String[]{"👥", "Nhân viên", "Theo dõi hồ sơ, trạng thái làm việc và thông tin liên hệ."},
                new String[]{"🔐", "Tài khoản", "Quản lý quyền đăng nhập, vai trò và trạng thái tài khoản."},
                new String[]{"📋", "Nhật ký hệ thống", "Kiểm tra lịch sử thêm, sửa, xóa và các thao tác thất bại."}
        );
    }

    @Override
    protected List<String[]> getOverviewActionItems() {
        return List.of(
                new String[]{"Rà soát tài khoản mới", "Đảm bảo nhân viên được cấp đúng vai trò trước khi sử dụng hệ thống."},
                new String[]{"Kiểm tra trạng thái nhân viên", "Khóa hoặc cập nhật các hồ sơ không còn hoạt động."},
                new String[]{"Theo dõi nhật ký bất thường", "Đọc log để phát hiện thao tác lỗi hoặc dữ liệu bị sửa sai."}
        );
    }

    @Override
    protected List<String> getOverviewWorkflowSteps() {
        return List.of(
                "Tạo hoặc cập nhật hồ sơ nhân viên.",
                "Gán tài khoản và vai trò phù hợp cho nhân viên.",
                "Kiểm tra nhật ký hệ thống sau các thao tác quản trị quan trọng."
        );
    }

}
