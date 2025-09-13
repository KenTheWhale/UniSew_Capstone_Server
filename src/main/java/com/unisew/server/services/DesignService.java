package com.unisew.server.services;

import com.unisew.server.requests.CancelRequest;
import com.unisew.server.requests.CreateDesignQuotationRequest;
import com.unisew.server.requests.CreateDesignRequest;
import com.unisew.server.requests.CreateNewDeliveryRequest;
import com.unisew.server.requests.CreateRevisionRequest;
import com.unisew.server.requests.DuplicateRequest;
import com.unisew.server.requests.GetListDeliveryRequest;
import com.unisew.server.requests.GetUnUseListRevisionRequest;
import com.unisew.server.requests.ImportDesignRequest;
import com.unisew.server.requests.MakeDesignFinalRequest;
import com.unisew.server.requests.PickDesignQuotationRequest;
import com.unisew.server.requests.UpdateRequestByDeadline;
import com.unisew.server.requests.UpdateRevisionTimeRequest;
import com.unisew.server.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface DesignService {
    //--------------------------------DESIGN_REQUEST-----------------------------------------//
    ResponseEntity<ResponseObject> createDesignRequest(CreateDesignRequest createDesignRequest, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> viewListDesignRequest();

    ResponseEntity<ResponseObject> getListDesignRequestBySchool(HttpServletRequest request);

    ResponseEntity<ResponseObject> getDesignRequestDetailForSchool(int id);

    ResponseEntity<ResponseObject> getDesignRequestDetailForDesigner(int id);

    ResponseEntity<ResponseObject> getListDesignRequestByDesigner(HttpServletRequest request);

    ResponseEntity<ResponseObject> getListRejectedDesignRequestByDesigner(HttpServletRequest request);

    ResponseEntity<ResponseObject> pickDesignQuotation(PickDesignQuotationRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> updateRequestByDeadline(UpdateRequestByDeadline request);

    ResponseEntity<ResponseObject> duplicateRequest(DuplicateRequest request);

    ResponseEntity<ResponseObject> buyRevisionTime(UpdateRevisionTimeRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> cancelRequest(CancelRequest request, HttpServletRequest httpServletRequest);

    ResponseEntity<ResponseObject> importDesign(ImportDesignRequest request, HttpServletRequest httpRequest);

    //--------------------------------FABRIC-----------------------------------------//
    ResponseEntity<ResponseObject> getAllFabric();

    //--------------------------------DESIGN DELIVERY----------------------------------------//

    ResponseEntity<ResponseObject> getListDeliveries(GetListDeliveryRequest request);

    ResponseEntity<ResponseObject> createNewDelivery(CreateNewDeliveryRequest request);

    //--------------------------------REVISION_REQUEST----------------------------------------//
    ResponseEntity<ResponseObject> createRevisionRequest(CreateRevisionRequest request);

    ResponseEntity<ResponseObject> getAllUnUsedRevisionRequest(GetUnUseListRevisionRequest request);


    //--------------------------------SCHOOL_DESIGN----------------------------------------//
    ResponseEntity<ResponseObject> getListSchoolDesign(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> makeDesignFinal(HttpServletRequest httpRequest, MakeDesignFinalRequest request);

    //--------------------------------DESIGN QUOTATION----------------------------------------//
    ResponseEntity<ResponseObject> getQuotationHistory(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> createQuotation(HttpServletRequest httpRequest, CreateDesignQuotationRequest request);

    ResponseEntity<ResponseObject> getAllDesignRequest();
}
