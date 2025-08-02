package com.unisew.server;

import com.unisew.server.models.Fabric;
import com.unisew.server.models.FeedbackImage;
import com.unisew.server.models.SchoolDesign;
import com.unisew.server.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class ServerApplication {

    private final AccountRepo accountRepo;

    private final CustomerRepo customerRepo;

    private final DeliveryItemRepo deliveryItemRepo;

    private final DesignCommentRepo designCommentRepo;

    private final DesignDeliveryRepo designDeliveryRepo;

    private final DesignItemRepo designItemRepo;

    private final DesignRequestRepo designRequestRepo;

    private final FabricRepo fabricRepo;

    private final FeedbackRepo feedbackRepo;

    private final FeedbackImageRepo feedbackImageRepo;

    private final ItemImageRepo imageRepo;

    private final OrderRepo orderRepo;

    private final OrderDetailRepo orderDetailRepo;

    private final PackagesRepo packagesRepo;

    private final PackageServiceRepo packageServiceRepo;

    private final PartnerRepo partnerRepo;

    private final QuotationRepo quotationRepo;

    private final RequestReceiptRepo requestReceiptRepo;

    private final RevisionRequestRepo revisionRequestRepo;

    private final SampleImageRepo sampleImageRepo;

    private final SchoolDesignRepo schoolDesignRepo;

    private final ServiceRepo serviceRepo;

    private final ThumbnailImageRepo thumbnailImageRepo;

    private final TransactionRepo transactionRepo;

    private final WalletRepo walletRepo;



    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                // Danh cho DB Local create-drop//

                //----------------------ACCOUNT--------------------//
                if(accountRepo.count() == 0) {

                }
                //----------------------CUSTOMER--------------------//
                if(customerRepo.count() == 0) {

                }
                //----------------------DELIVERY_ITEM--------------------//
                if(deliveryItemRepo.count() == 0) {

                }
                //----------------------DESIGN_COMMENT--------------------//
                if(designCommentRepo.count() == 0) {

                }
                //----------------------DESIGN_DELIVERY--------------------//
                if(designDeliveryRepo.count() == 0) {

                }
                //----------------------DESIGN_ITEM--------------------//
                if(designItemRepo.count() == 0) {

                }
                //----------------------DESIGN_REQUEST--------------------//
                if(designRequestRepo.count() == 0) {

                }
                //----------------------FABRIC--------------------//
                if(fabricRepo.count() == 0) {

                }
                //----------------------FEEDBACK--------------------//
                if(feedbackRepo.count() == 0) {

                }
                //----------------------FEEDBACK_IMAGE--------------------//
                if(feedbackImageRepo.count() == 0) {

                }
                //----------------------ITEM_IMAGE--------------------//
                if(imageRepo.count() == 0) {

                }
                //----------------------ORDER--------------------//
                if(orderRepo.count() == 0) {

                }
                //----------------------ORDER_DETAIL--------------------//
                if(orderDetailRepo.count() == 0) {

                }
                //----------------------PACKAGES--------------------//
                if(packagesRepo.count() == 0) {

                }
                //----------------------PACKAGES_SERVICE--------------------//
                if(packageServiceRepo.count() == 0) {

                }
                //----------------------PARTNER--------------------//
                if(partnerRepo.count() == 0) {

                }
                //----------------------QUOTATION--------------------//
                if(quotationRepo.count() == 0) {

                }
                //----------------------REQUEST_RECEIPT--------------------//
                if(requestReceiptRepo.count() == 0) {

                }
                //----------------------REVISION_REQUEST--------------------//
                if(revisionRequestRepo.count() == 0) {

                }
                //----------------------SAMPLE_IMAGE--------------------//
                if(sampleImageRepo.count() == 0) {

                }
                //----------------------SCHOOL_DESIGN--------------------//
                if(schoolDesignRepo.count() == 0) {

                }
                //----------------------SERVICE--------------------//
                if(serviceRepo.count() == 0) {

                }
                //----------------------THUMBNAIL_IMAGE--------------------//
                if(thumbnailImageRepo.count() == 0) {

                }
                //----------------------TRANSACTION--------------------//
                if(transactionRepo.count() == 0) {

                }
                //----------------------WALLET--------------------//
                if(walletRepo.count() == 0) {

                }

            }
        };
    }
}
