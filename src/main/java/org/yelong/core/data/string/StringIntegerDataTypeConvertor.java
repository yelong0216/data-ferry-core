package org.yelong.core.data.string;

import org.apache.commons.lang3.StringUtils;
import org.yelong.core.data.DataTypeConvertException;

/**
 * 字符串数字数据类型转换器
 * 
 * @since 2.1.6
 */
public class StringIntegerDataTypeConvertor implements StringDataTypeConvertor<Integer> {

	public StringIntegerDataTypeConvertor() {
	}

	@Override
	public Integer convert(String sourceObject) throws DataTypeConvertException {
		if (StringUtils.isBlank(sourceObject)) {
			return null;
		}
		return Integer.valueOf(sourceObject);
	}

	@Override
	public String reverseConvert(Integer requiredObject) throws DataTypeConvertException {
		if (null == requiredObject) {
			return null;
		}
		return requiredObject.toString();
	}

}
