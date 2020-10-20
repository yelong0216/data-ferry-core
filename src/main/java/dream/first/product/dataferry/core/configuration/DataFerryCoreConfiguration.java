package dream.first.product.dataferry.core.configuration;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.yelong.core.data.string.StringDataTypeConvertorManager;
import org.yelong.core.data.string.StringDateDataTypeConvertor;
import org.yelong.core.data.string.StringDoubleDataTypeConvertor;
import org.yelong.core.data.string.StringIntegerDataTypeConvertor;
import org.yelong.core.data.string.StringTimestampDataTypeConvertor;
import org.yelong.core.model.manage.ModelManager;
import org.yelong.core.model.property.ModelProperty;

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
import dream.first.product.dataferry.core.resolve.DataFileResolver;

public class DataFerryCoreConfiguration {

	// ==================================================字符串的数据类型转换器管理器==================================================

	@Bean
	@ConditionalOnMissingBean
	public StringDataTypeConvertorManager stringDataTypeConvertorManager() {
		StringDataTypeConvertorManager stringDataTypeConvertorManager = new StringDataTypeConvertorManager();
		stringDataTypeConvertorManager.registerDataTypeConvertor(Date.class, new StringDateDataTypeConvertor());
		stringDataTypeConvertorManager.registerDataTypeConvertor(Timestamp.class,
				new StringTimestampDataTypeConvertor());
		stringDataTypeConvertorManager.registerDataTypeConvertor(Integer.class, new StringIntegerDataTypeConvertor());
		stringDataTypeConvertorManager.registerDataTypeConvertor(Double.class, new StringDoubleDataTypeConvertor());
		return stringDataTypeConvertorManager;
	}

	// ==================================================数据对象源工厂==================================================

	@Bean
	@ConditionalOnMissingBean
	public ModelDataObjectSourceFactory modelDataObjectSourceFactory(ModelManager modelManager) {
		return new DefaultModelDataObjectSourceFactory(modelManager);
	}

	// ==================================================模型数据对象转换器==================================================

	@Bean
	@ConditionalOnMissingBean
	public ModelDataObjectConvertor modelDataObjectConvertor(ModelManager modelManager, ModelProperty modelProperty,
			ModelDataObjectSourceFactory modelDataObjectSourceFactory) {
		return new DefaultModelDataObjectConvertor(modelManager, modelProperty, modelDataObjectSourceFactory);
	}

	// ==================================================数据对象源操作器==================================================

	@Bean
	@ConditionalOnMissingBean
	public DataObjectOrdinaryAttributeManager dataObjectOrdinaryAttributeManager() {
		return new DefaultDataObjectOrdinaryAttributeManager();
	}

	@Bean
	@ConditionalOnMissingBean
	public DataObjectSourceOperator dataObjectSourceOperator(
			DataObjectOrdinaryAttributeManager dataObjectOrdinaryAttributeManager) {
		return new DefaultDataObjectSourceOperator(dataObjectOrdinaryAttributeManager);
	}

	// ==================================================数据摆渡==================================================

	@Bean
	@ConditionalOnMissingBean
	public DataFerry dataFerry(DataFileResolver dataFileResolver, DataObjectSourceOperator dataObjectSourceOperator) {
		return new DefaultDataFerry(dataFileResolver, dataObjectSourceOperator);
	}

}
