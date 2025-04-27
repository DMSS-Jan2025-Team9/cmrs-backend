package com.example.courseregistration.service.strategy;

import com.example.courseregistration.dto.CreateRegistrationDTO;
import com.example.courseregistration.dto.RegistrationDTO;
import java.util.List;

public interface RegistrationCreationStrategy {
    /**
     * @return true if this strategy should handle the given create request
     */
    boolean supports(CreateRegistrationDTO dto);

    List<RegistrationDTO> create(CreateRegistrationDTO dto);
}

