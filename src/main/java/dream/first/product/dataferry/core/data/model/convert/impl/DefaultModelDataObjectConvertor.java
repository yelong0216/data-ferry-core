package dream.first.product.dataferry.core.data.model.convert.impl;

import java.util.List;
import java.util.Objects;

import org.yelong.commons.lang.annotation.AnnotationUtilsE;
import org.yelong.core.model.Modelable;
import org.yelong.core.model.manage.FieldAndColumn;
import org.yelong.core.model.manage.ModelAndTable;
import org.yelong.core.model.manage.ModelManager;
import org.yelong.core.model.manage.exception.PrimaryKeyException;
import org.yelong.core.model.property.ModelProperty;

import dream.first.product.dataferry.core.data.model.ModelDataObject;
import dream.first.product.dataferry.core.data.model.ModelDataObjectSource;
import dream.first.product.dataferry.core.data.model.ModelDataObjectSourceFactory;
import dream.first.product.dataferry.core.data.model.convert.ModelDataObjectConvertException;
import dream.first.product.dataferry.core.data.model.convert.ModelDataObjectConvertor;
import dream.first.product.dataferry.core.data.model.convert.ModelDataObjectOperationType;

/**
 * 模型数据对象转换器
 */
public class DefaultModelDataObjectConvertor implements ModelDataObjectConvertor {

	private ModelManager modelManager;

	private ModelProperty modelProperty;

	private ModelDataObjectSourceFactory modelDataObjectSourceFactory;

	public DefaultModelDataObjectConvertor(ModelManager modelManager, ModelProperty modelProperty,
			ModelDataObjectSourceFactory modelDataObjectSourceFactory) {
		this.modelManager = Objects.requireNonNull(modelManager);
		this.modelDataObjectSourceFactory = Objects.requireNonNull(modelDataObjectSourceFactory);
		this.modelProperty = Objects.requireNonNull(modelProperty);
	}

	@Override
	public <M extends Modelable> ModelDataObjectSource<M> toDataObjectSource(Class<M> modelClass)
			throws ModelDataObjectConvertException {
		ModelAndTable modelAndTable = modelManager.getModelAndTable(modelClass);
		ModelDataObjectSource<M> modelDataObjectSource = modelDataObjectSourceFactory.create(modelClass);
		try {
			FieldAndColumn primaryKey = modelAndTable.getOnlyPrimaryKey();
			modelDataObjectSource.setPrimaryKey(primaryKey.getColumn());
		} catch (PrimaryKeyException e) {
			// ignore
		}
		modelDataObjectSource.setTableName(modelAndTable.getTableName());
		ModelDataObjectOperationType modelDataObjectOperationType = AnnotationUtilsE.getAnnotation(modelClass,
				ModelDataObjectOperationType.class, true);
		if (null != modelDataObjectOperationType) {
			modelDataObjectSource.setDataObjectOperationType(modelDataObjectOperationType.value());
		}
		return modelDataObjectSource;
	}

	@Override
	public <M extends Modelable> ModelDataObject<M> toDataObject(M model, ModelDataObjectSource<M> dataObjectSource)
			throws ModelDataObjectConvertException {
		ModelDataObject<M> modelDataObject = dataObjectSource.createModelDataObject(model);
		ModelAndTable modelAndTable = modelManager.getModelAndTable(model.getClass());
		List<FieldAndColumn> normalFieldAndColumns = modelAndTable.getNormalFieldAndColumns();
		for (FieldAndColumn fieldAndColumn : normalFieldAndColumns) {
			Object value = modelProperty.get(model, fieldAndColumn.getFieldName());
			modelDataObject.addOrdinaryAttribute(fieldAndColumn.getColumn(), value, fieldAndColumn.getFieldType());
		}
		return modelDataObject;
	}

}
