package com.mycompany.trafficsystem.controller;

import com.mycompany.trafficsystem.database.NodeDatabase;
import com.mycompany.trafficsystem.model.Node;
import com.mycompany.trafficsystem.util.SystemLogUtil;

import java.util.List;

public class NodeController {

    private final NodeDatabase nodeDatabase;

    public NodeController() {
        this.nodeDatabase = new NodeDatabase();
    }

    public List<Node> getAllNodes() {
        return nodeDatabase.getAllNodes();
    }

    public List<Node> searchNodes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllNodes();
        }

        String normalizedKeyword = keyword.trim();

        if (normalizedKeyword.toUpperCase().startsWith("NG")) {
            try {
                int number = Integer.parseInt(normalizedKeyword.substring(2));
                normalizedKeyword = String.valueOf(number);
            } catch (NumberFormatException e) {
                // Nếu nhập sai như NGABC thì giữ nguyên để search bình thường.
            }
        }

        return nodeDatabase.searchNodes(normalizedKeyword);
    }

    public String generateNextNodeId() {
        return nodeDatabase.generateNextNodeId();
    }

    public boolean addNode(String latitudeText, String longitudeText) {
        Double latitude = parseDouble(latitudeText);
        Double longitude = parseDouble(longitudeText);

        if (!isValidLatitude(latitude) || !isValidLongitude(longitude)) {
            return false;
        }

        Node node = new Node();
        node.setNodeId(generateNextNodeId());
        node.setLatitude(latitude);
        node.setLongitude(longitude);

        boolean success = nodeDatabase.insertNode(node);

        if (success) {
            SystemLogUtil.logSuccess("Thêm nút giao", "NODE", node.getNodeId(), null, nodeToLogValue(node));
        } else {
            SystemLogUtil.logFailed("Thêm nút giao", "NODE", node.getNodeId(), null, nodeToLogValue(node));
        }

        return success;
    }

    public boolean updateNode(String nodeId, String latitudeText, String longitudeText) {
        if (nodeId == null || nodeId.trim().isEmpty()) {
            return false;
        }

        Double latitude = parseDouble(latitudeText);
        Double longitude = parseDouble(longitudeText);

        if (!isValidLatitude(latitude) || !isValidLongitude(longitude)) {
            return false;
        }

        String cleanNodeId = nodeId.trim();
        Node oldNode = nodeDatabase.getNodeById(cleanNodeId);

        Node node = new Node();
        node.setNodeId(cleanNodeId);
        node.setLatitude(latitude);
        node.setLongitude(longitude);

        boolean success = nodeDatabase.updateNode(node);

        if (success) {
            SystemLogUtil.logSuccess("Cập nhật nút giao", "NODE", cleanNodeId, nodeToLogValue(oldNode), nodeToLogValue(node));
        } else {
            SystemLogUtil.logFailed("Cập nhật nút giao", "NODE", cleanNodeId, nodeToLogValue(oldNode), nodeToLogValue(node));
        }

        return success;
    }

    public boolean deleteNode(String nodeId) {
        if (nodeId == null || nodeId.trim().isEmpty()) {
            return false;
        }

        String cleanNodeId = nodeId.trim();
        Node oldNode = nodeDatabase.getNodeById(cleanNodeId);
        boolean success = nodeDatabase.softDeleteNode(cleanNodeId);

        if (success) {
            SystemLogUtil.logSuccess("Xóa nút giao", "NODE", cleanNodeId, nodeToLogValue(oldNode), "IS_DELETED=1");
        } else {
            SystemLogUtil.logFailed("Xóa nút giao", "NODE", cleanNodeId, nodeToLogValue(oldNode), "Xóa thất bại");
        }

        return success;
    }

    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isValidLatitude(Double latitude) {
        return latitude != null && latitude >= -90 && latitude <= 90;
    }

    private boolean isValidLongitude(Double longitude) {
        return longitude != null && longitude >= -180 && longitude <= 180;
    }

    private String nodeToLogValue(Node node) {
        if (node == null) {
            return null;
        }

        return "NODE_ID=" + node.getNodeId()
                + ", LATITUDE=" + node.getLatitude()
                + ", LONGITUDE=" + node.getLongitude();
    }
}
