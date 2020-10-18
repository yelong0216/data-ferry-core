/**
 * 
 */
package dream.first.product.dataferry.core.resolve.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.yelong.core.annotation.Nullable;

import dream.first.product.dataferry.core.constants.NodeNameTool;
import dream.first.product.dataferry.core.data.DataObjectSource;
import dream.first.product.dataferry.core.resolve.DataFileResolveException;
import dream.first.product.dataferry.core.resolve.DataFileResolver;

/**
 * 抽象的数据文件解析器
 */
public abstract class AbstractDataFileResolver implements DataFileResolver {

	/**
	 * 根元素名称
	 */
	public static final String ROOT_ELEMENT_NAME = NodeNameTool.ROOT_ELEMENT_NAME;

	@Override
	public List<DataObjectSource> resolve(File dataXML) throws DataFileResolveException {
		Document document = parseDocument(dataXML);
		Element root = document.getDocumentElement();
		
		//验证根节点是否符合规范
		isNormativeRootElement(root);
		//所有的子节点对应一个数据对象源
		NodeList rootElementChildNodes = root.getChildNodes();

		int childNodesLength = rootElementChildNodes.getLength();
		List<DataObjectSource> dataObjectSources = new ArrayList<DataObjectSource>(childNodesLength);

		for (int i = 0; i < childNodesLength; i++) {
			Node node = rootElementChildNodes.item(i);
			//解析节点为数据对象源
			DataObjectSource dataObjectSource = resolveToDataObjectSource(node);
			if (null == dataObjectSource) {
				continue;
			}
			dataObjectSources.add(dataObjectSource);
		}
		return dataObjectSources;
	}

	/**
	 * 将节点解析为数据对象源
	 * 
	 * @param node 节点
	 * @return 数据对象源
	 */
	@Nullable
	protected abstract DataObjectSource resolveToDataObjectSource(Node node) throws DataFileResolveException;

	/**
	 * 判断是否是标准的根元素
	 * 
	 * @param rootElement 根元素
	 * @throws DataFileResolveException 根元素不是一个标准的元素
	 */
	public void isNormativeRootElement(Element rootElement) throws DataFileResolveException {
		if (!ROOT_ELEMENT_NAME.equals(rootElement.getNodeName())) {
			throw new DataFileResolveException(
					"数据文件必须以“" + ROOT_ELEMENT_NAME + "”元素为根节点。而不是“" + rootElement.getNodeName() + "”");
		}
	}

	/**
	 * 解析XML文档
	 * 
	 * @param file XML文件
	 * @return XML文档对象
	 * @throws DataFileResolveException 数据文件解析异常
	 */
	public Document parseDocument(File file) throws DataFileResolveException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(file);
		} catch (Exception e) {
			throw new DataFileResolveException(e);
		}
	}

}
