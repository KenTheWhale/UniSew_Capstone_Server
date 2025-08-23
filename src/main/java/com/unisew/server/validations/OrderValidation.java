package com.unisew.server.validations;

import com.unisew.server.models.Order;
import com.unisew.server.models.SewingPhase;
import com.unisew.server.repositories.SewingPhaseRepo;
import com.unisew.server.requests.AssignMilestoneRequest;
import com.unisew.server.requests.CreateOrderRequest;
import com.unisew.server.requests.CreateSewingPhaseRequest;
import com.unisew.server.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderValidation {
    public static String validate(CreateOrderRequest request) {
        if (request.getDeliveryId() == null || request.getDeliveryId() <= 0) {
            return "Invalid delivery ID.";
        }
        if (request.getDeadline() == null) {
            return "Deadline cannot be null.";
        }
        if (request.getOrderDetails() == null || request.getOrderDetails().isEmpty()) {
            return "Order details cannot be empty.";
        }
        for (CreateOrderRequest.OrderItem item : request.getOrderDetails()) {
            if (item.getDeliveryItemId() == null || item.getDeliveryItemId() <= 0) {
                return "Invalid delivery item ID in order details.";
            }
            if (item.getSize() == null || item.getSize().isEmpty()) {
                return "Size cannot be null or empty in order details.";
            }
            if (item.getQuantity() <= 0) {
                return "Quantity must be greater than 0 in order details.";
            }
        }
        return null;
    }

    public static String validateCreateSewingPhase(CreateSewingPhaseRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            return "Sewing phase name cannot be null or empty.";
        }
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            return "Sewing phase description cannot be null or empty.";
        }
        return null;
    }

    public static String validateAssignMilestone(Order order, AssignMilestoneRequest request, SewingPhaseRepo sewingPhaseRepo) {
        List<AssignMilestoneRequest.Phase> phases = request.getPhaseList();
        if (phases == null || phases.isEmpty()) return "Phase list is empty";
        if (request.getPhaseList().size() < 3) return "At least 3 phases are required to assign milestones";

        for (AssignMilestoneRequest.Phase p : phases) {
            if (p.getId() == null) return "Phase id is required for every milestone";
            if (p.getStage() <= 0) return "Stage must be a positive integer";
            if (p.getStartDate() == null || p.getEndDate() == null) return "Start date and end date are required";
            if (p.getStartDate().isAfter(p.getEndDate())) return "Start date must be on or before end date";

            SewingPhase phaseEntity = sewingPhaseRepo.findById(p.getId()).orElse(null);
            if (phaseEntity == null) return "Sewing phase not found: " + p.getId();

            if (order.getGarmentId() != null && phaseEntity.getGarment() != null) {
                Integer phaseGarmentId = phaseEntity.getGarment().getId();
                if (phaseGarmentId != null && !phaseGarmentId.equals(order.getGarmentId())) {
                    return "Phase " + phaseEntity.getName() + " does not belong to the garment of this order";
                }
            }
        }

        Set<Integer> stageSeen = new HashSet<>();
        for (AssignMilestoneRequest.Phase p : phases) {
            if (!stageSeen.add(p.getStage())) {
                return "Duplicate stage detected: " + p.getStage();
            }
        }

        List<AssignMilestoneRequest.Phase> sorted = phases.stream()
                .sorted(Comparator.comparingInt(AssignMilestoneRequest.Phase::getStage))
                .toList();

        AssignMilestoneRequest.Phase first = sorted.get(0);
        if (first.getStartDate().isBefore(LocalDate.now())) {
            return "The first milestone must start today or in the future";
        }

        for (int i = 1; i < sorted.size(); i++) {
            LocalDate prevEnd = sorted.get(i - 1).getEndDate();
            LocalDate currStart = sorted.get(i).getStartDate();
            if (currStart.isBefore(prevEnd)) {
                return "Milestone with stage " + sorted.get(i).getStage() +
                        " must start on or after the previous milestone end date (" + prevEnd + ")";
            }
        }

        if (order.getDeadline() != null) {
            LocalDate lastEnd = sorted.get(sorted.size() - 1).getEndDate();
            if (lastEnd.isAfter(order.getDeadline())) {
                return "The last milestone end date must be on or before order deadline (" + order.getDeadline() + ")";
            }
        }

        if (order.getOrderDate() != null) {
            if (first.getStartDate().isBefore(order.getOrderDate())) {
                return "The first milestone cannot start before the order date (" + order.getOrderDate() + ")";
            }
        }

        return null;
    }
}
