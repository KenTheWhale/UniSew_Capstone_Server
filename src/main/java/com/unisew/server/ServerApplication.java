package com.unisew.server;

import com.unisew.server.enums.DesignItemCategory;
import com.unisew.server.enums.DesignItemType;
import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.Fabric;
import com.unisew.server.models.PlatformConfig;
import com.unisew.server.models.Wallet;
import com.unisew.server.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@SpringBootApplication
@RequiredArgsConstructor
public class ServerApplication {

    private final AccountRepo accountRepo;

    private final CustomerRepo customerRepo;

    private final FabricRepo fabricRepo;

    private final WalletRepo walletRepo;

    private final PlatformConfigRepo platformConfigRepo;


    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
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
                                .email("unisewsu2025@gmail.com")
                                .role(Role.ADMIN)
                                .registerDate(LocalDate.now())
                                .status(Status.ACCOUNT_ACTIVE)
                                .build();

                        accountRepo.saveAll(List.of(customer1Account));

                        // WALLET
                        if (walletRepo.count() == 0) {
                            Wallet walletAccount1 = Wallet.builder()
                                    .account(customer1Account)
                                    .balance(0)
                                    .pendingBalance(0)
                                    .cardOwner("VO DANG KIEN QUOC")
                                    .bankAccountNumber("9989575268")
                                    .bank("VCB")
                                    .build();

                            walletRepo.saveAll(List.of(walletAccount1));
                        }

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

                    }
                }

                if (platformConfigRepo.count() == 0) {

                    Map<String, Object> businessData = new HashMap<>();
                    businessData.put("taxRate", 0.05);
                    businessData.put("serviceRate", 0.05);
                    businessData.put("minPay", 10000);
                    businessData.put("maxPay", 200000000);

                    Map<String, Object> mediaData = new HashMap<>();
                    mediaData.put("maxImgSize", 10);
                    mediaData.put("maxVideoSize", 50);
                    mediaData.put("maxDesignRefImg", 4);
                    mediaData.put("maxFeedbackImg", 4);
                    mediaData.put("maxFeedbackVideo", 1);
                    mediaData.put("maxReportImg", 4);
                    mediaData.put("maxReportVideo", 1);
                    mediaData.put("maxGarmentThumbnail", 4);
                    mediaData.put("imgFormat", List.of(
                            Map.of("format", ".jpg"),
                            Map.of("format", ".jpeg"),
                            Map.of("format", ".png"),
                            Map.of("format", ".gif"),
                            Map.of("format", ".webp")
                    ));
                    mediaData.put("videoFormat", List.of(
                            Map.of("format", ".mp4"),
                            Map.of("format", ".avi"),
                            Map.of("format", ".mov"),
                            Map.of("format", ".wmv"),
                            Map.of("format", ".webm")
                    ));

                    Map<String, Object> designData = new HashMap<>();
                    designData.put("illustrationImage", "https://res.cloudinary.com/dj0ckodyq/image/upload/v1756751023/logoPos_yffeh1.png");
                    designData.put("positions", List.of(
                            Map.of("p", "Top Left"),
                            Map.of("p", "Top Right"),
                            Map.of("p", "Bottom Left"),
                            Map.of("p", "Bottom Right"),
                            Map.of("p", "Center")
                    ));

                    Map<String, Object> orderData = new HashMap<>();
                    orderData.put("minUniformQty", 50);
                    orderData.put("maxAssignedMilestone", 5);
                    Map<String, Object> reportData = new HashMap<>();
                    reportData.put("maxAppealDay", 7);
                    reportData.put("maxDisbursementDay", 7);
                    reportData.put("severityLevels", List.of(
                            Map.of("name", "Minor", "compensation", 0.1),
                            Map.of("name", "Moderate", "compensation", 0.25),
                            Map.of("name", "Major", "compensation", 0.5),
                            Map.of("name", "Critical", "compensation", 1)
                    ));

                    LocalDate today = LocalDate.now();

                    platformConfigRepo.saveAll(
                            List.of(
                                    PlatformConfig.builder().key("business").value(businessData).creationDate(today).modifiedDate(today).build(),
                                    PlatformConfig.builder().key("media").value(mediaData).creationDate(today).modifiedDate(today).build(),
                                    PlatformConfig.builder().key("design").value(designData).creationDate(today).modifiedDate(today).build(),
                                    PlatformConfig.builder().key("order").value(orderData).creationDate(today).modifiedDate(today).build(),
                                    PlatformConfig.builder().key("report").value(reportData).creationDate(today).modifiedDate(today).build()
                            )
                    );
                }
            }
        };

    }

}


