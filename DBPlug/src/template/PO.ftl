public class ${handleName} implements RowHandler<${className}> {

	@Override
	public ${className} handleRow(ResultSetRow row) throws Exception {
		${className} po = new ${className}();
		<#list attrs as x> 
		${x} 
		</#list> 
		
		return po;
	}
};