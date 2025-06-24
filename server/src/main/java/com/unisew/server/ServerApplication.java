package com.unisew.server;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.Designer;
import com.unisew.server.models.PackageService;
import com.unisew.server.models.Packages;
import com.unisew.server.models.Partner;
import com.unisew.server.models.Profile;
import com.unisew.server.models.Services;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.DesignerRepo;
import com.unisew.server.repositories.PackageRepo;
import com.unisew.server.repositories.PackageServiceRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.ProfileRepo;
import com.unisew.server.repositories.ServiceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

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
                }
            }
        };
    }
}
