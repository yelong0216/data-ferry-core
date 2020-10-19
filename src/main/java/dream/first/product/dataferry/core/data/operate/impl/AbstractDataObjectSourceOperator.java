package dream.first.product.dataferry.core.data.operate.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.yelong.core.jdbc.sql.attribute.AttributeSqlFragment;
import org.yelong.core.jdbc.sql.condition.ConditionalOperator;
import org.yelong.core.jdbc.sql.condition.single.SingleConditionSqlFragment;
import org.yelong.core.jdbc.sql.condition.support.Condition;
import org.yelong.core.jdbc.sql.condition.support.ConditionResolver;
import org.yelong.core.jdbc.sql.executable.CountSqlFragment;
import org.yelong.core.jdbc.sql.executable.InsertSqlFragment;
import org.yelong.core.jdbc.sql.executable.UpdateSqlFragment;
import org.yelong.core.model.ModelConfiguration;
import org.yelong.core.model.service.SqlModelService;
import org.yelong.core.model.sql.ModelSqlFragmentFactory;

import dream.first.base.model.DreamFirstBaseModelable;
import dream.first.product.dataferry.core.data.DataObject;
import dream.first.product.dataferry.core.data.DataObjectOperationType;
import dream.first.product.dataferry.core.data.DataObjectOrdinaryAttribute;
import dream.first.product.dataferry.core.data.DataObjectSource;
import dream.first.product.dataferry.core.data.attribute.DataObjectGroup;
import dream.first.product.dataferry.core.data.attribute.DataObjectOrdinaryAttributeManager;
import dream.first.product.dataferry.core.data.operate.DataObjectSourceOperateException;
import dream.first.product.dataferry.core.data.operate.DataObjectSourceOperator;

/**
 * 抽象数据对象源操作器实现
 */
public abstract class AbstractDataObjectSourceOperator implements DataObjectSourceOperator {

	/**
	 * 默认的主键
	 */
	public static final String DEFAUL_PRIMARYKEY = DreamFirstBaseModelable.ID;

	private DataObjectOrdinaryAttributeManager dataObjectOrdinaryAttributeManager;

	public AbstractDataObjectSourceOperator(DataObjectOrdinaryAttributeManager dataObjectOrdinaryAttributeManager) {
		this.dataObjectOrdinaryAttributeManager = dataObjectOrdinaryAttributeManager;
	}

	@Override
	public void operate(DataObjectSource dataObjectSource, SqlModelService modelService)
			throws DataObjectSourceOperateException {
		_operate(new DataObjectGroup(), dataObjectSource, modelService);
	}

	/**
	 * 操作
	 */
	protected void _operate(DataObjectGroup dataObjectGroup, DataObjectSource dataObjectSource,
			SqlModelService modelService) throws DataObjectSourceOperateException {
		Objects.requireNonNull(dataObjectGroup);
		DataObjectSourceOperateProperties dataObjectSourceOperateProperties = new DataObjectSourceOperateProperties(
				dataObjectSource, modelService);
		DataObjectOperationType dataObjectOperationType = dataObjectSource.getDataObjectOperationType();
		List<? extends DataObject> dataObjects = dataObjectSource.getDataObjects();
		for (DataObject dataObject : dataObjects) {
			dataObjectGroup.addDataObject(dataObject);
			if (!dataObject.isEmptyOrdinaryAttribute()) {
				switch (dataObjectOperationType) {
				case INSERT:
					insert(dataObjectSourceOperateProperties, dataObjectGroup, dataObject);
					break;
				case INSERT_UPDATE:
					insertOrUpdate(dataObjectSourceOperateProperties, dataObjectGroup, dataObject);
					break;
				}
			}
			if (!dataObject.isEmptyDataObjectSourceAttribute()) {
				List<? extends DataObjectSource> dataObjectSourceAttributes = dataObject
						.getDataObjectSourceAttributes();
				for (DataObjectSource dataObjectSourceAttribute : dataObjectSourceAttributes) {
					_operate(dataObjectGroup, dataObjectSourceAttribute, modelService);
				}
			}
		}
	}

	/**
	 * 插入数据
	 * 
	 * @param dataObjectSourceOperateProperties 配置
	 * @param dataObject                        数据对象
	 */
	public void insert(DataObjectSourceOperateProperties dataObjectSourceOperateProperties,
			DataObjectGroup dataObjectGroup, DataObject dataObject) {
		beforeInsert(dataObjectSourceOperateProperties, dataObjectGroup, dataObject);
		String tableName = dataObjectSourceOperateProperties.getTableName();
		ModelSqlFragmentFactory modelSqlFragmentFactory = dataObjectSourceOperateProperties
				.getModelSqlFragmentFactory();
		AttributeSqlFragment attributeSqlFragment = setAttribute(modelSqlFragmentFactory.createAttributeSqlFragment(),
				dataObjectGroup, dataObject);
		InsertSqlFragment insertSqlFragment = dataObjectSourceOperateProperties.getModelSqlFragmentFactory()
				.createInsertSqlFragment(tableName, attributeSqlFragment);
		dataObjectSourceOperateProperties.getModelService().execute(insertSqlFragment);
	}

	/**
	 * 执行新增操作之前的操作
	 * 
	 * @param dataObjectSourceOperateProperties 数据对象源操作配置
	 * @param dataObjectGroup                   数据对象组
	 * @param dataObject                        数据对象
	 * @return 新增SQL片段
	 */
	protected void beforeInsert(DataObjectSourceOperateProperties dataObjectSourceOperateProperties,
			DataObjectGroup dataObjectGroup, DataObject dataObject) {
	}

	/**
	 * 新增或者修改数据.这根据
	 * 
	 * @param dataObjectSourceOperateProperties
	 * @param dataObject
	 */
	public void insertOrUpdate(DataObjectSourceOperateProperties dataObjectSourceOperateProperties,
			DataObjectGroup dataObjectGroup, DataObject dataObject) {
		String primaryKey = dataObjectSourceOperateProperties.getPrimaryKey();
		if (StringUtils.isBlank(primaryKey)) {
			primaryKey = DEFAUL_PRIMARYKEY;
		}
		Object idValue = dataObject.getOrdinaryAttributeValue(primaryKey);
		if (null == idValue) {
			insert(dataObjectSourceOperateProperties, dataObjectGroup, dataObject);
			return;
		}
		String tableName = dataObjectSourceOperateProperties.getTableName();
		ModelConfiguration modelConfiguration = dataObjectSourceOperateProperties.getModelConfiguration();
		SqlModelService modelService = dataObjectSourceOperateProperties.getModelService();
		ModelSqlFragmentFactory modelSqlFragmentFactory = dataObjectSourceOperateProperties
				.getModelSqlFragmentFactory();
		// 创建id = ?的条件
		Condition condition = new Condition(primaryKey, ConditionalOperator.EQUAL, idValue);
		ConditionResolver conditionResolver = modelConfiguration.getConditionResolver();
		SingleConditionSqlFragment singleConditionSqlFragment = conditionResolver.resolve(condition);
		// 创建 count SQL
		String baseCountSql = dataObjectSourceOperateProperties.getDialect().getBaseCountSql(tableName);
		CountSqlFragment countSqlFragment = modelSqlFragmentFactory.createCountSqlFragment(baseCountSql);
		countSqlFragment.setConditionSqlFragment(singleConditionSqlFragment);
		// 如果记录不存在则改为新增操作
		if (modelService.execute(countSqlFragment) == 0) {
			insert(dataObjectSourceOperateProperties, dataObjectGroup, dataObject);
			return;
		}
		beforeUpdate(dataObjectSourceOperateProperties, dataObjectGroup, dataObject);
		// 如果记录存在则根据条件修改数据
		AttributeSqlFragment attributeSqlFragment = setAttribute(modelSqlFragmentFactory.createAttributeSqlFragment(),
				dataObjectGroup, dataObject);

		// 移除ID
		attributeSqlFragment.removeAttr(primaryKey);

		UpdateSqlFragment updateSqlFragment = modelSqlFragmentFactory.createUpdateSqlFragment(tableName,
				attributeSqlFragment);
		updateSqlFragment.setConditionSqlFragment(singleConditionSqlFragment);
		modelService.execute(updateSqlFragment);
	}

	/**
	 * 执行修改操作之前的操作
	 * 
	 * @param dataObjectSourceOperateProperties 数据对象源操作配置
	 * @param dataObjectGroup                   数据对象组
	 * @param dataObject                        数据对象
	 * @return 修改SQL片段
	 */
	protected void beforeUpdate(DataObjectSourceOperateProperties dataObjectSourceOperateProperties,
			DataObjectGroup dataObjectGroup, DataObject dataObject) {
	}

	/**
	 * 设置属性
	 * 
	 * @param attributeSqlFragment 属性SQL片段
	 * @param dataObjectGroup      数据对象组
	 * @param dataObject           数据对象
	 * @return 设置后的属性
	 */
	protected AttributeSqlFragment setAttribute(AttributeSqlFragment attributeSqlFragment,
			DataObjectGroup dataObjectGroup, DataObject dataObject) {
		List<DataObjectOrdinaryAttribute> ordinaryAttributes = dataObject.getOrdinaryAttributes();
		for (DataObjectOrdinaryAttribute dataObjectOrdinaryAttribute : ordinaryAttributes) {
			Object value = dataObjectOrdinaryAttributeManager.getAttributeValue(dataObjectGroup,
					dataObjectOrdinaryAttribute);
			attributeSqlFragment.addAttr(dataObjectOrdinaryAttribute.getName(), value);
		}
		return attributeSqlFragment;
	}

}
