package ${t.packageName}.${t.moduleName}.dto;

import ${t.packageName}.${t.moduleName}.entity.${t.entityName};
import ${t.packageName}.${t.moduleName}.entity.${t.entityName}DO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ${t.entityName}GetPageDTO extends ${t.entityName}DO<${t.entityName}> {


}
