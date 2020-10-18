package dream.first.product.dataferry.core.data.defaults;

import dream.first.product.dataferry.core.data.DataObjectSource;
import dream.first.product.dataferry.core.data.DataObjectSourceFactory;

/**
 * 默认的数据对象源建造者工厂
 */
public class DefaultDataObjectSourceFactory implements DataObjectSourceFactory {

	public static final DataObjectSourceFactory INSTANCE = new DefaultDataObjectSourceFactory();

	protected DefaultDataObjectSourceFactory() {
	}

	@Override
	public DataObjectSource create() {
		return new DefaultDataObjectSource();
	}

}
