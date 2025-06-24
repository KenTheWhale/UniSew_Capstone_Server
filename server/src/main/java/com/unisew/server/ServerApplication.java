package com.unisew.server;

import com.unisew.server.enums.ClothCategory;
import com.unisew.server.enums.ClothType;
import com.unisew.server.enums.Gender;
import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.Cloth;
import com.unisew.server.models.DesignDraft;
import com.unisew.server.models.DesignRequest;
import com.unisew.server.models.Designer;
import com.unisew.server.models.DraftImage;
import com.unisew.server.models.PackageService;
import com.unisew.server.models.Packages;
import com.unisew.server.models.Partner;
import com.unisew.server.models.Profile;
import com.unisew.server.models.RevisionRequest;
import com.unisew.server.models.SampleImage;
import com.unisew.server.models.Services;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.ClothRepo;
import com.unisew.server.repositories.DesignDraftRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.DesignerRepo;
import com.unisew.server.repositories.DraftImageRepo;
import com.unisew.server.repositories.PackageRepo;
import com.unisew.server.repositories.PackageServiceRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.ProfileRepo;
import com.unisew.server.repositories.RevisionRequestRepo;
import com.unisew.server.repositories.SampleImageRepo;
import com.unisew.server.repositories.ServiceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class ServerApplication {

    private final AccountRepo accountRepo;

    private final ProfileRepo profileRepo;

    private final PartnerRepo partnerRepo;

    private final DesignerRepo designerRepo;

    private final ServiceRepo serviceRepo;

    private final PackageRepo packageRepo;

    private final PackageServiceRepo packageServiceRepo;

    private final ClothRepo clothRepo;

    private final DesignRequestRepo designRequestRepo;

    private final DesignDraftRepo designDraftRepo;

    private final DraftImageRepo draftImageRepo;

    private final RevisionRequestRepo revisionRequestRepo;

    private final SampleImageRepo sampleImageRepo;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                if(accountRepo.count() == 0){
                    Account account1 = accountRepo.save(
                            Account.builder()
                                    .email("khavhuynhw@gmail.com")
                                    .role(Role.ADMIN)
                                    .registerDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );

                    Account account2 = accountRepo.save(
                            Account.builder()
                                    .email("unisewsu2025@gmail.com")
                                    .role(Role.ADMIN)
                                    .registerDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );

                    Account account3 = accountRepo.save(
                            Account.builder()
                                    .email("luandokhacthanh@gmail.com")
                                    .role(Role.DESIGNER)
                                    .registerDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );

                    Account account4 = accountRepo.save(
                            Account.builder()
                                    .email("quocvdkse160940@fpt.edu.vn")
                                    .role(Role.GARMENT_FACTORY)
                                    .registerDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );
                    Account account5 = accountRepo.save(
                            Account.builder()
                                    .email("quochuy2112002@gmail.com")
                                    .role(Role.SCHOOL)
                                    .registerDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );

                    // 1. Tạo profile
                    Profile profile1 = profileRepo.save(
                            Profile.builder()
                                    .account(account1)
                                    .name("Alice Nguyen")
                                    .phone("0909000001")
                                    .avatar("https://picsum.photos/seed/1/200")
                                    .build()
                    );

                    Profile profile2 = profileRepo.save(
                            Profile.builder()
                                    .account(account2)
                                    .name("Vikor")
                                    .phone("0911094322")
                                    .avatar("https://employer.jobsgo.vn/uploads/media/img/201803/pictures_library_hue-dinh_8457_180316171037_1078.jpg")
                                    .build()
                    );

                    Profile profile3 = profileRepo.save(
                            Profile.builder()
                                    .account(account3)
                                    .name("Neb")
                                    .phone("0909090909")
                                    .avatar("https://employer.jobsgo.vn/uploads/media/img/201803/pictures_library_hue-dinh_8457_180316171037_1078.jpg")
                                    .build()
                    );

                    Profile profile4 = profileRepo.save(
                            Profile.builder()
                                    .account(account4)
                                    .name("Garment TQH")
                                    .phone("0911094322")
                                    .avatar("https://employer.jobsgo.vn/uploads/media/img/201803/pictures_library_hue-dinh_8457_180316171037_1078.jpg")
                                    .build()
                    );
                    Profile profile5 = profileRepo.save(
                            Profile.builder()
                                    .account(account5)
                                    .name("Huy Trần")
                                    .phone("N/A")
                                    .avatar("https://lh3.googleusercontent.com/a/ACg8ocL2WCc2liu2AMfEIUNlTD4UuMODbl-Sj7_BXSna1wFgHz9jfVJC=s96-c")
                                    .build()
                    );

                    //designer
                    Designer designer1 = designerRepo.save(
                            Designer.builder()
                                    .shortPreview("Creative uniform designer")
                                    .bio("Experienced in logo & uniform for schools.")
                                    .profile(profile3)
                                    .build()
                    );

                    //partner
                    Partner partner1 = partnerRepo.save(
                            Partner.builder()
                                    .street("20 Vinh Vien")
                                    .ward("Ward 9")
                                    .district("District 10")
                                    .province("Ho Chi Minh City")
                                    .isBusy(false)
                                    .profile(profile4)
                                    .build()
                    );
                    Partner partner2 = partnerRepo.save(
                            Partner.builder()
                                    .street("N/A")
                                    .ward("N/A")
                                    .district("N/A")
                                    .province("N/A")
                                    .isBusy(false)
                                    .profile(profile5)
                                    .build()
                    );

                    // 2. Tạo services
                    Services service1 = serviceRepo.save(
                            Services.builder()
                                    .rule("Logo vector design")
                                    .creationDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );
                    Services service2 = serviceRepo.save(
                            Services.builder()
                                    .rule("Uniform color consultation")
                                    .creationDate(LocalDate.now())
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .build()
                    );

                    // 3. Tạo package
                    Packages pkg1 = packageRepo.save(
                            Packages.builder()
                                    .name("Basic Design")
                                    .headerContent("Design 1 logo, 1 uniform mockup, 2 revisions")
                                    .deliveryDuration(5)
                                    .revisionTime(2)
                                    .fee(1000000L)
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .designer(designer1)
                                    .build()
                    );
                    Packages pkg2 = packageRepo.save(
                            Packages.builder()
                                    .name("Premium Design")
                                    .headerContent("Design 2 logos, 3 uniform mockups, 5 revisions, color consultation")
                                    .deliveryDuration(7)
                                    .revisionTime(5)
                                    .fee(2500000L)
                                    .status(Status.ACCOUNT_ACTIVE)
                                    .designer(designer1)
                                    .build()
                    );

                    // 4. PackageService
                    PackageService.ID pkgServId1 = PackageService.ID.builder()
                            .packageId(pkg1.getId())
                            .serviceId(service1.getId())
                            .build();

                    PackageService packageService1 = packageServiceRepo.save(
                            PackageService.builder()
                                    .id(pkgServId1)
                                    .pkg(pkg1)
                                    .service(service1)
                                    .build()
                    );

                    PackageService.ID pkgServId2 = PackageService.ID.builder()
                            .packageId(pkg2.getId())
                            .serviceId(service2.getId())
                            .build();

                    PackageService packageService2 = packageServiceRepo.save(
                            PackageService.builder()
                                    .id(pkgServId2)
                                    .pkg(pkg2)
                                    .service(service2)
                                    .build()
                    );

                    DesignRequest request1 = DesignRequest.builder()
                            .creationDate(LocalDate.now().minusDays(10))
                            .isPrivate(true)
                            .status(Status.DESIGN_REQUEST_CREATED)
                            .school(account5)
                            .pkg(null)
                            .feedback(null)
                            .build();

                    DesignRequest request2 = DesignRequest.builder()
                            .creationDate(LocalDate.now().minusDays(7))
                            .isPrivate(true)
                            .status(Status.DESIGN_REQUEST_PENDING)
                            .school(account5)
                            .pkg(null)
                            .feedback(null)
                            .build();

                    designRequestRepo.saveAll(List.of(request1, request2));

                    // ======== CLOTHS ========
                    Cloth boyShirt = Cloth.builder()
                            .type(ClothType.SHIRT)
                            .category(ClothCategory.REGULAR)
                            .color("White")
                            .fabric("Cotton")
                            .gender(Gender.BOY)
                            .note("Short sleeve white shirt for boys")
                            .designRequest(request1)
                            .build();

                    Cloth girlSkirt = Cloth.builder()
                            .type(ClothType.SKIRT)
                            .category(ClothCategory.REGULAR)
                            .color("Blue navy")
                            .fabric("Polyester")
                            .gender(Gender.GIRL)
                            .note("Elementary school uniform skirts for girls")
                            .logoImage("logo_primary_girl.png")
                            .logoPosition("Front belt")
                            .logoWidth(60)
                            .logoHeight(50)
                            .designRequest(request1)
                            .build();

                    Cloth pePants = Cloth.builder()
                            .type(ClothType.PANTS)
                            .category(ClothCategory.PHYSICAL)
                            .color("Gray")
                            .fabric("Elastic")
                            .gender(Gender.BOY)
                            .note("Educate pants for male students")
                            .designRequest(request2)
                            .build();

                    Cloth peShirt = Cloth.builder()
                            .type(ClothType.SHIRT)
                            .category(ClothCategory.PHYSICAL)
                            .color("White with blue")
                            .fabric("Polyester Elastic")
                            .gender(Gender.GIRL)
                            .note("Gym shirt for female students")
                            .designRequest(request2)
                            .build();

                    clothRepo.saveAll(List.of(boyShirt, girlSkirt, pePants, peShirt));

                    // ======== DESIGN DRAFTS ========
                    DesignDraft draft1 = DesignDraft.builder()
                            .description("White shirt sketch")
                            .designDate(LocalDate.now().minusDays(8))
                            .isFinal(false)
                            .cloth(boyShirt)
                            .build();

                    DesignDraft draft2 = DesignDraft.builder()
                            .description("Complete version of women's dress")
                            .designDate(LocalDate.now().minusDays(5))
                            .isFinal(true)
                            .cloth(girlSkirt)
                            .build();

                    designDraftRepo.saveAll(List.of(draft1, draft2));

                    // ======== DRAFT IMAGES ========
                    DraftImage image1 = DraftImage.builder()
                            .name("Men's shirt front")
                            .imageUrl("http://cdn.school.com/draft_boy_shirt_front.png")
                            .designDraft(draft1)
                            .build();

                    DraftImage image2 = DraftImage.builder()
                            .name("Complete dress")
                            .imageUrl("http://cdn.school.com/draft_girl_skirt_final.png")
                            .designDraft(draft2)
                            .build();

                    draftImageRepo.saveAll(List.of(image1, image2));

                    // ======== REVISION REQUESTS ========
                    RevisionRequest rev1 = RevisionRequest.builder()
                            .note("Add front buttons")
                            .designDraft(draft1)
                            .build();

                    RevisionRequest rev2 = RevisionRequest.builder()
                            .note("Small Logo")
                            .designDraft(draft2)
                            .build();

                    revisionRequestRepo.saveAll(List.of(rev1, rev2));

                    // ======== SAMPLE IMAGES ========
                    String cloudinarySampleUrl = "https://res.cloudinary.com/di1aqthok/image/upload/v1750703944/onmapboetu92mv4hvxz0.jpg";

                    SampleImage sample1 = SampleImage.builder()
                            .imageUrl(cloudinarySampleUrl)
                            .cloth(boyShirt)
                            .build();

                    SampleImage sample2 = SampleImage.builder()
                            .imageUrl(cloudinarySampleUrl)
                            .cloth(girlSkirt)
                            .build();

                    SampleImage sample3 = SampleImage.builder()
                            .imageUrl(cloudinarySampleUrl)
                            .cloth(pePants)
                            .build();

                    sampleImageRepo.saveAll(List.of(sample1, sample2, sample3));
                }
            }
        };
    }
}
