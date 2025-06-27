package com.jaya.clinwarecompliance.controller;



import com.jaya.clinwarecompliance.model.OffenderRequest;
import com.jaya.clinwarecompliance.service.OffenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OffenderController {

    @Autowired
    private OffenderService offenderService;

   @PostMapping("/offender-search")
public List<Map<String, Object>> searchOffender(@RequestBody OffenderRequest request) {
    return offenderService.searchOffenders(request); // ✅ FIXED
}

}