package com.unisew.server.services.implementors;

import com.unisew.server.enums.Status;
import com.unisew.server.models.*;
import com.unisew.server.repositories.*;
import com.unisew.server.requests.ApproveReportRequest;
import com.unisew.server.requests.GiveFeedbackRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.FeedbackService;
import com.unisew.server.services.JWTService;
import com.unisew.server.utils.CookieUtil;
import com.unisew.server.utils.EntityResponseBuilder;
import com.unisew.server.utils.ResponseBuilder;
import com.unisew.server.validations.FeedbackValidation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackServiceImpl implements FeedbackService {

    FeedbackRepo feedbackRepo;
    JWTService jwtService;
    AccountRepo accountRepo;
    DesignRequestRepo designRequestRepo;
    FeedbackImageRepo feedbackImageRepo;
    DesignQuotationRepo designQuotationRepo;
    PartnerRepo partnerRepo;
    OrderRepo orderRepo;

    @Override
    public ResponseEntity<ResponseObject> getFeedbacksByOrder(Integer orderId) {
        Feedback feedback = feedbackRepo.findByOrder_Id(orderId);
        if (feedback == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "No feedback found for this design order", null);
        }

        return ResponseBuilder.build(HttpStatus.OK, "Feedback found", EntityResponseBuilder.buildFeedbackResponse(feedback));
    }

    @Override
    public ResponseEntity<ResponseObject> getFeedbacksByDesign(Integer designRequestId) {
        DesignRequest designRequest = designRequestRepo.findById(designRequestId).orElse(null);
        if (designRequest == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Design request not found", null);
        }

        Feedback feedback = designRequest.getFeedback();
        if (feedback == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "No feedback found for this design request", null);
        }

        return ResponseBuilder.build(HttpStatus.OK, "Feedback found", EntityResponseBuilder.buildFeedbackResponse(feedback));
    }

    @Override
    public ResponseEntity<ResponseObject> getFeedbackByGarment(Integer garmentId) {
        Partner garment = partnerRepo.findById(garmentId).orElse(null);
        if (garment == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Garment not found", null);
        }

        List<Map<String, Object>> data = orderRepo.findAll().stream()
                .filter(Objects::nonNull)
                .filter(o -> Objects.equals(o.getGarmentId(), garmentId))
                .filter(o -> o.getFeedback() != null)
                .map(o -> {
                    Feedback fb = o.getFeedback();

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", fb.getId());
                    map.put("rating", fb.getRating());
                    map.put("content", fb.getContent());
                    map.put("report", fb.isReport());
                    map.put("creationDate", fb.getCreationDate());

                    map.put("orderId", o.getId());
                    map.put("garmentId", o.getGarmentId());
                    map.put("garmentName", o.getGarmentName());

                    String schoolName = null;
                    if (o.getSchoolDesign() != null && o.getSchoolDesign().getCustomer() != null) {
                        schoolName = o.getSchoolDesign().getCustomer().getName();
                    }
                    map.put("schoolName", schoolName);

                    List<String> images = (fb.getFeedbackImages() != null)
                            ? fb.getFeedbackImages().stream()
                            .filter(Objects::nonNull)
                            .map(FeedbackImage::getImageUrl)
                            .filter(Objects::nonNull)
                            .toList()
                            : List.of();
                    map.put("images", images);

                    return map;
                })
                .toList();

        String message = data.isEmpty()
                ? "No feedback found for this garment."
                : "Feedback list for garment get successfully.";
        return ResponseBuilder.build(HttpStatus.OK, message, data);
    }

    @Override
    public ResponseEntity<ResponseObject> getFeedbackByDesigner(Integer designerId) {
        Partner designer = partnerRepo.findById(designerId).orElse(null);
        if (designer == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Designer (partner) not found", null);
        }

        Set<Integer> designerQuotationIds = designQuotationRepo.findAllByDesigner_Id(designerId)
                .stream()
                .map(DesignQuotation::getId)
                .collect(java.util.stream.Collectors.toSet());

        if (designerQuotationIds.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.OK, "No feedback found for this designer", List.of());
        }

        List<DesignRequest> requestsWithFeedback = designRequestRepo.findAllByFeedbackIsNotNull()
                .stream()
                .filter(dr -> dr.getDesignQuotationId() != null && designerQuotationIds.contains(dr.getDesignQuotationId()))
                .toList();

        if (requestsWithFeedback.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.OK, "No feedback found for this designer", List.of());
        }

        List<Feedback> feedbacks = requestsWithFeedback.stream()
                .map(DesignRequest::getFeedback)
                .filter(Objects::nonNull)
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "List of designer feedback", EntityResponseBuilder.buildListFeedbackResponse(feedbacks));
    }


    @Override
    public ResponseEntity<ResponseObject> approveReport(ApproveReportRequest request) {
        Feedback report = feedbackRepo.findById(request.getFeedbackId()).orElse(null);

        String error = FeedbackValidation.validateApproveFeedback(request, report);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        if (request.getAdminMessage() != null && !request.getAdminMessage().isBlank()) {
            String updated = (report.getContent() == null ? "" : report.getContent() + "\n\n")
                    + "[Admin Response] " + request.getAdminMessage();
            report.setContent(updated);
        }

        report.setReport(false);
        feedbackRepo.save(report);

        return ResponseBuilder.build(HttpStatus.OK, "Feedback approved successfully", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> giveFeedback(HttpServletRequest httpServletRequest, GiveFeedbackRequest request) {
        String error = FeedbackValidation.validateGiveFeedback(request);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if (account == null || account.getCustomer() == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account/Customer not found", null);
        }
        Customer school = account.getCustomer();

        if (request.getRequestId() != null) {
            return handleDesignRequestFeedback(request, school);
        } else {
            return handleOrderFeedback(request, school);
        }
    }

    private ResponseEntity<ResponseObject> handleDesignRequestFeedback(GiveFeedbackRequest request, Customer school) {
        DesignRequest dr = designRequestRepo.findById(request.getRequestId()).orElse(null);
        if (dr == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Design request not found", null);
        }

        if (!Objects.equals(dr.getSchool().getId(), school.getId())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You are not the owner of this request", null);
        }

        if (dr.getFeedback() != null) {
            return ResponseBuilder.build(HttpStatus.CONFLICT, "Feedback already exists for this design request", null);
        }

        Partner designer = resolveDesignerPartner(dr);
        if (designer == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Designer partner could not be resolved for this request", null);
        }

        Feedback feedback = Feedback.builder()
                .rating(request.getRating())
                .content(request.getContent())
                .report(request.isReport())
                .creationDate(LocalDate.now())
                .order(null)
                .designRequest(dr)
                .build();

        feedback = feedbackRepo.save(feedback);

        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            FeedbackImage img = FeedbackImage.builder()
                    .imageUrl(request.getImageUrl())
                    .feedback(feedback)
                    .build();
            feedbackImageRepo.save(img);
        }

        dr.setFeedback(feedback);
        designRequestRepo.save(dr);

        updatePartnerAverageRating(designer);

        return ResponseBuilder.build(HttpStatus.OK, "Feedback created successfully for design request", null);
    }

    private ResponseEntity<ResponseObject> handleOrderFeedback(GiveFeedbackRequest request, Customer school) {
        Order order = orderRepo.findById(request.getOrderId()).orElse(null);
        if (order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);
        }

        if (order.getSchoolDesign() == null || order.getSchoolDesign().getCustomer() == null
                || !Objects.equals(order.getSchoolDesign().getCustomer().getId(), school.getId())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You are not the owner of this order's school design", null);
        }

        if (order.getFeedback() != null) {
            return ResponseBuilder.build(HttpStatus.CONFLICT, "Feedback already exists for this order", null);
        }

        if (order.getGarmentId() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Order has no garment assigned", null);
        }
        Partner garment = partnerRepo.findById(order.getGarmentId()).orElse(null);
        if (garment == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Garment partner not found", null);
        }

        Feedback feedback = Feedback.builder()
                .rating(request.getRating())
                .content(request.getContent())
                .report(request.isReport())
                .creationDate(LocalDate.now())
                .order(order)
                .designRequest(null)
                .build();

        feedback = feedbackRepo.save(feedback);

        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            FeedbackImage img = FeedbackImage.builder()
                    .imageUrl(request.getImageUrl())
                    .feedback(feedback)
                    .build();
            feedbackImageRepo.save(img);
        }

        order.setFeedback(feedback);
        orderRepo.save(order);

        updatePartnerAverageRating(garment);

        return ResponseBuilder.build(HttpStatus.OK, "Feedback created successfully for order", null);
    }

    private Partner resolveDesignerPartner(DesignRequest dr) {
        if (dr.getDesignQuotationId() != null) {
            DesignQuotation dq = designQuotationRepo.findById(dr.getDesignQuotationId()).orElse(null);
            if (dq != null && dq.getDesigner() != null) {
                return dq.getDesigner();
            }
        }

        if (dr.getDesignQuotations() != null && !dr.getDesignQuotations().isEmpty()) {
            return dr.getDesignQuotations().stream()
                    .filter(q -> q.getStatus() == Status.DESIGN_QUOTATION_PENDING && q.getDesigner() != null)
                    .map(DesignQuotation::getDesigner)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    private boolean isDesigner(Partner partner) {
        return partner.getDesignQuotations() != null && !partner.getDesignQuotations().isEmpty();
    }

    private boolean isGarment(Partner partner) {
        return partner.getGarmentQuotations() != null && !partner.getGarmentQuotations().isEmpty();
    }

    private void updatePartnerAverageRating(Partner partner) {
        if (isDesigner(partner)) {
            updateDesignerAverageRating(partner);
        } else if (isGarment(partner)) {
            updateGarmentAverageRating(partner);
        }
    }

    private void updateDesignerAverageRating(Partner designer) {
        Map<Integer, DesignQuotation> dqById = designQuotationRepo.findAll().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(DesignQuotation::getId, dq -> dq, (a, b) -> a));

        IntSummaryStatistics stats = designRequestRepo.findAll().stream()
                .filter(Objects::nonNull)
                .filter(dr -> dr.getFeedback() != null && dr.getDesignQuotationId() != null)
                .map(dr -> new AbstractMap.SimpleEntry<>(dr, dqById.get(dr.getDesignQuotationId())))
                .filter(e -> e.getValue() != null
                        && e.getValue().getDesigner() != null
                        && Objects.equals(e.getValue().getDesigner().getId(), designer.getId()))
                .map(e -> e.getKey().getFeedback())
                .mapToInt(Feedback::getRating)
                .summaryStatistics();

        if (stats.getCount() == 0) {
            designer.setRating(0);
            return;
        }

        int rounded = (int) Math.round(stats.getAverage());
        designer.setRating(rounded);
        partnerRepo.save(designer);
    }

    private void updateGarmentAverageRating(Partner garment) {
        IntSummaryStatistics stats = orderRepo.findAll().stream()
                .filter(Objects::nonNull)
                .filter(o -> o.getFeedback() != null)
                .filter(o -> Objects.equals(o.getGarmentId(), garment.getId()))
                .map(Order::getFeedback)
                .mapToInt(Feedback::getRating)
                .summaryStatistics();

        if (stats.getCount() == 0) {
            garment.setRating(0);
            return;
        }

        int rounded = (int) Math.round(stats.getAverage());
        garment.setRating(rounded);
        partnerRepo.save(garment);
    }
}
