package com.dhr.chapatumoney.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PublishEventRequest {
    @NotNull(message = "El campo confirmar es requerido")
    @AssertTrue(message = "Debe confirmar la publicación (confirmar = true)")
    private Boolean confirmar;
}
