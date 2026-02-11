package com.backend.dto;

import com.backend.domain.cctv.Cctv;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CctvResponseDto {
    private List<Cctv> cctvs;
    private long totalElements;
    private int totalPages;
    private int currentPage;

    public static CctvResponseDto from(Page<Cctv> page) {
        return CctvResponseDto.builder()
                .cctvs(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber() + 1) // 0-based index -> 1-based index
                .build();
    }
}