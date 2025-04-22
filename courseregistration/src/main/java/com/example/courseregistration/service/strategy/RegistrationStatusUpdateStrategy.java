package com.example.courseregistration.service.strategy;

import com.example.courseregistration.dto.UpdateRegistrationStatusDTO;
import com.example.courseregistration.dto.RegistrationDTO;
import java.util.List;


public interface RegistrationStatusUpdateStrategy {
    boolean supports(UpdateRegistrationStatusDTO dto);
    List<RegistrationDTO> update(UpdateRegistrationStatusDTO dto);
}