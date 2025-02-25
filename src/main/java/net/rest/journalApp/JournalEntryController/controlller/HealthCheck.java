package net.rest.journalApp.JournalEntryController.controlller;

import net.rest.journalApp.JournalEntryController.payload.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck {
    @GetMapping("/health-check")
    public ResponseEntity<GenericResponse<String>> healthCheck() {
        GenericResponse<String> response = new GenericResponse<>(
                "success",
                "Health check completed",
                "Service is running properly"
        );
        return ResponseEntity.ok(response);
    }
}