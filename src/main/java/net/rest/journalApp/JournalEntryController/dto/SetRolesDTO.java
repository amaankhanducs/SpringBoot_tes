package net.rest.journalApp.JournalEntryController.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetRolesDTO {

    @NotBlank
    private String userName;

    @NotNull
    private List<String> role;

}
