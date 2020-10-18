package dream.first.product.dataferry.core.data.model.impl;

import org.yelong.core.model.Modelable;

import dream.first.product.dataferry.core.data.defaults.DefaultDataObjectSourceFactory;
import dream.first.product.dataferry.core.data.model.ModelDataObjectSource;
import dream.first.product.dataferry.core.data.model.ModelDataObjectSourceFactory;

/**
 * 默认的模型对象源工厂
 */
public class DefaultModelDataObjectSourceFactory extends DefaultDataObjectSourceFactory
		implements ModelDataObjectSourceFactory {

	@Override
	public <M extends Modelable> ModelDataObjectSource<M> create(Class<M> modelClass) {
		return new DefaultModelDataObjectSource<M>(modelClass);
	}

}
