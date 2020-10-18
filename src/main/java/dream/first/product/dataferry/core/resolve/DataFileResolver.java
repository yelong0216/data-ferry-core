package dream.first.product.dataferry.core.resolve;

import java.io.File;
import java.util.List;

import dream.first.product.dataferry.core.data.DataObjectSource;

/**
 * 数据文件解析器
 */
public interface DataFileResolver {

	/**
	 * 解析数据文件
	 * 
	 * @param dataFile 数据文件
	 * @return 数据对象源集合
	 * @throws DataFileResolveException 数据文件解析异常
	 */
	List<DataObjectSource> resolve(File dataFile) throws DataFileResolveException;

}
