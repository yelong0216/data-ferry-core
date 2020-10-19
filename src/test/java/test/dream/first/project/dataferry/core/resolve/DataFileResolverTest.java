package test.dream.first.project.dataferry.core.resolve;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.yelong.core.data.string.StringDataTypeConvertorManager;
import org.yelong.core.data.string.StringDateDataTypeConvertor;

import dream.first.product.dataferry.core.data.DataObjectSource;
import dream.first.product.dataferry.core.data.model.impl.DefaultModelDataObjectSourceFactory;
import dream.first.product.dataferry.core.resolve.DataFileResolver;
import dream.first.product.dataferry.core.resolve.xml.DefaultXMLDataFileResolver;
import dream.first.product.dataferry.core.resolve.xml.node.DefaultDataNodeResolver;

public class DataFileResolverTest {

	static StringDataTypeConvertorManager stringDataTypeConvertorManager = new StringDataTypeConvertorManager();

	static {
		stringDataTypeConvertorManager.registerDataTypeConvertor(Date.class, new StringDateDataTypeConvertor());
	}

	static DataFileResolver dataXMLResolver = new DefaultXMLDataFileResolver(
			new DefaultDataNodeResolver(new DefaultModelDataObjectSourceFactory(), stringDataTypeConvertorManager));
	
	public static void main(String[] args) throws Exception {
		List<DataObjectSource> dataObjectSources = dataXMLResolver.resolve(new File("K:\\user.xml"));
		System.out.println(dataObjectSources.size());
		
	}

}
