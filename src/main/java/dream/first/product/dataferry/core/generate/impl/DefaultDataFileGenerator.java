package dream.first.product.dataferry.core.generate.impl;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.yelong.core.data.DataTypeConvertException;
import org.yelong.core.data.DataTypeConvertor;
import org.yelong.core.data.string.StringDataTypeConvertorManager;

import dream.first.product.dataferry.core.constants.NodeNameTool;
import dream.first.product.dataferry.core.data.DataObject;
import dream.first.product.dataferry.core.data.DataObjectOrdinaryAttribute;
import dream.first.product.dataferry.core.data.DataObjectSource;
import dream.first.product.dataferry.core.generate.DataFileGenerateException;
import dream.first.product.dataferry.core.generate.DataFileGenerator;

/**
 * 默认的数据文件生成器
 */
public class DefaultDataFileGenerator implements DataFileGenerator {

	private StringDataTypeConvertorManager stringDataTypeConvertorManager;

	public DefaultDataFileGenerator(StringDataTypeConvertorManager stringDataTypeConvertorManager) {
		this.stringDataTypeConvertorManager = stringDataTypeConvertorManager;
	}

	@Override
	public void generate(List<? extends DataObjectSource> dataObjectSources, File dateFile) throws DataFileGenerateException {

		// 创建DocumentBuilderFactory对象
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// 创建DocumentBuilder对象
		DocumentBuilder docBulider;
		try {
			docBulider = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new DataFileGenerateException(e);
		}
		// 创建Document对象，Document对象表示整个 HTML 或 XML 文档
		Document document = docBulider.newDocument();
		Element data = document.createElement(NodeNameTool.ROOT_ELEMENT_NAME);
		document.appendChild(data);

		for (DataObjectSource dataObjectSource : dataObjectSources) {
			data.appendChild(dataObjectSourceToElement(dataObjectSource, document));
		}

		// 接下来将Element对象转换成xml文档
		TransformerFactory tff = TransformerFactory.newInstance();
		try {
			Transformer tf = tff.newTransformer();
			tf.transform(new DOMSource(data), new StreamResult(dateFile));
		} catch (Exception e) {
			throw new DataFileGenerateException(e);
		}
	}

	/**
	 * 数据对象源转换为XML元素
	 * 
	 * @param dataObjectSource 数据对象源
	 * @param document         文档
	 * @return XMl元素
	 */
	@SuppressWarnings("unchecked")
	public Element dataObjectSourceToElement(DataObjectSource dataObjectSource, Document document)
			throws DataFileGenerateException {
		String tableName = dataObjectSource.getTableName();
		Element tableElement = document.createElement(tableName);
		tableElement.setAttribute(NodeNameTool.ATTR_DATAOPERATIONTYPE,
				dataObjectSource.getDataObjectOperationType().name());
		tableElement.setAttribute(NodeNameTool.ATTR_PRIMARYKEY, dataObjectSource.getPrimaryKey());

		List<? extends DataObject> dataObjects = dataObjectSource.getDataObjects();
		if (!dataObjects.isEmpty()) {
			for (DataObject dataObject : dataObjects) {
				Element rowElement = document.createElement(NodeNameTool.ROW);
				tableElement.appendChild(rowElement);
				List<DataObjectOrdinaryAttribute> ordinaryAttributes = dataObject.getOrdinaryAttributes();
				for (DataObjectOrdinaryAttribute dataObjectOrdinaryAttribute : ordinaryAttributes) {
					String attrName = dataObjectOrdinaryAttribute.getName();
					Element attrElement = document.createElement(attrName);
					rowElement.appendChild(attrElement);
					Object value = dataObjectOrdinaryAttribute.getValue();
					String reverseValue = null;
					// 根据值类型转换数据格式
					Class<?> valueType = dataObjectOrdinaryAttribute.getValueType();
					if (null != valueType) {
						attrElement.setAttribute(NodeNameTool.ATTR_JAVATYPE, valueType.getName());
						DataTypeConvertor<String, Object> dataTypeConvertor = (DataTypeConvertor<String, Object>) stringDataTypeConvertorManager
								.getDataTypeConvertor(valueType);
						if (null != dataTypeConvertor) {
							try {
								reverseValue = dataTypeConvertor.reverseConvert(value);
							} catch (DataTypeConvertException e) {
								throw new DataFileGenerateException(
										"属性元素“" + attrName + "”值(" + value + ")转换为(String)异常", e);
							}
						}
					}
					if (null == reverseValue) {
						if (null == value) {
							reverseValue = null;
						} else {
							reverseValue = value.toString();
						}
					}

					attrElement.setTextContent(reverseValue);

				}
				List<? extends DataObjectSource> dataObjectSourceAttributes = dataObject
						.getDataObjectSourceAttributes();
				for (DataObjectSource dataObjectSourceAttribute : dataObjectSourceAttributes) {
					rowElement.appendChild(dataObjectSourceToElement(dataObjectSourceAttribute, document));
				}
			}
		}
		return tableElement;
	}

}
