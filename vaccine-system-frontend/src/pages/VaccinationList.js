import React, { useEffect, useState } from 'react';
import { Table, message, Spin, Tag, Space, Button, Popconfirm, Card, Modal, Input, Select, Form } from 'antd';
import { vaccinationApi, adverseReactionApi } from '../api';
import { CheckCircleOutlined, WarningOutlined } from '@ant-design/icons';

const { Option } = Select;
const { TextArea } = Input;

function VaccinationList() {
  const [loading, setLoading] = useState(true);
  const [records, setRecords] = useState([]);
  const [reactionModalVisible, setReactionModalVisible] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchRecords();
  }, []);

  const fetchRecords = async () => {
    try {
      setLoading(true);
      const res = await vaccinationApi.getByUser(1);
      if (res.success) {
        setRecords(res.data);
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to fetch vaccination records:', error);
      message.error('获取接种记录失败');
    } finally {
      setLoading(false);
    }
  };

  const getStatusTag = (status) => {
    const statusMap = {
      PENDING: { text: '待接种', color: 'default' },
      IN_PROGRESS: { text: '接种中', color: 'blue' },
      COMPLETED: { text: '接种完成', color: 'green' },
      OBSERVING: { text: '留观中', color: 'orange' },
      OBSERVATION_COMPLETED: { text: '留观完成', color: 'green' },
      ADVERSE_REACTION_REPORTED: { text: '已报异常', color: 'red' },
      ABORTED: { text: '已中止', color: 'red' },
    };
    const { text, color } = statusMap[status] || { text: '未知', color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const handleCompleteVaccination = async (recordId) => {
    try {
      const res = await vaccinationApi.complete(recordId, '上臂三角肌');
      if (res.success) {
        message.success('接种完成，已进入留观');
        fetchRecords();
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to complete vaccination:', error);
      message.error('完成接种失败');
    }
  };

  const handleCompleteObservation = async (recordId) => {
    try {
      const res = await vaccinationApi.completeObservation(recordId);
      if (res.success) {
        message.success('留观完成');
        fetchRecords();
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to complete observation:', error);
      message.error(error.response?.data?.message || '完成留观失败');
    }
  };

  const handleReportAdverseReaction = (record) => {
    setSelectedRecord(record);
    form.setFieldsValue({
      symptoms: '',
      severity: '轻度',
    });
    setReactionModalVisible(true);
  };

  const handleReactionSubmit = async () => {
    try {
      const values = await form.validateFields();
      const data = {
        vaccinationRecordId: selectedRecord.id,
        symptoms: values.symptoms,
        severity: values.severity,
      };

      const res = await adverseReactionApi.report(data);

      if (res.success) {
        message.success('异常反应已上报');
        setReactionModalVisible(false);
        fetchRecords();
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to report adverse reaction:', error);
      message.error('上报失败');
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
      title: '记录号',
      dataIndex: 'recordNo',
      key: 'recordNo',
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
      title: '接种点',
      dataIndex: 'vaccinationPointName',
      key: 'vaccinationPointName',
    },
    {
      title: '医生',
      dataIndex: 'doctorName',
      key: 'doctorName',
    },
    {
      title: '剂次',
      dataIndex: 'doseNumber',
      key: 'doseNumber',
    },
    {
      title: '状态',
      key: 'status',
      render: (_, record) => getStatusTag(record.status),
    },
    {
      title: '接种时间',
      dataIndex: 'vaccinationTime',
      key: 'vaccinationTime',
    },
    {
      title: '留观结束时间',
      dataIndex: 'observationEndTime',
      key: 'observationEndTime',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          {record.status === 'IN_PROGRESS' && (
            <Popconfirm
              title="确定完成接种吗？"
              description="完成后将进入留观阶段"
              onConfirm={() => handleCompleteVaccination(record.id)}
              okText="确定"
              cancelText="取消"
            >
              <Button type="primary" size="small" icon={<CheckCircleOutlined />}>
                完成接种
              </Button>
            </Popconfirm>
          )}
          {record.status === 'OBSERVING' && (
            <>
              <Button
                type="primary"
                size="small"
                icon={<CheckCircleOutlined />}
                onClick={() => handleCompleteObservation(record.id)}
              >
                完成留观
              </Button>
              <Button
                danger
                size="small"
                icon={<WarningOutlined />}
                onClick={() => handleReportAdverseReaction(record)}
              >
                报异常
              </Button>
            </>
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
      <h2 className="page-header">接种记录</h2>
      
      <Card>
        <Table
          columns={columns}
          dataSource={records}
          rowKey="id"
          bordered
        />
      </Card>

      <Modal
        title="上报异常反应"
        open={reactionModalVisible}
        onOk={handleReactionSubmit}
        onCancel={() => setReactionModalVisible(false)}
      >
        <div style={{ marginBottom: 16 }}>
          <strong>接种记录：</strong>
          <div>用户：{selectedRecord?.userName}</div>
          <div>疫苗：{selectedRecord?.vaccineName}</div>
          <div>批次：{selectedRecord?.batchNumber}</div>
        </div>
        <Form form={form} layout="vertical">
          <Form.Item
            name="symptoms"
            label="症状描述"
            rules={[{ required: true, message: '请描述症状' }]}
          >
            <TextArea rows={4} placeholder="请详细描述症状" />
          </Form.Item>
          <Form.Item
            name="severity"
            label="严重程度"
            rules={[{ required: true, message: '请选择严重程度' }]}
          >
            <Select>
              <Option value="轻度">轻度</Option>
              <Option value="中度">中度</Option>
              <Option value="重度">重度</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

export default VaccinationList;
