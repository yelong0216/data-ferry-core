package test.dream.first.project.dataferry.core.generate;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.yelong.commons.io.FileUtilsE;
import org.yelong.core.data.string.StringDataTypeConvertorManager;
import org.yelong.core.data.string.StringDateDataTypeConvertor;

import dream.first.product.dataferry.core.data.DataObjectSource;
import dream.first.product.dataferry.core.data.model.impl.DefaultModelDataObjectSourceFactory;
import dream.first.product.dataferry.core.generate.DataFileGenerator;
import dream.first.product.dataferry.core.generate.xml.DefaultXMLDataFileGenerator;
import dream.first.product.dataferry.core.resolve.DataFileResolver;
import dream.first.product.dataferry.core.resolve.xml.DefaultXMLDataFileResolver;
import dream.first.product.dataferry.core.resolve.xml.node.DefaultDataNodeResolver;

public class DataFileGeneratorTest {

	static StringDataTypeConvertorManager stringDataTypeConvertorManager = new StringDataTypeConvertorManager();

	static {
		stringDataTypeConvertorManager.registerDataTypeConvertor(Date.class, new StringDateDataTypeConvertor());
	}

	static DataFileResolver dataXMLResolver = new DefaultXMLDataFileResolver(
			new DefaultDataNodeResolver(new DefaultModelDataObjectSourceFactory(), stringDataTypeConvertorManager));

	static DataFileGenerator dataFileGenerator = new DefaultXMLDataFileGenerator(stringDataTypeConvertorManager);

	public static void main(String[] args) throws Exception {
		List<DataObjectSource> dataObjectSources = dataXMLResolver.resolve(new File("K:\\test.xml"));
		System.out.println(dataObjectSources.size());

		for (DataObjectSource dataObjectSource : dataObjectSources) {
			dataFileGenerator.generate(dataObjectSource,
					FileUtilsE.createNewFile("K:\\", dataObjectSource.getTableName() + ".xml"));
		}
	}

}
