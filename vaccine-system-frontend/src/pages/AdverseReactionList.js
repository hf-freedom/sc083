import React, { useEffect, useState } from 'react';
import { Table, message, Spin, Tag, Space, Button, Card, Modal, Select, Input, Form } from 'antd';
import { adverseReactionApi } from '../api';
import { EditOutlined } from '@ant-design/icons';

const { Option } = Select;
const { TextArea } = Input;

function AdverseReactionList() {
  const [loading, setLoading] = useState(true);
  const [reactions, setReactions] = useState([]);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [selectedReaction, setSelectedReaction] = useState(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchReactions();
  }, []);

  const fetchReactions = async () => {
    try {
      setLoading(true);
      const res = await adverseReactionApi.getAll();
      if (res.success) {
        setReactions(res.data);
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to fetch adverse reactions:', error);
      message.error('获取异常反应列表失败');
    } finally {
      setLoading(false);
    }
  };

  const getStatusTag = (status) => {
    const statusMap = {
      REPORTED: { text: '已上报', color: 'red' },
      IN_PROCESS: { text: '处理中', color: 'orange' },
      RESOLVED: { text: '已解决', color: 'green' },
      ESCALATED: { text: '已升级', color: 'red' },
    };
    const { text, color } = statusMap[status] || { text: '未知', color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const getSeverityTag = (severity) => {
    const colorMap = {
      '轻度': 'green',
      '中度': 'orange',
      '重度': 'red',
    };
    return <Tag color={colorMap[severity] || 'default'}>{severity}</Tag>;
  };

  const handleEdit = (reaction) => {
    setSelectedReaction(reaction);
    form.setFieldsValue({
      status: reaction.status,
      treatmentMeasures: reaction.treatmentMeasures || '',
      handler: reaction.handler || '',
      remark: reaction.remark || '',
    });
    setEditModalVisible(true);
  };

  const handleEditSubmit = async () => {
    try {
      const values = await form.validateFields();
      
      const params = {
        status: values.status,
        treatmentMeasures: values.treatmentMeasures,
        handler: values.handler,
        remark: values.remark,
      };

      const res = await adverseReactionApi.update(selectedReaction.id, params);

      if (res.success) {
        message.success('更新成功');
        setEditModalVisible(false);
        fetchReactions();
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to update reaction:', error);
      message.error('更新失败');
    }
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '报告号',
      dataIndex: 'reportNo',
      key: 'reportNo',
    },
    {
      title: '用户',
      dataIndex: 'userName',
      key: 'userName',
    },
    {
      title: '疫苗名称',
      dataIndex: 'vaccineName',
      key: 'vaccineName',
    },
    {
      title: '批次号',
      dataIndex: 'batchNumber',
      key: 'batchNumber',
    },
    {
      title: '症状',
      dataIndex: 'symptoms',
      key: 'symptoms',
      ellipsis: true,
    },
    {
      title: '严重程度',
      key: 'severity',
      render: (_, record) => getSeverityTag(record.severity),
    },
    {
      title: '反应时间',
      dataIndex: 'reactionTime',
      key: 'reactionTime',
    },
    {
      title: '报告时间',
      dataIndex: 'reportTime',
      key: 'reportTime',
    },
    {
      title: '状态',
      key: 'status',
      render: (_, record) => getStatusTag(record.status),
    },
    {
      title: '处理人',
      dataIndex: 'handler',
      key: 'handler',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            处理
          </Button>
        </Space>
      ),
    },
  ];

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div>
      <h2 className="page-header">异常反应列表</h2>
      
      <Card>
        <Table
          columns={columns}
          dataSource={reactions}
          rowKey="id"
          bordered
        />
      </Card>

      <Modal
        title="处理异常反应"
        open={editModalVisible}
        onOk={handleEditSubmit}
        onCancel={() => setEditModalVisible(false)}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="status"
            label="状态"
            rules={[{ required: true, message: '请选择状态' }]}
          >
            <Select>
              <Option value="IN_PROCESS">处理中</Option>
              <Option value="RESOLVED">已解决</Option>
              <Option value="ESCALATED">已升级</Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="treatmentMeasures"
            label="处理措施"
          >
            <TextArea rows={3} placeholder="请填写处理措施" />
          </Form.Item>
          <Form.Item
            name="handler"
            label="处理人"
          >
            <Input placeholder="请填写处理人" />
          </Form.Item>
          <Form.Item
            name="remark"
            label="备注"
          >
            <TextArea rows={2} placeholder="请填写备注" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

export default AdverseReactionList;
