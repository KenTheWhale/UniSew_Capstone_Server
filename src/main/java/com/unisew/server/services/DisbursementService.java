package com.unisew.server.services;

import java.time.Instant;

public interface DisbursementService {

    void disburseDue(Instant now);
}
