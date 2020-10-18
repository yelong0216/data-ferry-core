package dream.first.product.dataferry.core.ferry;

import java.io.File;

import org.yelong.core.model.service.SqlModelService;

import dream.first.product.dataferry.core.data.operate.DataObjectSourceOperateException;
import dream.first.product.dataferry.core.resolve.DataFileResolveException;

/**
 * 数据摆渡
 */
public interface DataFerry {

	/**
	 * 数据摆渡
	 * 
	 * @param dataFile     数据文件
	 * @param modelService 操作的数据库服务
	 * @throws DataFileResolveException         数据文件解析异常
	 * @throws DataObjectSourceOperateException 数据库操作异常
	 * @return 数据摆渡结果
	 */
	DataFerryResult ferry(File dataFile, SqlModelService modelService)
			throws DataFileResolveException, DataObjectSourceOperateException;

}
