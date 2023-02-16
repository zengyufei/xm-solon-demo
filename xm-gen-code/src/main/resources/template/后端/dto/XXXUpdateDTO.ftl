package ${t.packageName}.${t.moduleName}.dto;

import ${t.packageName}.${t.moduleName}.entity.${t.entityName};
import ${t.packageName}.${t.moduleName}.entity.${t.entityName}DO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ${t.entityName}UpdateDTO extends ${t.entityName}DO<${t.entityName}> {

    @NotNull
    @NotBlank
    private String id;

}
