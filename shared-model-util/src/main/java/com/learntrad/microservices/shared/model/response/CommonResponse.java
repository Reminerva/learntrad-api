package com.learntrad.microservices.shared.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {
    private int status;
    private String message;
    private T data;
    private PagingResponse paging;
}
