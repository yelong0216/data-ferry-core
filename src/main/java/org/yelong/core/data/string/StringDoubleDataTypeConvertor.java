package org.yelong.core.data.string;

import org.apache.commons.lang3.StringUtils;
import org.yelong.core.data.DataTypeConvertException;

/**
 * 字符串数字数据类型转换器
 * 
 * @since 2.1.6
 */
public class StringDoubleDataTypeConvertor implements StringDataTypeConvertor<Double> {

	public StringDoubleDataTypeConvertor() {
	}

	@Override
	public Double convert(String sourceObject) throws DataTypeConvertException {
		if (StringUtils.isBlank(sourceObject)) {
			return null;
		}
		return Double.valueOf(sourceObject);
	}

	@Override
	public String reverseConvert(Double requiredObject) throws DataTypeConvertException {
		if (null == requiredObject) {
			return null;
		}
		return requiredObject.toString();
	}

}
