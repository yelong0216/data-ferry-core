/**
 * 
 */
package dream.first.product.dataferry.core.data.model.convert;

import java.util.ArrayList;
import java.util.List;

import org.yelong.core.model.Modelable;

import dream.first.product.dataferry.core.data.model.ModelDataObject;
import dream.first.product.dataferry.core.data.model.ModelDataObjectSource;

/**
 * 模型与数据对象转换器。模型与数据对象的相互转换
 */
public interface ModelDataObjectConvertor {

	/**
	 * 根据模型类型创建模型数据对象源
	 * 
	 * @param <M>        model type
	 * @param modelClass 模型类
	 * @return 模型数据对象源
	 * @throws ModelDataObjectConvertException 转换异常
	 */
	<M extends Modelable> ModelDataObjectSource<M> toDataObjectSource(Class<M> modelClass)
			throws ModelDataObjectConvertException;

	/**
	 * 根据模型对象、模型数据对象源创建模型数据对象
	 * 
	 * @param <M>              model type
	 * @param model            模型对象
	 * @param dataObjectSource 数据对象源
	 * @return 模型数据对象
	 * @throws ModelDataObjectConvertException 转换异常
	 */
	<M extends Modelable> ModelDataObject<M> toDataObject(M model, ModelDataObjectSource<M> dataObjectSource)
			throws ModelDataObjectConvertException;

	/**
	 * 根据模型对象、模型数据对象源创建模型数据对象
	 * 
	 * @param <M>              model type
	 * @param models           模型对象集合
	 * @param dataObjectSource 数据对象源
	 * @return 模型数据对象集合
	 * @throws ModelDataObjectConvertException 转换异常
	 */
	default <M extends Modelable> List<ModelDataObject<M>> toDataObject(List<M> models,
			ModelDataObjectSource<M> dataObjectSource) throws ModelDataObjectConvertException {
		List<ModelDataObject<M>> modelDataObjects = new ArrayList<ModelDataObject<M>>(models.size());
		for (M model : models) {
			modelDataObjects.add(toDataObject(model, dataObjectSource));
		}
		return modelDataObjects;
	}

}
