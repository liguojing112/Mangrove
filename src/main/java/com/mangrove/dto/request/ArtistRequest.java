package com.mangrove.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistRequest {

    @NotBlank(message = "本名不能为空")
    private String name;

    @NotBlank(message = "艺名不能为空")
    private String stageName;

    private String birthDate;

    private String debutDate;

    private String nationality;

    private String bio;

    private String company;

    private String avatarUrl;

    private String coverUrl;

    private Integer sortOrder;
}
