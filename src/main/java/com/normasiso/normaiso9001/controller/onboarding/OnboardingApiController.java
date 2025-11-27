package com.normasiso.normaiso9001.controller.onboarding;

import com.normasiso.normaiso9001.model.onboarding.NormaIso;
import com.normasiso.normaiso9001.repository.onboarding.NormaIsoDao;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OnboardingApiController {

    private final NormaIsoDao normaIsoDao;

    public OnboardingApiController(NormaIsoDao normaIsoDao) {
        this.normaIsoDao = normaIsoDao;
    }

    @GetMapping("/sectores/{idSector}/normas")
    public List<NormaIso> normasPorSector(@PathVariable Long idSector) {
        return normaIsoDao.findBySector(idSector);
    }
}
