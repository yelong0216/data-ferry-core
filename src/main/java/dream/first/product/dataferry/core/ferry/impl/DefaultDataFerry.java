package dream.first.product.dataferry.core.ferry.impl;

import java.io.InputStream;
import java.util.List;

import org.yelong.core.model.service.SqlModelService;

import dream.first.product.dataferry.core.data.DataObjectSource;
import dream.first.product.dataferry.core.data.operate.DataObjectSourceOperateException;
import dream.first.product.dataferry.core.data.operate.DataObjectSourceOperator;
import dream.first.product.dataferry.core.ferry.DataFerry;
import dream.first.product.dataferry.core.ferry.DataFerryResult;
import dream.first.product.dataferry.core.resolve.DataFileResolveException;
import dream.first.product.dataferry.core.resolve.DataFileResolver;

/**
 * 默认的数据库摆渡实现
 */
public class DefaultDataFerry implements DataFerry {

	private DataFileResolver dataFileResolver;

	private DataObjectSourceOperator dataObjectSourceOperator;

	public DefaultDataFerry(DataFileResolver dataFileResolver, DataObjectSourceOperator dataObjectSourceOperator) {
		this.dataFileResolver = dataFileResolver;
		this.dataObjectSourceOperator = dataObjectSourceOperator;
	}

	@Override
	public DataFerryResult ferry(InputStream inputStream, SqlModelService modelService)
			throws DataFileResolveException, DataObjectSourceOperateException {
		List<DataObjectSource> dataObjectSources = dataFileResolver.resolve(inputStream);
		// 一个文件具有事务功能
		modelService.doOperation(() -> {
			for (DataObjectSource dataObjectSource : dataObjectSources) {
				dataObjectSourceOperator.operate(dataObjectSource, modelService);
			}
		});
		return new DataFerryResult(dataObjectSources);
	}

}
