package dream.first.product.dataferry.core.resolve.xml;

import org.w3c.dom.Node;

import dream.first.product.dataferry.core.data.DataObjectSource;
import dream.first.product.dataferry.core.resolve.DataFileResolveException;

/**
 * 默认的数据文件解析器
 */
public class DefaultDataFileResolver extends AbstractDataFileResolver {

	protected final DataNodeResolver dataNodeResolver;

	public DefaultDataFileResolver(DataNodeResolver dataNodeResolver) {
		this.dataNodeResolver = dataNodeResolver;
	}

	@Override
	protected DataObjectSource resolveToDataObjectSource(Node node) throws DataFileResolveException {
		return dataNodeResolver.resolve(node);
	}

}
