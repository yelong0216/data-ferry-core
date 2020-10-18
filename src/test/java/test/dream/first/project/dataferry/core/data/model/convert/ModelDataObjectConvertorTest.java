package test.dream.first.project.dataferry.core.data.model.convert;

import java.io.IOException;
import java.util.Date;

import org.yelong.commons.io.FileUtilsE;
import org.yelong.commons.util.Dates;
import org.yelong.core.data.string.StringDataTypeConvertorManager;
import org.yelong.core.data.string.StringDateDataTypeConvertor;
import org.yelong.core.jdbc.dialect.Dialects;
import org.yelong.core.model.ModelConfigurationBuilder;
import org.yelong.core.model.manage.ModelManager;

import dream.first.base.platform.user.model.BaseUser;
import dream.first.product.dataferry.core.data.model.ModelDataObject;
import dream.first.product.dataferry.core.data.model.ModelDataObjectSource;
import dream.first.product.dataferry.core.data.model.convert.ModelDataObjectConvertException;
import dream.first.product.dataferry.core.data.model.convert.ModelDataObjectConvertor;
import dream.first.product.dataferry.core.data.model.convert.impl.DefaultModelDataObjectConvertor;
import dream.first.product.dataferry.core.generate.DataFileGenerateException;
import dream.first.product.dataferry.core.generate.DataFileGenerator;
import dream.first.product.dataferry.core.generate.impl.DefaultDataFileGenerator;

public class ModelDataObjectConvertorTest {

	static StringDataTypeConvertorManager stringDataTypeConvertorManager = new StringDataTypeConvertorManager();

	static {
		stringDataTypeConvertorManager.registerDataTypeConvertor(Date.class, new StringDateDataTypeConvertor());
	}
	
	static DataFileGenerator dataFileGenerator = new DefaultDataFileGenerator(stringDataTypeConvertorManager);

	static ModelDataObjectConvertor modelDataObjectConvertor;

	static {
		ModelManager modelManager = ModelConfigurationBuilder.create(Dialects.MYSQL.getDialect()).build()
				.getModelManager();
		modelDataObjectConvertor = new DefaultModelDataObjectConvertor(modelManager, null, null);
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args)
			throws ModelDataObjectConvertException, DataFileGenerateException, IOException {
		BaseUser user = new BaseUser();
		user.setId("123456");
		user.setCreateTime(Dates.nowDate());
		ModelDataObjectSource<BaseUser> dataObjectSource = modelDataObjectConvertor.toDataObjectSource(BaseUser.class);
		ModelDataObject<BaseUser> dataObject = modelDataObjectConvertor.toDataObject(user, dataObjectSource);
		dataObjectSource.addDataObject(dataObject);
		dataFileGenerator.generate(dataObjectSource, FileUtilsE.createNewFileOverride("K:\\user.xml"));
	}

}
