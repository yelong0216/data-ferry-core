package dream.first.product.dataferry.core.ferry;

import java.util.List;

import dream.first.product.dataferry.core.data.DataObjectSource;

public class DataFerryResult {

	private final List<DataObjectSource> dataObjectSources;

	public DataFerryResult(List<DataObjectSource> dataObjectSources) {
		this.dataObjectSources = dataObjectSources;
	}

	public List<DataObjectSource> getDataObjectSources() {
		return dataObjectSources;
	}
	
}
