package com.athletiq.backend.form.controller;

import com.athletiq.backend.form.entity.Form;
import com.athletiq.backend.form.entity.FormVersion;
import com.athletiq.backend.form.entity.FormField;
import com.athletiq.backend.form.service.FormService;
import com.athletiq.backend.form.service.FormVersionService;
import com.athletiq.backend.form.service.FormFieldService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/form")
public class FormController {

    private final FormService formService;
    private final FormVersionService formVersionService;
    private final FormFieldService formFieldService;

    public FormController(
            FormService formService,
            FormVersionService formVersionService,
            FormFieldService formFieldService
    ) {
        this.formService = formService;
        this.formVersionService = formVersionService;
        this.formFieldService = formFieldService;
    }

    @PostMapping
    public ResponseEntity<Form> createForm(
            @PathVariable Long eventId,
            @RequestParam Long organizerId
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        formService.createForm(
                                eventId,
                                organizerId
                        )
                );
    }

    @GetMapping
    public ResponseEntity<Form> getForm(
            @PathVariable Long eventId,
            @RequestParam Long organizerId
    ) {

        return ResponseEntity.ok(
                formService.getFormForOrganizer(
                        eventId,
                        organizerId
                )
        );
    }

    @GetMapping("/versions")
    public ResponseEntity<List<FormVersion>> getVersions(
            @PathVariable Long eventId,
            @RequestParam Long organizerId
    ) {

        return ResponseEntity.ok(
                formVersionService.getVersions(
                        eventId,
                        organizerId
                )
        );
    }

    @PostMapping("/versions")
    public ResponseEntity<FormVersion> createVersion(
            @PathVariable Long eventId,
            @RequestParam Long organizerId
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        formVersionService.createVersion(
                                eventId,
                                organizerId
                        )
                );
    }

    @PostMapping(
            "/versions/{versionId}/clone"
    )
    public ResponseEntity<FormVersion> cloneVersion(
            @PathVariable Long eventId,
            @PathVariable Long versionId,
            @RequestParam Long organizerId
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        formVersionService.cloneVersion(
                                eventId,
                                versionId,
                                organizerId
                        )
                );
    }

    @PostMapping(
            "/versions/{versionId}/publish"
    )
    public ResponseEntity<FormVersion> publishVersion(
            @PathVariable Long eventId,
            @PathVariable Long versionId,
            @RequestParam Long organizerId
    ) {

        return ResponseEntity.ok(
                formVersionService.publishVersion(
                        eventId,
                        versionId,
                        organizerId
                )
        );
    }

    @GetMapping(
            "/versions/{versionId}/fields"
    )
    public ResponseEntity<List<FormField>> getFields(
            @PathVariable Long eventId,
            @PathVariable Long versionId,
            @RequestParam Long organizerId
    ) {

        return ResponseEntity.ok(
                formFieldService.getFields(
                        eventId,
                        versionId,
                        organizerId
                )
        );
    }

    @GetMapping(
            "/versions/{versionId}/fields/{fieldId}"
    )
    public ResponseEntity<FormField> getField(
            @PathVariable Long eventId,
            @PathVariable Long versionId,
            @PathVariable Long fieldId,
            @RequestParam Long organizerId
    ) {

        return ResponseEntity.ok(
                formFieldService.getField(
                        eventId,
                        versionId,
                        fieldId,
                        organizerId
                )
        );
    }

    @PostMapping(
            "/versions/{versionId}/fields"
    )
    public ResponseEntity<FormField> addField(
            @PathVariable Long eventId,
            @PathVariable Long versionId,
            @RequestParam Long organizerId,
            @RequestBody FormField field
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        formFieldService.addField(
                                eventId,
                                versionId,
                                organizerId,
                                field
                        )
                );
    }

    @PutMapping(
            "/versions/{versionId}/fields/{fieldId}"
    )
    public ResponseEntity<FormField> updateField(
            @PathVariable Long eventId,
            @PathVariable Long versionId,
            @PathVariable Long fieldId,
            @RequestParam Long organizerId,
            @RequestBody FormField field
    ) {

        return ResponseEntity.ok(
                formFieldService.updateField(
                        eventId,
                        versionId,
                        fieldId,
                        organizerId,
                        field
                )
        );
    }

    @DeleteMapping(
            "/versions/{versionId}/fields/{fieldId}"
    )
    public ResponseEntity<Void> deleteField(
            @PathVariable Long eventId,
            @PathVariable Long versionId,
            @PathVariable Long fieldId,
            @RequestParam Long organizerId
    ) {

        formFieldService.deleteField(
                eventId,
                versionId,
                fieldId,
                organizerId
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            "/versions/{versionId}/fields/{fieldId}/duplicate"
    )
    public ResponseEntity<FormField> duplicateField(
            @PathVariable Long eventId,
            @PathVariable Long versionId,
            @PathVariable Long fieldId,
            @RequestParam Long organizerId
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        formFieldService.duplicateField(
                                eventId,
                                versionId,
                                fieldId,
                                organizerId
                        )
                );
    }
}