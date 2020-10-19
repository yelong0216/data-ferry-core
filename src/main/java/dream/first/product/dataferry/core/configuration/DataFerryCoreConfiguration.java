package dream.first.product.dataferry.core.configuration;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.yelong.core.data.string.StringDataTypeConvertorManager;
import org.yelong.core.data.string.StringDateDataTypeConvertor;
import org.yelong.core.data.string.StringTimestampDataTypeConvertor;
import org.yelong.core.model.manage.ModelManager;
import org.yelong.core.model.property.ModelProperty;

import dream.first.product.dataferry.core.data.DataObjectSourceFactory;
import dream.first.product.dataferry.core.data.attribute.DataObjectOrdinaryAttributeManager;
import dream.first.product.dataferry.core.data.attribute.impl.DefaultDataObjectOrdinaryAttributeManager;
import dream.first.product.dataferry.core.data.model.ModelDataObjectSourceFactory;
import dream.first.product.dataferry.core.data.model.convert.ModelDataObjectConvertor;
import dream.first.product.dataferry.core.data.model.convert.impl.DefaultModelDataObjectConvertor;
import dream.first.product.dataferry.core.data.model.impl.DefaultModelDataObjectSourceFactory;
import dream.first.product.dataferry.core.data.operate.DataObjectSourceOperator;
import dream.first.product.dataferry.core.data.operate.impl.DefaultDataObjectSourceOperator;
import dream.first.product.dataferry.core.ferry.DataFerry;
import dream.first.product.dataferry.core.ferry.impl.DefaultDataFerry;
import dream.first.product.dataferry.core.generate.DataFileGenerator;
import dream.first.product.dataferry.core.generate.impl.DefaultDataFileGenerator;
import dream.first.product.dataferry.core.resolve.DataFileResolver;
import dream.first.product.dataferry.core.resolve.impl.DataNodeResolver;
import dream.first.product.dataferry.core.resolve.impl.DefaultDataFileResolver;
import dream.first.product.dataferry.core.resolve.impl.DefaultDataNodeResolver;

public class DataFerryCoreConfiguration {

	// ==================================================字符串的数据类型转换器管理器==================================================

	@Bean
	public StringDataTypeConvertorManager stringDataTypeConvertorManager() {
		StringDataTypeConvertorManager stringDataTypeConvertorManager = new StringDataTypeConvertorManager();
		stringDataTypeConvertorManager.registerDataTypeConvertor(Date.class, new StringDateDataTypeConvertor());
		stringDataTypeConvertorManager.registerDataTypeConvertor(Timestamp.class,
				new StringTimestampDataTypeConvertor());
		return stringDataTypeConvertorManager;
	}

	// ==================================================数据对象源工厂==================================================

	@Bean
	public ModelDataObjectSourceFactory modelDataObjectSourceFactory() {
		return new DefaultModelDataObjectSourceFactory();
	}

	// ==================================================数据文件解析器==================================================

	@Bean
	public DataNodeResolver dataNodeResolver(DataObjectSourceFactory dataObjectSourceFactory,
			StringDataTypeConvertorManager stringDataTypeConvertorManager) {
		return new DefaultDataNodeResolver(dataObjectSourceFactory, stringDataTypeConvertorManager);
	}

	@Bean
	public DataFileResolver dataFileResolver(DataNodeResolver dataNodeResolver) {
		return new DefaultDataFileResolver(dataNodeResolver);
	}

	// ==================================================数据文件生成器==================================================

	@Bean
	public DataFileGenerator dataFileGenerator(StringDataTypeConvertorManager stringDataTypeConvertorManager) {
		return new DefaultDataFileGenerator(stringDataTypeConvertorManager);
	}

	// ==================================================模型数据对象转换器==================================================

	@Bean
	public ModelDataObjectConvertor modelDataObjectConvertor(ModelManager modelManager, ModelProperty modelProperty,
			ModelDataObjectSourceFactory modelDataObjectSourceFactory) {
		return new DefaultModelDataObjectConvertor(modelManager, modelProperty, modelDataObjectSourceFactory);
	}

	// ==================================================数据对象源操作器==================================================

	@Bean
	public DataObjectOrdinaryAttributeManager dataObjectOrdinaryAttributeManager() {
		return new DefaultDataObjectOrdinaryAttributeManager();
	}

	@Bean
	public DataObjectSourceOperator dataObjectSourceOperator(
			DataObjectOrdinaryAttributeManager dataObjectOrdinaryAttributeManager) {
		return new DefaultDataObjectSourceOperator(dataObjectOrdinaryAttributeManager);
	}

	// ==================================================数据摆渡==================================================

	@Bean
	public DataFerry defaultDataFerry(DataFileResolver dataFileResolver,
			DataObjectSourceOperator dataObjectSourceOperator) {
		return new DefaultDataFerry(dataFileResolver, dataObjectSourceOperator);
	}

}
