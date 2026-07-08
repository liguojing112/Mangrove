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
public class MusicRequest {

    @NotBlank(message = "歌曲名不能为空")
    private String title;

    private String singer;

    private String albumName;

    @NotBlank(message = "外链地址不能为空")
    private String externalUrl;

    private String coverUrl;

    private String lyric;

    private String description;

    private Long artistId;
}
