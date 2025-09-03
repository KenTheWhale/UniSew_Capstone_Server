package com.unisew.server.services.implementors;

import com.unisew.server.models.DesignQuotation;
import com.unisew.server.models.DesignRequest;
import com.unisew.server.models.Order;
import com.unisew.server.models.Wallet;
import com.unisew.server.repositories.DesignQuotationRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.OrderRepo;
import com.unisew.server.repositories.PartnerRepo;
import com.unisew.server.repositories.WalletRepo;
import com.unisew.server.services.DisbursementService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DisbursementImpl implements DisbursementService {

    DesignRequestRepo designRequestRepo;
    OrderRepo orderRepo;
    WalletRepo walletRepo;
    DesignQuotationRepo designQuotationRepo;
    PartnerRepo partnerRepo;

    @Override
    @Transactional
    public void disburseDue(Instant now) {
        List<DesignRequest> designDue = designRequestRepo.findAll().stream()
                .filter(d -> d.getDisburseAt() != null && !d.getDisburseAt().isAfter(now.minus(1, ChronoUnit.DAYS)))
                .toList();
        for (DesignRequest dr : designDue) {
            Integer receiverId = resolveDesignerAccountId(dr);
            if (receiverId != null) {
                Wallet w = walletRepo.findByAccount_Id(receiverId);
                long amount = calcDesignReceivable(dr);
                if (amount > 0) {
                    w.setBalance(w.getBalance() + amount);
                    w.setPendingBalance(Math.max(0, w.getPendingBalance() - amount));
                    walletRepo.save(w);
                }
            }
            dr.setDisburseAt(null);
            designRequestRepo.save(dr);
        }

        List<Order> orderDue = orderRepo.findAll().stream()
                .filter(o -> o.getDisburseAt() != null && !o.getDisburseAt().isAfter(now))
                .toList();
        for (Order o : orderDue) {
            Integer payeeAccountId = resolveGarmentAccountId(o);
            if (payeeAccountId != null) {
                Wallet w = walletRepo.findByAccount_Id(payeeAccountId);
                long amount = calcOrderReceivable(o);
                if (amount > 0) {
                    w.setBalance(w.getBalance() + amount);
                    w.setPendingBalance(Math.max(0, w.getPendingBalance() - amount));
                    walletRepo.save(w);
                }
            }
            o.setDisburseAt(null);
            orderRepo.save(o);
        }
    }


    private Integer resolveDesignerAccountId(DesignRequest dr) {
        if (dr.getDesignQuotationId() == null) return null;
        return designQuotationRepo.findById(dr.getDesignQuotationId())
                .map(q -> q.getDesigner().getCustomer().getAccount().getId())
                .orElse(null);
    }

    private Integer resolveGarmentAccountId(Order o) {
        if (o.getGarmentId() == null) return null;
        return partnerRepo.findById(o.getGarmentId())
                .map(p -> p.getCustomer().getAccount().getId())
                .orElse(null);
    }

    private long calcDesignReceivable(DesignRequest dr) {
        return designQuotationRepo.findById(dr.getDesignQuotationId())
                .map(DesignQuotation::getPrice)
                .orElse(0L);
    }

    private long calcOrderReceivable(Order o) {
        long gross = o.getPrice();
        return Math.max(gross, 0L);
    }
}
