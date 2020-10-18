package dream.first.product.dataferry.core.data.attribute;

import org.apache.commons.lang3.StringUtils;

import dream.first.product.dataferry.core.data.DataObjectOrdinaryAttribute;

/**
 *
 */
public interface DataObjectOrdinaryAttributeManager {

	Object getAttributeReferenceValue(DataObjectGroup dataObjectGroup,
			DataObjectOrdinaryAttribute dataObjectOrdinaryAttribute) throws OrdinaryAttributeReferenceException;

	default Object getAttributeValue(DataObjectGroup dataObjectGroup,
			DataObjectOrdinaryAttribute dataObjectOrdinaryAttribute) throws OrdinaryAttributeReferenceException{
		String reference = dataObjectOrdinaryAttribute.getReference();
		if (StringUtils.isBlank(reference)) {
			return dataObjectOrdinaryAttribute.getValue();
		}
		return getAttributeReferenceValue(dataObjectGroup, dataObjectOrdinaryAttribute);
	}

}
