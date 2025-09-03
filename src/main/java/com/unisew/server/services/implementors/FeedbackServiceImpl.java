package com.unisew.server.services.implementors;

import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.Appeals;
import com.unisew.server.models.Customer;
import com.unisew.server.models.DesignQuotation;
import com.unisew.server.models.DesignRequest;
import com.unisew.server.models.Feedback;
import com.unisew.server.models.FeedbackImage;
import com.unisew.server.models.Order;
import com.unisew.server.models.Partner;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.AppealsRepo;
import com.unisew.server.repositories.DeliveryItemRepo;
import com.unisew.server.repositories.DesignItemRepo;
import com.unisew.server.repositories.DesignQuotationRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.FeedbackImageRepo;
import com.unisew.server.repositories.FeedbackRepo;
import com.unisew.server.repositories.OrderRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.TransactionRepo;
import com.unisew.server.requests.GiveAppealsRequest;
import com.unisew.server.requests.ApproveAppealsRequest;
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

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    DeliveryItemRepo deliveryItemRepo;
    DesignItemRepo designItemRepo;
    AppealsRepo appealsRepo;
    TransactionRepo transactionRepo;

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
    public ResponseEntity<ResponseObject> getFeedbackByGarment(HttpServletRequest httpServletRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account/Partner not found", null);
        }
        Partner garment = partnerRepo.findById(account.getCustomer().getPartner().getId()).orElse(null);
        if (garment == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Garment (partner) not found", null);
        }

        List<Map<String, Object>> data = orderRepo.findAll().stream()
                .filter(Objects::nonNull)
                .filter(o -> Objects.equals(o.getGarmentId(), garment.getId()))
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
    public ResponseEntity<ResponseObject> getFeedbackByDesigner(HttpServletRequest httpServletRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account/Partner not found", null);
        }
        Partner designer = partnerRepo.findById(account.getCustomer().getPartner().getId()).orElse(null);
        if (designer == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Designer (partner) not found", null);
        }

        Set<Integer> designerQuotationIds = designQuotationRepo.findAllByDesigner_Id(designer.getId())
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
        Feedback feedback = feedbackRepo.findById(request.getFeedbackId()).orElse(null);

        String error = FeedbackValidation.validateApproveReport(request, feedback);
        if (error != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        if (request.getMessageForSchool() != null && !request.getMessageForSchool().isBlank()) {
            feedback.setMessageForSchool(request.getMessageForSchool().trim());
        }
        if (request.getMessageForPartner() != null && !request.getMessageForPartner().isBlank()) {
            feedback.setMessageForPartner(request.getMessageForPartner().trim());
        }

        feedback.setApprovalDate(LocalDate.now());

        if (request.isApproved()) {
            feedback.setStatus(Status.FEEDBACK_REPORT_RESOLVED_ACCEPTED);
        } else {
            feedback.setStatus(Status.FEEDBACK_REPORT_RESOLVED_REJECTED);
        }

        feedbackRepo.save(feedback);

        return ResponseBuilder.build(HttpStatus.OK, "Feedback report processed successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getAllReport() {
        List<Feedback> reports = feedbackRepo.findAllByReportIsTrue();
        return ResponseBuilder.build(HttpStatus.OK, "List of all feedback reports", EntityResponseBuilder.buildListReportResponse(reports, partnerRepo, deliveryItemRepo, designItemRepo, designQuotationRepo, designRequestRepo, transactionRepo) );
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
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "You are not the owner of this request", null);
        }
        if (dr.getFeedback() != null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Feedback already exists for this design request", null);
        }

        Partner designer = resolveDesignerPartner(dr);
        if (designer == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Designer partner could not be resolved for this request", null);
        }

        Feedback feedback = feedbackRepo.save(
                Feedback.builder()
                        .rating(request.getRating())
                        .content(request.getContent())
                        .report(request.isReport())
                        .creationDate(LocalDate.now())
                        .messageForPartner("")
                        .messageForSchool("")
                        .videoUrl(request.getVideoUrl())
                        .approvalDate(LocalDate.now())
                        .appealDeadline(request.getAppealsDeadline())
                        .designRequest(dr)
                        .order(null)
                        .status(request.isReport() ? Status.FEEDBACK_REPORT_UNDER_REVIEW : Status.FEEDBACK_APPROVED)
                        .build()
        );

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<FeedbackImage> images = request.getImageUrls().stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(url -> FeedbackImage.builder()
                            .imageUrl(url)
                            .feedback(feedback)
                            .build())
                    .toList();
            if (!images.isEmpty()) {
                feedbackImageRepo.saveAll(images);
            }
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
        if (order.getSchoolDesign() == null
                || order.getSchoolDesign().getCustomer() == null
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

        Feedback feedback = feedbackRepo.save(
                Feedback.builder()
                        .rating(request.getRating())
                        .content(request.getContent())
                        .report(request.isReport())
                        .creationDate(LocalDate.now())
                        .messageForPartner("")
                        .messageForSchool("")
                        .videoUrl(request.getVideoUrl())
                        .approvalDate(LocalDate.now())
                        .appealDeadline(request.getAppealsDeadline())
                        .order(order)
                        .designRequest(null)
                        .status(request.isReport() ? Status.FEEDBACK_REPORT_UNDER_REVIEW : Status.FEEDBACK_APPROVED)
                        .build()
        );

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<FeedbackImage> images = request.getImageUrls().stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(url -> FeedbackImage.builder()
                            .imageUrl(url)
                            .feedback(feedback)
                            .build())
                    .toList();
            if (!images.isEmpty()) {
                feedbackImageRepo.saveAll(images);
            }
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

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> giveAppeals(GiveAppealsRequest request, HttpServletRequest httpServletRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if (account == null || account.getCustomer() == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Account not found", null);
        }

        Feedback feedback = feedbackRepo.findById(request.getFeedbackId()).orElse(null);
        if (feedback == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Feedback not found", null);
        }

        Customer school = account.getCustomer();
        boolean owns =
                (feedback.getDesignRequest() != null && feedback.getDesignRequest().getSchool().getId().equals(school.getId()))
                        || (feedback.getOrder() != null && feedback.getOrder().getSchoolDesign().getCustomer().getId().equals(school.getId()));
        if (!owns) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You are not owner of this report", null);
        }
        if (feedback.getApprovalDate() != null && feedback.getAppealDeadline() != null && LocalDate.now().isAfter(feedback.getAppealDeadline())) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Appeal is out of deadline", null);
        }
        boolean existsPending = appealsRepo.existsByFeedback_IdAndStatus(feedback.getId(), Status.APPEAL_UNDER_REVIEW);
        if (existsPending) {
            return ResponseBuilder.build(HttpStatus.CONFLICT, "An appeal is already under reviewing for this report", null);
        }

        appealsRepo.save(
                Appeals.builder()
                        .accountId(account.getId())
                        .reason(request.getReason())
                        .videoUrl(request.getVideoUrl())
                        .creationDate(LocalDate.now())
                        .status(Status.APPEAL_UNDER_REVIEW)
                        .feedback(feedback)
                        .build()
        );
        return ResponseBuilder.build(HttpStatus.OK, "Appeal submitted successfully", null);
    }

    @Override
    public ResponseEntity<ResponseObject> approveAppeal(ApproveAppealsRequest request) {
        if (request == null ) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Request is empty", null);
        }

        Appeals appeals = appealsRepo.findById(request.getAppealId()).orElse(null);
        if (appeals == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Appeal not found", null);
        }
        if (appeals.getStatus() != Status.APPEAL_UNDER_REVIEW) {
            return ResponseBuilder.build(HttpStatus.CONFLICT, "Appeal is already approved", null);
        }
        Feedback feedback = appeals.getFeedback();
        if (feedback == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Feedback not found for this appeal", null);
        }

        Integer schoolAccountId = null;
        Integer partnerAccountId = null;

        if (feedback.getDesignRequest() != null) {
            DesignRequest dr = feedback.getDesignRequest();
            if (dr.getSchool() != null && dr.getSchool().getAccount() != null) {
                schoolAccountId = dr.getSchool().getAccount().getId();
            }
            Partner designer = resolveDesignerPartner(dr);
            if (designer != null && designer.getCustomer() != null && designer.getCustomer().getAccount() != null) {
                partnerAccountId = designer.getCustomer().getAccount().getId();
            }
        } else if (feedback.getOrder() != null) {
            Order od = feedback.getOrder();
            if (od.getSchoolDesign() != null
                    && od.getSchoolDesign().getCustomer() != null
                    && od.getSchoolDesign().getCustomer().getAccount() != null) {
                schoolAccountId = od.getSchoolDesign().getCustomer().getAccount().getId();
            }
            if (od.getGarmentId() != null) {
                Partner garment = partnerRepo.findById(od.getGarmentId()).orElse(null);
                if (garment != null && garment.getCustomer() != null && garment.getCustomer().getAccount() != null) {
                    partnerAccountId = garment.getCustomer().getAccount().getId();
                }
            }
        }

        Integer appellantAccountId = appeals.getAccountId();

        boolean isSchoolAppellant  = (schoolAccountId != null && schoolAccountId.equals(appellantAccountId));
        boolean isPartnerAppellant = (partnerAccountId != null && partnerAccountId.equals(appellantAccountId));

        if (!isSchoolAppellant && !isPartnerAppellant) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Appellant does not belong to this feedback", null);
        }

        boolean approved = request.isApproved();

        if (request.getAdminResponse() != null) {
            appeals.setAdminResponse(request.getAdminResponse().trim());
        }
        appeals.setApprovalDate(LocalDate.now());
        appeals.setStatus(approved ? Status.APPEAL_ACCEPTED : Status.APPEAL_REJECTED);

        if (isSchoolAppellant) {
            String msg = "[Appeal Decision for School] " +
                    (approved ? "APPROVED" : "REJECTED") +
                    (request.getAdminResponse() != null ? (": " + request.getAdminResponse().trim()) : "");
            feedback.setMessageForSchool(
                    concatLines(feedback.getMessageForSchool(), msg)
            );
        } else {
            String msg = "[Appeal Decision for Partner] " +
                    (approved ? "APPROVED" : "REJECTED") +
                    (request.getAdminResponse() != null ? (": " + request.getAdminResponse().trim()) : "");
            feedback.setMessageForPartner(
                    concatLines(feedback.getMessageForPartner(), msg)
            );
        }

        feedbackRepo.save(feedback);
        appealsRepo.save(appeals);

        return ResponseBuilder.build(HttpStatus.OK, "Appeal approved successfully", null);
    }

    private String concatLines(String current, String appended) {
        String base = (current == null || current.isBlank()) ? "" : current.trim();
        return base.isEmpty() ? appended : base + "\n" + appended;
    }
}
