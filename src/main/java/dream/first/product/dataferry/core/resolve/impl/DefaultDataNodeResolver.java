package dream.first.product.dataferry.core.resolve.impl;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.yelong.core.annotation.Nullable;
import org.yelong.core.data.DataTypeConvertException;
import org.yelong.core.data.DataTypeConvertor;
import org.yelong.core.data.string.StringDataTypeConvertorManager;

import dream.first.product.dataferry.core.constants.NodeNameTool;
import dream.first.product.dataferry.core.data.DataObject;
import dream.first.product.dataferry.core.data.DataObjectOrdinaryAttribute;
import dream.first.product.dataferry.core.data.DataObjectSource;
import dream.first.product.dataferry.core.data.DataObjectSourceFactory;

/**
 * 默认的数据节点解析器
 */
public class DefaultDataNodeResolver implements DataNodeResolver {

	protected DataObjectSourceFactory dataObjectSourceFactory;

	protected StringDataTypeConvertorManager stringDataTypeConvertorManager;

	public DefaultDataNodeResolver(DataObjectSourceFactory dataObjectSourceFactory,
			StringDataTypeConvertorManager stringDataTypeConvertorManager) {
		this.dataObjectSourceFactory = dataObjectSourceFactory;
		this.stringDataTypeConvertorManager = stringDataTypeConvertorManager;
	}

	@Override
	public DataObjectSource resolve(Node dataNode) throws DataNodeResolveException {
		// 子节点只有为元素时才会解析
		if (Node.ELEMENT_NODE != dataNode.getNodeType()) {
			return null;
		}
		DataObjectSource dataObjectSource = dataObjectSourceFactory.create();

		Element dataElement = (Element) dataNode;

		// 对象表名
		dataObjectSource.setTableName(dataElement.getNodeName());
		// 对象主键
		dataObjectSource.setPrimaryKey(dataElement.getAttribute(NodeNameTool.ATTR_PRIMARYKEY));
		// 数据操作类型
		String dataObjectOperationType = dataElement.getAttribute(NodeNameTool.ATTR_DATAOPERATIONTYPE);
		try {
			dataObjectSource.setDataObjectOperationType(dataObjectOperationType);
		} catch (Exception e) {
			throw new DataNodeResolveException("未知的数据操作类型：" + dataObjectOperationType, e);
		}

		// 解析所有的Row节点为数据对象
		NodeList dataElementChildNodes = dataElement.getChildNodes();
		for (int i = 0; i < dataElementChildNodes.getLength(); i++) {
			DataObject dataObject = resolveRowNode(dataElementChildNodes.item(i), dataObjectSource);
			if (null != dataObject) {
				dataObjectSource.addDataObject(dataObject);
			}
		}

		return dataObjectSource;
	}

	/**
	 * 解析行节点
	 * 
	 * @param rowNode          行节点
	 * @param dataObjectSource 数据对象源
	 * @return 数据对象
	 * @throws DataNodeResolveException 数据节点解析异常
	 */
	@Nullable
	public DataObject resolveRowNode(Node rowNode, DataObjectSource dataObjectSource) throws DataNodeResolveException {
		// 子节点只有为元素时才会解析
		if (Node.ELEMENT_NODE != rowNode.getNodeType()) {
			return null;
		}
		DataObject dataObject = dataObjectSource.createDataObject();

		// 行节点中所有子元素分为 对象元素和属性元素。这个由元素的类型区分
		NodeList rowChildNodes = rowNode.getChildNodes();
		for (int i = 0; i < rowChildNodes.getLength(); i++) {
			Node node = rowChildNodes.item(i);
			// 只有是一个元素的时候才能解析
			if (Node.ELEMENT_NODE != node.getNodeType()) {
				continue;
			}
			Element nodeElement = (Element) node;
			// 包含元素子节点则为对象元素。否则为属性元素
			if (childNodeContainElementNode(node)) {
				DataObjectSource childDataObjectSource = resolve(node);
				if (null != childDataObjectSource) {
					dataObject.addDataObjectSourceAttribute(childDataObjectSource);
				}
			} else {
				DataObjectOrdinaryAttribute attribute = dataObject.addOrdinaryAttribute(node.getNodeName());
				// 获取引用值
				String reference = nodeElement.getAttribute(NodeNameTool.ATTR_REF);
				attribute.setReference(reference);

				// 解析数值值
				String textContent = nodeElement.getTextContent();
				String javaTypeStr = nodeElement.getAttribute(NodeNameTool.ATTR_JAVATYPE);
				Class<?> javaType = null;
				if (StringUtils.isBlank(javaTypeStr)) {
					javaType = String.class;
				} else {
					try {
						javaType = ClassUtils.getClass(javaTypeStr);
					} catch (ClassNotFoundException e) {
						throw new DataNodeResolveException(
								"属性元素“" + node.getNodeName() + "”指定的JavaType(" + javaTypeStr + ")不符合规范", e);
					}
				}
				// 如果属性值为空字符且属性类型不是String则修改值为null
				if (StringUtils.isEmpty(textContent)) {
					if (javaType != String.class) {
						textContent = null;
					}
				}

				Object value;
				DataTypeConvertor<String, ?> dataTypeConvertor = stringDataTypeConvertorManager
						.getDataTypeConvertor(javaType);
				if (null == dataTypeConvertor) {
					value = textContent;
				} else {
					try {
						value = dataTypeConvertor.convert(textContent);
					} catch (DataTypeConvertException e) {
						throw new DataNodeResolveException(
								"属性元素“" + node.getNodeName() + "”值(" + textContent + ")转换为JavaType(" + javaType + ")异常",
								e);
					}
				}
				attribute.setValue(value);
				attribute.setValueType(javaType);
			}
		}
		return dataObject;
	}

	/**
	 * 节点的子节点中是否存在元素节点
	 * 
	 * @param node 节点
	 * @return <code>true</code> 节点的子节点中存在元素节点
	 */
	protected boolean childNodeContainElementNode(Node node) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (Node.ELEMENT_NODE == childNode.getNodeType()) {
				return true;
			}
		}
		return false;
	}

}