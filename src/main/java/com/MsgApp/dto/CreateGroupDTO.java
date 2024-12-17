package com.MsgApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

import lombok.Data;

@Data
public class CreateGroupDTO {
    @NotBlank(message = "Grup adı boş olamaz")
    @Size(min = 3, max = 50, message = "Grup adı 3-50 karakter arasında olmalıdır")
    private String name;

    @NotNull(message = "Grup yaratıcısı belirtilmelidir")
    private Long creatorId;

    @Size(max = 4, message = "Grup en fazla 4 üye içerebilir (yaratıcı hariç)")
    private Set<Long> memberIds;
}
