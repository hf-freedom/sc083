import React, { useEffect, useState } from 'react';
import { Table, message, Spin, Tag, Space, Button, Card, Popconfirm, Modal, Input, Select, Form } from 'antd';
import { vaccineApi, recallApi } from '../api';

const { Option } = Select;

function VaccineBatchList() {
  const [loading, setLoading] = useState(true);
  const [batches, setBatches] = useState([]);
  const [recallModalVisible, setRecallModalVisible] = useState(false);
  const [selectedBatch, setSelectedBatch] = useState(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchBatches();
  }, []);

  const fetchBatches = async () => {
    try {
      setLoading(true);
      const res = await vaccineApi.getAllBatches();
      if (res.success) {
        setBatches(res.data);
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to fetch batches:', error);
      message.error('获取批次列表失败');
    } finally {
      setLoading(false);
    }
  };

  const getColdChainStatusTag = (status) => {
    const statusMap = {
      NORMAL: { text: '正常', color: 'green' },
      ABNORMAL: { text: '异常', color: 'red' },
      RECALLED: { text: '已召回', color: 'orange' },
    };
    const { text, color } = statusMap[status] || { text: '未知', color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const handleRecall = (batch) => {
    setSelectedBatch(batch);
    form.setFieldsValue({
      recallReason: '',
      recallLevel: '2级',
    });
    setRecallModalVisible(true);
  };

  const handleRecallSubmit = async () => {
    try {
      const values = await form.validateFields();
      const res = await recallApi.create({
        batchId: selectedBatch.id,
        recallReason: values.recallReason,
        recallLevel: values.recallLevel,
      });

      if (res.success) {
        message.success('批次召回成功');
        setRecallModalVisible(false);
        fetchBatches();
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to recall batch:', error);
      message.error('召回失败');
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
      title: '批次号',
      dataIndex: 'batchNumber',
      key: 'batchNumber',
    },
    {
      title: '疫苗名称',
      dataIndex: 'vaccineName',
      key: 'vaccineName',
    },
    {
      title: '生产日期',
      dataIndex: 'productionDate',
      key: 'productionDate',
    },
    {
      title: '有效期',
      dataIndex: 'expirationDate',
      key: 'expirationDate',
    },
    {
      title: '总量',
      dataIndex: 'totalQuantity',
      key: 'totalQuantity',
    },
    {
      title: '可用库存',
      dataIndex: 'availableQuantity',
      key: 'availableQuantity',
      render: (text) => <span style={{ color: '#3f8600', fontWeight: 'bold' }}>{text}</span>,
    },
    {
      title: '锁定库存',
      dataIndex: 'lockedQuantity',
      key: 'lockedQuantity',
    },
    {
      title: '冷链状态',
      key: 'coldChainStatus',
      render: (_, record) => getColdChainStatusTag(record.coldChainStatus),
    },
    {
      title: '最近温度',
      key: 'temperature',
      render: (_, record) => `${record.lastTemperature}°C`,
    },
    {
      title: '召回状态',
      key: 'isRecalled',
      render: (_, record) => (
        record.isRecalled ? 
          <Tag color="orange">已召回</Tag> : 
          <Tag color="green">正常</Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          {!record.isRecalled && (
            <Popconfirm
              title="确定要召回该批次吗？"
              description="召回后该批次疫苗将不可使用"
              onConfirm={() => handleRecall(record)}
              okText="确定"
              cancelText="取消"
            >
              <Button type="link" danger>召回</Button>
            </Popconfirm>
          )}
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
      <h2 className="page-header">批次管理</h2>
      
      <Card>
        <Table
          columns={columns}
          dataSource={batches}
          rowKey="id"
          bordered
        />
      </Card>

      <Modal
        title="批次召回"
        open={recallModalVisible}
        onOk={handleRecallSubmit}
        onCancel={() => setRecallModalVisible(false)}
      >
        <div style={{ marginBottom: 16 }}>
          <strong>批次信息：</strong>
          <div>批次号：{selectedBatch?.batchNumber}</div>
          <div>疫苗名称：{selectedBatch?.vaccineName}</div>
        </div>
        <Form form={form} layout="vertical">
          <Form.Item
            name="recallReason"
            label="召回原因"
            rules={[{ required: true, message: '请输入召回原因' }]}
          >
            <Input.TextArea rows={4} placeholder="请输入召回原因" />
          </Form.Item>
          <Form.Item
            name="recallLevel"
            label="召回级别"
            rules={[{ required: true, message: '请选择召回级别' }]}
          >
            <Select>
              <Option value="1级">1级 - 严重</Option>
              <Option value="2级">2级 - 一般</Option>
              <Option value="3级">3级 - 轻微</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

export default VaccineBatchList;
