package com.unisew.server;

import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.DesignItemRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.PackagesRepo;
import com.unisew.server.repositories.PackageServiceRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.CustomerRepo;
import com.unisew.server.repositories.RevisionRequestRepo;
import com.unisew.server.repositories.SampleImageRepo;
import com.unisew.server.repositories.ServiceRepo;
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

    private final PartnerRepo partnerRepo;


    private final ServiceRepo serviceRepo;

    private final PackagesRepo packagesRepo;

    private final PackageServiceRepo packageServiceRepo;

    private final DesignItemRepo designItemRepo;

    private final DesignRequestRepo designRequestRepo;

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

            }
        };
    }
}
