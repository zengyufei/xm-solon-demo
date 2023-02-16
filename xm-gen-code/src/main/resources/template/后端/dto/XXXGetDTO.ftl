package ${t.packageName}.${t.moduleName}.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ${t.entityName}GetDTO {

    @NotNull
    @NotBlank
    private String id;

}
