package dream.first.product.dataferry.core.data.defaults;

import dream.first.product.dataferry.core.data.DataObjectSource;

/**
 * 默认的数据对象
 */
public class DefaultDataObject extends AbstractDataObject {

	private final DataObjectSource dataObjectSource;

	protected DefaultDataObject(DataObjectSource dataObjectSource) {
		this.dataObjectSource = dataObjectSource;
	}

	@Override
	public DataObjectSource getDeclaringDataObjectSource() {
		return dataObjectSource;
	}

}
