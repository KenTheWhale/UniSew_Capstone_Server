package com.unisew.server.services.implementors;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.Designer;
import com.unisew.server.models.Partner;
import com.unisew.server.models.Profile;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.DesignerRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.ProfileRepo;
import com.unisew.server.requests.CreateProfileRequest;
import com.unisew.server.requests.LoginRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AuthService;
import com.unisew.server.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthImpl implements AuthService {

    private final AccountRepo accountRepo;

    private final ProfileRepo profileRepo;

    private final PartnerRepo partnerRepo;

    private final DesignerRepo designerRepo;

    @Value("${google.client_id}")
    private String clientId;

    @Value("${google.redirect_uri}")
    private String redirectUri;

    @Value("${google.response_type}")
    private String responseType;

    @Value("${google.scope}")
    private String scope;

    @Override
    public ResponseEntity<ResponseObject> getGoogleUrl() {
        Map<String, Object> data = new HashMap<>();
        String url = "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=" + responseType
                + "&scope=" + scope;
        data.put("url", url);

        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    @Override
    public ResponseEntity<ResponseObject> login(LoginRequest request) {
        Account account = accountRepo.findByEmail(request.getEmail()).orElse(null);
        if (account == null) {
            return createAccount(request);
        }
        Map<String, Object> data = buildAccountResponse(account);
        data.put("profile", getProfileInfo(account));

        return ResponseBuilder.build(HttpStatus.OK, "Login successfully", data);
    }

    private ResponseEntity<ResponseObject> createAccount(LoginRequest request) {
        Account account = accountRepo.save(
                Account.builder()
                        .registerDate(LocalDate.now())
                        .email(request.getEmail())
                        .role(Role.SCHOOL)
                        .status(Status.ACCOUNT_ACTIVE)
                        .build()
        );

        Map<String, Object> data = buildAccountResponse(account);
        data.put("profile", createProfile(request, account));

        return ResponseBuilder.build(HttpStatus.OK, "Login successfully", data);
    }

    public Map<String, Object> createProfile(LoginRequest request, Account account) {
        Profile profile = profileRepo.save(
                Profile.builder()
                        .account(account)
                        .avatar(request.getAvatar())
                        .name(request.getName())
                        .phone("N/A")
                        .build()
        );

        Map<String, Object> profileData = buildProfileResponse(profile);
        if(account.getRole().getValue().equalsIgnoreCase("designer")){
            profileData.put("designer", createDesignerByProfile(profile));
        }else {
            profileData.put("partner", createPartnerByProfile(profile));
        }

        return profileData;
    }

    private Map<String, Object> createPartnerByProfile(Profile profile) {
        Partner partner = partnerRepo.save(
                Partner.builder()
                        .profile(profile)
                        .district("N/A")
                        .province("N/A")
                        .street("N/A")
                        .ward("N/A")
                        .build()
        );

        return buildPartnerResponse(partner);
    }

    private Map<String, Object> createDesignerByProfile(Profile profile) {
        Designer designer = designerRepo.save(
                Designer.builder()
                        .profile(profile)
                        .bio("N/A")
                        .shortPreview("N/A")
                        .build()
        );

        return buildDesignerResponse(designer);
    }

    public Map<String, Object> getProfileInfo(Account account) {
        Profile profile = account.getProfile();
        if (profile == null) {
            return null;
        }

        Map<String, Object> profileData = buildProfileResponse(profile);
        if(profile.getDesigner() != null && profile.getPartner() == null){
            profileData.put("designer", buildDesignerResponse(profile.getDesigner()));
        }

        if(profile.getDesigner() == null && profile.getPartner() != null) {
            profileData.put("partner", buildPartnerResponse(profile.getPartner()));
        }

        return profileData;
    }



    private Map<String, Object> buildAccountResponse(Account account) {
        Map<String, Object> accountData = new HashMap<>();
        accountData.put("id", account.getId());
        accountData.put("email", account.getEmail());
        accountData.put("role", account.getRole().getValue().toLowerCase());
        accountData.put("status", account.getStatus().getValue().toLowerCase());
        accountData.put("registerDate", account.getRegisterDate());
        return accountData;
    }

    private Map<String, Object> buildProfileResponse(Profile profile) {
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("id", profile.getId());
        profileData.put("name", profile.getName());
        profileData.put("avatar", profile.getAvatar());
        profileData.put("phone", profile.getPhone());

        return profileData;
    }

    private Map<String, Object> buildDesignerResponse(Designer designer) {
        Map<String, Object> designerData = new HashMap<>();
        designerData.put("id", designer.getId());
        designerData.put("bio", designer.getBio());
        designerData.put("shortPreview", designer.getShortPreview());
        return designerData;
    }

    private Map<String, Object> buildPartnerResponse(Partner partner) {
        Map<String, Object> partnerData = new HashMap<>();
        partnerData.put("id", partner.getId());
        partnerData.put("street", partner.getStreet());
        partnerData.put("ward", partner.getWard());
        partnerData.put("district", partner.getDistrict());
        partnerData.put("province", partner.getProvince());
        return partnerData;
    }
}
