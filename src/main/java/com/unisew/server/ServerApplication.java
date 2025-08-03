package com.unisew.server;

import com.unisew.server.enums.*;
import com.unisew.server.models.*;
import com.unisew.server.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
    public CommandLineRunner initData() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                if (accountRepo.count() == 0 && customerRepo.count() == 0) {
                    if (accountRepo.count() == 0 && customerRepo.count() == 0) {

                        Account customer1Account = Account.builder()
                                .email("customer1@unisew.com")
                                .role(Role.SCHOOL)
                                .registerDate(LocalDate.now())
                                .status(Status.ACCOUNT_ACTIVE)
                                .build();

                        Account customer2Account = Account.builder()
                                .email("customer2@unisew.com")
                                .role(Role.DESIGNER)
                                .registerDate(LocalDate.now())
                                .status(Status.ACCOUNT_ACTIVE)
                                .build();

                        accountRepo.saveAll(List.of(customer1Account, customer2Account));

                        // WALLET
                        if (walletRepo.count() == 0) {
                            Wallet walletAccount1 = Wallet.builder()
                                    .account(customer1Account)
                                    .balance(500_000L)
                                    .pendingBalance(200_000L)
                                    .cardNumber("1234-5678-9012-3456")
                                    .cardName("Nguyen Van A")
                                    .cardExpiredDate("12/27")
                                    .build();

                            Wallet walletAccount2 = Wallet.builder()
                                    .account(customer2Account)
                                    .balance(600_000L)
                                    .pendingBalance(200_000L)
                                    .cardNumber("1234-5678-9012-3456")
                                    .cardName("Nguyen Van A")
                                    .cardExpiredDate("12/27")
                                    .build();

                            walletRepo.saveAll(List.of(walletAccount1, walletAccount2));
                        }

                        // CUSTOMER
                        if (customerRepo.count() == 0) {
                            Customer customer1 = Customer.builder()
                                    .account(customer1Account)
                                    .name("Nguyen Van A")
                                    .phone("0900000001")
                                    .address("Hanoi")
                                    .taxCode("CUST001")
                                    .avatar(null)
                                    .build();

                            Customer customer2 = Customer.builder()
                                    .account(customer2Account)
                                    .name("Tran Thi B")
                                    .phone("0900000002")
                                    .address("Ho Chi Minh City")
                                    .taxCode("CUST002")
                                    .avatar(null)
                                    .build();

                            customerRepo.saveAll(List.of(customer1, customer2));

                            // PARTNER
                            if (partnerRepo.count() == 0) {
                                Partner partner1 = Partner.builder()
                                        .customer(customer2)
                                        .outsidePreview("none")
                                        .insidePreview("none")
                                        .startTime(LocalTime.of(14, 12, 58))
                                        .endTime(LocalTime.of(14, 15, 38))
                                        .rating(4)
                                        .busy(false)
                                        .build();

                                partnerRepo.save(partner1);
                                //----------------------FABRIC--------------------//
                                if (fabricRepo.count() == 0) {
                                    List<Fabric> fabrics = List.of(
                                            // Regular School Uniform
                                            Fabric.builder().name("Cotton fabric")
                                                    .description("Soft, breathable cotton for school shirts")
                                                    .designItemType(DesignItemType.SHIRT)
                                                    .designItemCategory(DesignItemCategory.REGULAR)
                                                    .build(),
                                            Fabric.builder().name("Oxford fabric")
                                                    .description("Durable and slightly textured oxford for shirts")
                                                    .designItemType(DesignItemType.SHIRT)
                                                    .designItemCategory(DesignItemCategory.REGULAR)
                                                    .build(),
                                            Fabric.builder().name("Poplin fabric")
                                                    .description("Smooth and lightweight poplin for shirts")
                                                    .designItemType(DesignItemType.SHIRT)
                                                    .designItemCategory(DesignItemCategory.REGULAR)
                                                    .build(),
                                            Fabric.builder().name("Poly-cotton blend")
                                                    .description("Blend of polyester and cotton for shirts")
                                                    .designItemType(DesignItemType.SHIRT)
                                                    .designItemCategory(DesignItemCategory.REGULAR)
                                                    .build(),

                                            Fabric.builder().name("Gabardine")
                                                    .description("Tightly woven, durable fabric for trousers")
                                                    .designItemType(DesignItemType.PANTS)
                                                    .designItemCategory(DesignItemCategory.REGULAR)
                                                    .build(),
                                            Fabric.builder().name("Twill fabric")
                                                    .description("Diagonal weave twill for school pants")
                                                    .designItemType(DesignItemType.PANTS)
                                                    .designItemCategory(DesignItemCategory.REGULAR)
                                                    .build(),
                                            Fabric.builder().name("Lightweight khaki fabric")
                                                    .description("Light and breathable khaki for pants")
                                                    .designItemType(DesignItemType.PANTS)
                                                    .designItemCategory(DesignItemCategory.REGULAR)
                                                    .build(),

                                            Fabric.builder().name("Wool blend")
                                                    .description("Soft and warm wool blend for skirts")
                                                    .designItemType(DesignItemType.SKIRT)
                                                    .designItemCategory(DesignItemCategory.REGULAR)
                                                    .build(),
                                            Fabric.builder().name("Soft twill")
                                                    .description("Soft and flexible twill fabric for skirts")
                                                    .designItemType(DesignItemType.SKIRT)
                                                    .designItemCategory(DesignItemCategory.REGULAR)
                                                    .build(),

                                            // Sports / PE Uniform
                                            Fabric.builder().name("Stretch cotton")
                                                    .description("Elastic and comfortable cotton for PE T-shirts")
                                                    .designItemType(DesignItemType.SHIRT)
                                                    .designItemCategory(DesignItemCategory.PHYSICAL)
                                                    .build(),
                                            Fabric.builder().name("Breathable polyester")
                                                    .description("Quick-dry and breathable polyester for PE shirts")
                                                    .designItemType(DesignItemType.SHIRT)
                                                    .designItemCategory(DesignItemCategory.PHYSICAL)
                                                    .build(),
                                            Fabric.builder().name("Poly-spandex blend")
                                                    .description("Flexible poly-spandex blend for PE shirts")
                                                    .designItemType(DesignItemType.SHIRT)
                                                    .designItemCategory(DesignItemCategory.PHYSICAL)
                                                    .build(),
                                            Fabric.builder().name("Sports mesh fabric")
                                                    .description("Breathable mesh fabric for PE T-shirts")
                                                    .designItemType(DesignItemType.SHIRT)
                                                    .designItemCategory(DesignItemCategory.PHYSICAL)
                                                    .build(),

                                            Fabric.builder().name("Poly-cotton jersey")
                                                    .description("Soft jersey for sports shorts and sweatpants")
                                                    .designItemType(DesignItemType.PANTS)
                                                    .designItemCategory(DesignItemCategory.PHYSICAL)
                                                    .build(),
                                            Fabric.builder().name("French terry")
                                                    .description("Comfortable French terry for sweatpants")
                                                    .designItemType(DesignItemType.PANTS)
                                                    .designItemCategory(DesignItemCategory.PHYSICAL)
                                                    .build(),
                                            Fabric.builder().name("Stretch athletic fabric")
                                                    .description("Flexible fabric for sports pants and shorts")
                                                    .designItemType(DesignItemType.PANTS)
                                                    .designItemCategory(DesignItemCategory.PHYSICAL)
                                                    .build(),
                                            Fabric.builder().name("Poly-spandex blend")
                                                    .description("Stretch fabric for sports pants")
                                                    .designItemType(DesignItemType.PANTS)
                                                    .designItemCategory(DesignItemCategory.PHYSICAL)
                                                    .build(),
                                            Fabric.builder().name("Breathable athletic fabric")
                                                    .description("Light and breathable fabric for PE pants")
                                                    .designItemType(DesignItemType.PANTS)
                                                    .designItemCategory(DesignItemCategory.PHYSICAL)
                                                    .build()
                                    );

                                    fabricRepo.saveAll(fabrics);
                                }

                                // PACKAGES
                                if (packagesRepo.count() == 0) {
                                    List<Packages> packages = List.of(
                                            Packages.builder()
                                                    .designer(partner1)
                                                    .fee(100)
                                                    .headerContent("this is for test")
                                                    .name("basic")
                                                    .deliveryDuration(5)
                                                    .status(Status.PACKAGE_ACTIVE)
                                                    .revisionTime(1)
                                                    .build(),
                                            Packages.builder()
                                                    .designer(partner1)
                                                    .fee(200)
                                                    .headerContent("this is for test 2")
                                                    .name("medium")
                                                    .deliveryDuration(7)
                                                    .status(Status.PACKAGE_ACTIVE)
                                                    .revisionTime(1)
                                                    .build(),
                                            Packages.builder()
                                                    .designer(partner1)
                                                    .fee(300)
                                                    .headerContent("this is for test 3")
                                                    .name("vip")
                                                    .deliveryDuration(9)
                                                    .status(Status.PACKAGE_ACTIVE)
                                                    .revisionTime(1)
                                                    .build()
                                    );
                                    packagesRepo.saveAll(packages);
                                }

                                // DESIGN_REQUEST
                                if (designRequestRepo.count() == 0) {
                                    DesignRequest request1 = DesignRequest.builder()
                                            .school(customer1)
                                            .template(null)
                                            .packageId(1)
                                            .name("Design Request 1")
                                            .creationDate(LocalDate.now())
                                            .logoImage("logo.png")
                                            .privacy(false)
                                            .status(Status.DESIGN_REQUEST_CREATED)
                                            .packageName("Basic Package")
                                            .headerContent("Welcome")
                                            .packageDeliveryWithin(5)
                                            .revisionTime(2)
                                            .packagePrice(500_000L)
                                            .build();

                                    designRequestRepo.save(request1);

                                    // REQUEST_RECEIPT
                                    if (requestReceiptRepo.count() == 0) {
                                        RequestReceipt receipt1 = RequestReceipt.builder()
                                                .designRequest(request1)
                                                .pkg(packagesRepo.findById(1).orElse(null))
                                                .acceptanceDeadline(LocalDate.of(2025, 8, 4))
                                                .status(Status.RECEIPT_PENDING)
                                                .build();

                                        RequestReceipt receipt2 = RequestReceipt.builder()
                                                .designRequest(request1)
                                                .pkg(packagesRepo.findById(2).orElse(null))
                                                .acceptanceDeadline(LocalDate.of(2025, 8, 4))
                                                .status(Status.RECEIPT_PENDING)
                                                .build();

                                        RequestReceipt receipt3 = RequestReceipt.builder()
                                                .designRequest(request1)
                                                .pkg(packagesRepo.findById(3).orElse(null))
                                                .acceptanceDeadline(LocalDate.of(2025, 8, 3))
                                                .status(Status.RECEIPT_PENDING)
                                                .build();

                                        requestReceiptRepo.saveAll(List.of(receipt1, receipt2, receipt3));
                                    }
                                    //----------------------DESIGN_ITEM--------------------//
                                    if (designItemRepo.count() == 0) {

                                        Fabric fabric = fabricRepo.findById(1).orElse(null);

                                        DesignItem item1 = DesignItem.builder()
                                                .designRequest(request1)
                                                .type(DesignItemType.SHIRT)
                                                .category(DesignItemCategory.REGULAR)
                                                .logoPosition("Left Chest")
                                                .color("#FFFFFF")
                                                .note("Long Shirt")
                                                .gender(Gender.BOY)
                                                .fabric(fabric)
                                                .build();

                                        DesignItem item2 = DesignItem.builder()
                                                .designRequest(request1)
                                                .type(DesignItemType.PANTS)
                                                .category(DesignItemCategory.REGULAR)
                                                .logoPosition("")
                                                .color("#000000")
                                                .note("Pant for regular")
                                                .gender(Gender.BOY)
                                                .fabric(fabric)
                                                .build();

                                        designItemRepo.saveAll(List.of(item1, item2));
                                    }

                                }
                            }
                        }
                    }

                }
                //----------------------DELIVERY_ITEM--------------------//
                if (deliveryItemRepo.count() == 0) {

                }
                //----------------------DESIGN_COMMENT--------------------//
                if (designCommentRepo.count() == 0) {

                }
                //----------------------DESIGN_DELIVERY--------------------//
                if (designDeliveryRepo.count() == 0) {

                }




                // Feedback
                if (feedbackRepo.count() == 0) {

                }
                // FeedbackImage
                if (feedbackImageRepo.count() == 0) {

                }
                // Order
                if (orderRepo.count() == 0) {

                }
                // OrderDetail
                if (orderDetailRepo.count() == 0) {

                }
                // PackageService
                if (packageServiceRepo.count() == 0) {

                }
                // Quotation
                if (quotationRepo.count() == 0) {

                }
                // RevisionRequest
                if (revisionRequestRepo.count() == 0) {

                }
                // SampleImage
                if (sampleImageRepo.count() == 0) {

                }
                // SchoolDesign
                if (schoolDesignRepo.count() == 0) {

                }
                // Service
                if (serviceRepo.count() == 0) {

                }
                // ThumbnailImage
                if (thumbnailImageRepo.count() == 0) {

                }

                if (transactionRepo.count() == 0) {

                }

            }
        };
    }

}
